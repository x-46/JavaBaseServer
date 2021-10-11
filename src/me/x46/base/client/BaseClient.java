package me.x46.base.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class BaseClient {


	private String ip;
	private int port;

	private ArrayList<InBox> inBoxList;
	private ArrayList<Connected> connectedList;

	private ClientIO io;

	private boolean runing;

	public BaseClient(String ip, int port) {
		this.ip = ip;
		this.port = port;

		inBoxList = new ArrayList<InBox>();
		connectedList = new ArrayList<Connected>();
	}

	public void openConnection() {
		try {
			Socket clientSocket = new Socket(ip, port);
			runing = true;
			
			io = new ClientIO(clientSocket, this);
			new Thread(io).start();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void addInBox(InBox b) {
		inBoxList.add(b);
	}

	public void addConnectedEvent(Connected c) {
		connectedList.add(c);
	}

	public void sendMessage(String message) {
		io.send(message);
	}

	protected ArrayList<InBox> getInBoxList() {
		return inBoxList;
	}

	protected ArrayList<Connected> getConnectedList() {
		return connectedList;
	}

	public boolean isRuning() {
		return runing;
	}

	public void startTLS() {
		io.startTLS();
	}
}
