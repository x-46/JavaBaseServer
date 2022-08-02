package me.x46.base.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import me.x46.base.tls.NoCertificateException;
import me.x46.base.tls.TLSFactory;

public class ClientConnection implements Runnable {

	private Socket socket;

	private BaseServer server;

	private PrintWriter out;
	private BufferedReader in;

	private HashMap<String, String> register;

	private ArrayList<ClientLogic> clientLogic;

	private static ArrayList<ClientConnection> clientList = new ArrayList<ClientConnection>();

	protected ClientConnection(Socket socket, BaseServer server) {
		this.socket = socket;
		this.server = server;

		clientLogic = new ArrayList<ClientLogic>();

		register = new HashMap<>();

		clientList.add(this);

		setRegister("ip", socket.getInetAddress().toString().substring(1));

		try {
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		for (ClientConnected c : server.getClientConnected()) {
			c.clientConnected(this);
		}

		while (server.isRuning() && socket.isConnected()) {
			try {
				String message = in.readLine();
				if (message != null) {
					for (ClientLogic l : server.getClientLogic()) {
						l.input(this, message);
					}

					for (ClientLogic l : this.clientLogic) {
						l.input(this, message);
					}
				}
			} catch (Exception e) {
				break;
			}
		}

		for (ClientDisconnected c : server.getClientDisconnected()) {
			c.clientDisconnected(this);
		}

	}

	public void close() {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startTLS() {
		TLSFactory f = null;
		try {
			f = new TLSFactory(socket, server.getKeystoreFile(), server.getKeystoreFilePassword());
		} catch (NoCertificateException e) {
			e.printStackTrace();
		}
		socket = f.startHandshake();

		try {
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setRegister(String key, String value) {
		register.put(key, value);
	}

	public boolean registerContainsKey(String key) {
		if (register.containsKey(key)) {
			if (register.get(key) == null) {
				return false;
			}
			return true;
		}

		return false;
	}

	public String getRegister(String key) {
		if (!register.containsKey(key)) {
			return "";
		}
		return register.get(key);
	}

	public void addClientLogic(ClientLogic clientLogic) {
		this.clientLogic.add(clientLogic);
	}

	public void removeClientLogic(ClientLogic clientLogic) {
		this.clientLogic.remove(clientLogic);
	}

	public static ArrayList<ClientConnection> getClientList() {
		return clientList;
	}

	public void sendMessage(String message) {
		out.print(message + server.getSendLineEnding());
		out.flush();
	}

	protected Socket getSocket() {
		return socket;
	}


}
