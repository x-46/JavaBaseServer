package me.x46.base.client;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class BaseClient {

	private String ip;
	private int port;

	private ArrayList<InBox> inBoxList;
	private ArrayList<Connected> connectedList;
	private ArrayList<Disconnected> disconnectedList;

	private ArrayList<HandshakeDone> handshakedoneList;

	private ClientIO io;

	private String sendLineEnding = "\n";

	private boolean runing;

	private boolean supportStartTLS;

	/**
	 * The following problem: To detect if the client is disconnected from the
	 * server, a readline must be called, but to perform a start TLS, no readline is
	 * allowed to block the loop. <br>
	 * The solution is to specify from the beginning if you want to use TSL. <br>
	 * As a result, if you don't want to use TSL, you can see if the server has
	 * disconnected. If you want to use TSL, you can only tell if the server
	 * disconnected when TSL is active (this is only a problem if you don't call TSL
	 * directly at the start).
	 * 
	 * 
	 * 
	 * @param ip
	 * @param port
	 * @param supportStartTLS
	 */

	public BaseClient(String ip, int port, boolean supportStartTLS) {
		this.ip = ip;
		this.port = port;
		this.supportStartTLS = supportStartTLS;

		inBoxList = new ArrayList<InBox>();
		connectedList = new ArrayList<Connected>();
		handshakedoneList = new ArrayList<HandshakeDone>();
		disconnectedList = new ArrayList<Disconnected>();
	}

	public void openConnection() {
		try {
			Socket clientSocket = new Socket(ip, port);
			runing = true;

			io = new ClientIO(clientSocket, this);
			new Thread(io).start();

		} catch (IOException e) {
			for (Disconnected d : getDisconnectedList()) {
				d.disconnect();
			}
		}

	}

	public void close() {
		inBoxList.clear();
		connectedList.clear();
		handshakedoneList.clear();
		runing = false;
		if(io != null)
			io.close();
	}

	public void addInBox(InBox b) {
		inBoxList.add(b);
	}

	public void addConnectedEvent(Connected c) {
		connectedList.add(c);
	}

	public void addDisconnectedEvent(Disconnected c) {
		disconnectedList.add(c);
	}

	public void addHandshakeDoneEvent(HandshakeDone c) {
		handshakedoneList.add(c);
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

	protected ArrayList<HandshakeDone> getHandshakedoneList() {
		return handshakedoneList;
	}

	protected ArrayList<Disconnected> getDisconnectedList() {
		return disconnectedList;
	}

	public boolean isRuning() {
		return runing;
	}

	protected boolean isSupportStartTLS() {
		return supportStartTLS;
	}

	public void startTLS() {
		io.startTLS();
	}

	public void setSendLineEnding(String sendLineEnding) {
		this.sendLineEnding = sendLineEnding;
	}

	public String getSendLineEnding() {
		return sendLineEnding;
	}
}
