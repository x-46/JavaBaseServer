package test.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import me.x46.base.client.BaseClient;
import me.x46.base.client.Connected;
import me.x46.base.client.InBox;

public class Client {

	public static void main(String[] args) {
		BaseClient c = new BaseClient("x46.me", 1610, true);
		Gui g = new Gui();
		
		c.addConnectedEvent(new Connected() {
			
			@Override
			public void connected() {
				c.sendMessage("ping");
			}
		});

		c.addInBox(new InBox() {

			@Override
			public void in(String message) {
				g.getTextArea().append(message + "\n");
			}
		});

		g.getTextField().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = g.getTextField().getText();
				
				g.getTextArea().append(text + "\n");
				g.getTextField().setText("");
				
				if(text.equals("tls")) {
					c.startTLS();
					c.sendMessage("tls");
					return;
				}
				
				if(text.equals("quit")) {
					System.exit(0);
				}
				
				c.sendMessage(text);
			}
		});

		
		c.openConnection();
	}

}
