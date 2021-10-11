package test.client;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Gui extends JFrame {

	private static final long serialVersionUID = 1973783423089647803L;
	private JTextArea textArea;
	private JTextField textField;

	public Gui() {
		textArea = new JTextArea();
		textField = new JTextField();

		setupGui();
	}

	private void setupGui() {
		setSize(500, 600);
		setTitle("Client");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				JOptionPane.showMessageDialog(null, "type \"quit\" to exit", "Exit", JOptionPane.ERROR_MESSAGE);
			}
		});

		textArea.setEditable(false);

		add(textArea, BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);

		setVisible(true);
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public JTextField getTextField() {
		return textField;
	}

}
