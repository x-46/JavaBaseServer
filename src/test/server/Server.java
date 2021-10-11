package test.server;

import java.security.InvalidParameterException;

import me.x46.base.server.BaseServer;
import me.x46.base.server.ClientConnected;
import me.x46.base.server.ClientConnection;
import me.x46.base.server.ClientLogic;

public class Server {

	public static void main(String[] args) {
		BaseServer server = new BaseServer(1610);
		server.start();

		server.setKeystoreFile("certificate.p12");
		server.setKeystoreFilePassword("123");
		
		server.addClientConnectedEvent(ClientConnected.getDefaultEvent());
		
		server.addClientConnectedEvent(new ClientConnected() {
			
			@Override
			public void clientConnected(ClientConnection cc) {
			//	cc.startTLS();
			}
		});

		
		server.addClientLogic(new ClientLogic() {

			@Override
			public void input(ClientConnection cc, String message) {
				if (message.equals("ping")) {
					System.out.println("pong");
					cc.sendMessage("pong");
				}else if(message.equals("quit")) {
					cc.close();
				}

			}
		});
		
		server.addClientLogic(new ClientLogic() {
			
			@Override
			public void input(ClientConnection cc, String message) {
				if(message.startsWith("tls")) {
					System.out.println("tls");
					cc.startTLS();
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							for(int i = 0; i < 10; i++) {
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println("send hallo");
								cc.sendMessage("hallo");
							}
							
						}
					}).start();
				}
				
			}
		});

		server.addClientLogic(new ClientLogic() {

			@Override
			public void input(ClientConnection cc, String message) {

				if (message.startsWith("sn+")) {
					try {
						if (!prepareMessage(message).equals("null")) {
							cc.setRegister("name", prepareMessage(message));
						} else {
							cc.sendMessage("error");
							return;
						}
					} catch (InvalidParameterException e) {
						cc.sendMessage("error");
						return;
					}

					cc.sendMessage("ok");
				} else if (message.startsWith("gn")) {
					cc.sendMessage(cc.getRegister("name"));
				}

			}
		});

		server.addClientLogic(new ClientLogic() {

			@Override
			public void input(ClientConnection cc, String message) {
				if (message.startsWith("syn+")) {
					ClientConnection ccc = getConnectionByName(prepareMessage(message));
					ccc.sendMessage("syn " + cc.getRegister("name"));
					cc.sendMessage("ok");
					cc.setRegister("partner", ccc.getRegister("name"));
					cc.setRegister("accepted", "null");
				} else if (message.startsWith("ack+")) {
					ClientConnection ccc = getConnectionByName(prepareMessage(message));
					if (ccc.getRegister("partner").equals(cc.getRegister("name"))) {
						cc.setRegister("partner", ccc.getRegister("name"));
						cc.setRegister("accepted", ccc.getRegister("name"));
						ccc.setRegister("accepted", cc.getRegister("name"));

						cc.sendMessage("ack");
						ccc.sendMessage("ack");
					}
				} else if (message.startsWith("s+")) {
					ClientConnection ccc = getConnectionByName(cc.getRegister("partner"));
					ccc.sendMessage(cc.getRegister("name") + " > " + prepareMessage(message));
				} else if (message.startsWith("fin")) {
					ClientConnection ccc = getConnectionByName(cc.getRegister("partner"));
					ccc.setRegister("partner", "null");
					ccc.setRegister("accepted", "null");
					ccc.sendMessage("fin");

					cc.setRegister("partner", "null");
					cc.setRegister("accepted", "null");
					cc.sendMessage("fin");
				}
			}
		});
	}

	public static String prepareMessage(String message) {

		if (!message.contains("+") || message.indexOf("+") == message.length() - 1) {
			throw new InvalidParameterException();
		}

		return message.substring(message.indexOf("+") + 1);

	}

	public static ClientConnection getConnectionByName(String name) {
		for (ClientConnection cc : ClientConnection.getClientList()) {
			if (name.equals(cc.getRegister("name"))) {
				return cc;
			}
		}
		return null;
	}

}
