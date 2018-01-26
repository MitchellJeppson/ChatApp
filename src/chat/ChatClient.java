package chat;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatClient {

	BufferedReader in;
	PrintWriter out;
	JFrame frame = new JFrame("Chatter");
	JTextField textField = new JTextField(40);
	JTextArea messageArea = new JTextArea(8, 40);

	/*
	 * Initializes chat GUI
	 */
	public ChatClient() {

		textField.setEditable(false);
		messageArea.setEditable(false);
		frame.getContentPane().add(textField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");
		frame.pack();

		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				out.println(textField.getText());
				textField.setText("");
			}
		});
	}

	
	private String getServerAddress() {
		return JOptionPane.showInputDialog(
				frame,
				"Enter IP Address of the Server:",
				"Welcome to the Chatter",
				JOptionPane.QUESTION_MESSAGE);
	}

	
	private String getName() {
		return JOptionPane.showInputDialog(
				frame,
				"Choose a screen name:",
				"Screen name selection",
				JOptionPane.PLAIN_MESSAGE);
	}

	/*
	 * Connects to server over ssl, loops over communication with server. 
	 */
	private void runChatApp() throws IOException {
		String serverAddress = getServerAddress();
		
		SSLSocketFactory sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
	    SSLSocket s = (SSLSocket)sslFact.createSocket(serverAddress, 9001);
	    String[] suites = s.getSupportedCipherSuites();
	    s.setEnabledCipherSuites(suites);

		in = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);

		// Process all messages from server, according to the protocol.
		while (true) {
			String line = in.readLine();
			if (line.startsWith("SUBMITNAME")) {
				out.println(getName());
			} else if (line.startsWith("NAMEACCEPTED")) {
				textField.setEditable(true);
			} else if (line.startsWith("MESSAGE")) {
				messageArea.append(line.substring(8) + "\n");
			}
		}
	}


	public static void main(String[] args) throws Exception {
		System.setProperty("https.protocols", "SSLv2Hello");

		ChatClient client = new ChatClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.setVisible(true);
		client.runChatApp();
	}
}