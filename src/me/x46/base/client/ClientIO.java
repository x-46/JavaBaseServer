package me.x46.base.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import me.x46.base.tls.TLSFactory;

public class ClientIO implements Runnable {

	private PrintWriter out;
	private BufferedReader in = null;

	private BaseClient client;
	private Socket clientSocket;

	private boolean startTLSHandshake = false;

	protected ClientIO(Socket clientSocket, BaseClient client) {

		this.client = client;
		this.clientSocket = clientSocket;

		try {
			this.out = new PrintWriter(clientSocket.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		for (Connected c : client.getConnectedList()) {
			c.connected();
		}

		while (client.isRuning() && !clientSocket.isClosed()) {
			try {

				if (startTLSHandshake) {
					System.out.println("start");
					TLSFactory f = new TLSFactory(clientSocket);
					clientSocket = f.startHandshake();

					try {
						this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						this.out = new PrintWriter(clientSocket.getOutputStream(), true);
					} catch (IOException e) {
						e.printStackTrace();
					}

					for (HandshakeDone d : client.getHandshakedoneList()) {
						d.handshake();
					}

					while (client.isRuning() && !clientSocket.isClosed()) {

						String message = in.readLine();
						if (message == null)
							break;
						for (InBox b : client.getInBoxList()) {
							b.in(message);
						}
					}

				}

				if (startTLSHandshake)
					break;

				if (!in.ready() && client.isSupportStartTLS()) {
					continue;
				}

				String message = in.readLine();

				if (message == null)
					break;

				for (InBox b : client.getInBoxList()) {
					b.in(message);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("end");

		for (Disconnected d : client.getDisconnectedList()) {
			d.disconnect();
		}

	}

	protected void startTLS() {
		startTLSHandshake = true;
		in = null;
	}

	protected void send(String msg) {
		out.print(msg + client.getSendLineEnding());
		out.flush();
	}

	public void close() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
