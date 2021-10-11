package me.x46.base.server;

public interface ClientConnected {
	public void clientConnected(ClientConnection cc);
	
	public static ClientConnected getDefaultEvent() {
		return new ClientConnected() {
			@Override
			public void clientConnected(ClientConnection cc) {
				System.out.println("new client from " + cc.getSocket().getInetAddress() + ":" + cc.getSocket().getPort());
			}
		};
	}
}
