package me.x46.base.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BaseServer implements Runnable{

	private ServerSocket serverSocket;

	private boolean runing;
	private int port;
	
	private ArrayList<ClientLogic> clientLogic;
	
	private ArrayList<ClientConnected> clientConnected;
	
	private String keystoreFile;
	private String keystoreFilePassword;

	public BaseServer(int port) {
		this.port = port;
		
		clientLogic = new ArrayList<ClientLogic>();
		clientConnected = new ArrayList<ClientConnected>();
	}

	public void start() {
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
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
	
	public boolean isRuning() {
		return runing;
	}

	protected ArrayList<ClientLogic> getClientLogic() {
		return clientLogic;
	}

	public void addClientLogic(ClientLogic clientLogic) {
		this.clientLogic.add(clientLogic);
	}
	
	public ArrayList<ClientConnected> getClientConnected() {
		return clientConnected;
	}
	
	public void addClientConnectedEvent(ClientConnected c) {
		clientConnected.add(c);
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
	
	

	
}
