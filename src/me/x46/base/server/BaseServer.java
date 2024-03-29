package me.x46.base.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

public class BaseServer implements Runnable {

	private ServerSocket serverSocket;

	private boolean runing;
	private int port;

	private ArrayList<ClientLogic> clientLogic;

	private ArrayList<ClientConnected> clientConnected;
	private ArrayList<ClientDisconnected> clientDisconnected;

	private ArrayList<ClientConnection> clientList;

	private String keystoreFile;
	private String keystoreFilePassword;

	private String sendLineEnding = "\n";

	private String bindIp;

	public BaseServer(int port) {
		this.port = port;

		bindIp = null;

		clientLogic = new ArrayList<ClientLogic>();
		clientConnected = new ArrayList<ClientConnected>();
		clientDisconnected = new ArrayList<ClientDisconnected>();

		clientList = new ArrayList<ClientConnection>();
	}

	public void start() {
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			if (bindIp == null) {
				serverSocket = new ServerSocket(port);
			} else {
				serverSocket = new ServerSocket(port, 0, InetAddress.getByName(bindIp));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		runing = true;
		System.out.println("Server start at port " + port);

		while (runing) {
			try {
				Socket clientSocket = serverSocket.accept();
				new Thread(new ClientConnection(clientSocket, this)).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void stop() {
		runing = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void activateClientList() {
		addClientConnectedEvent(new ClientConnected() {

			@Override
			public void clientConnected(ClientConnection cc) {
				clientList.add(cc);

			}
		});

		addClientDisconnectedEvent(new ClientDisconnected() {

			@Override
			public void clientDisconnected(ClientConnection cc) {
				clientList.remove(cc);
			}
		});
	}

	public void bindIp(String ip) {
		this.bindIp = ip;
	}

	public boolean isRuning() {
		return runing;
	}

	protected ArrayList<ClientLogic> getClientLogic() {
		return clientLogic;
	}

	public void addClientLogic(ClientLogic clientLogic) {
		this.clientLogic.add(clientLogic);
	}

	protected ArrayList<ClientConnected> getClientConnected() {
		return clientConnected;
	}

	protected ArrayList<ClientDisconnected> getClientDisconnected() {
		return clientDisconnected;
	}

	public void addClientConnectedEvent(ClientConnected c) {
		clientConnected.add(c);
	}

	public void addClientDisconnectedEvent(ClientDisconnected c) {
		clientDisconnected.add(c);
	}

	protected String getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(String keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	protected String getKeystoreFilePassword() {
		return keystoreFilePassword;
	}

	public void setKeystoreFilePassword(String keystoreFilePassword) {
		this.keystoreFilePassword = keystoreFilePassword;
	}

	public void setSendLineEnding(String sendLineEnding) {
		this.sendLineEnding = sendLineEnding;
	}

	public String getSendLineEnding() {
		return sendLineEnding;
	}

	protected void setClientConnected(ArrayList<ClientConnected> clientConnected) {
		this.clientConnected = clientConnected;
	}

	protected void setClientDisconnected(ArrayList<ClientDisconnected> clientDisconnected) {
		this.clientDisconnected = clientDisconnected;
	}

	public ArrayList<ClientConnection> getClientList() {
		return clientList;
	}

}
