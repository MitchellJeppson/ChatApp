package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Optional;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class ChatGUIController {

	@FXML private TextArea messageArea;
	@FXML private SplitPane splitPane;
	@FXML private AnchorPane rightPane;
	@FXML private AnchorPane leftPane;
	@FXML private TextArea leftTextArea;
	@FXML private TextField textBox;
	private BufferedReader in;
	private PrintWriter out;
	private String line = "";
	
	private static HashSet<String> names = new HashSet<String>();


	Thread t = new Thread() {
		public void run() {

			splitPane.lookupAll(".split-pane-divider").stream()
            .forEach(div ->  div.setMouseTransparent(true) );
			while (true) {
				try {
					line = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						System.out.println(line);

						if (line.startsWith("SUBMITNAME")) {
							TextInputDialog d = new TextInputDialog();
							d.initStyle(StageStyle.UNDECORATED);
							d.setHeaderText("Setup");
							d.setContentText("Please enter your chat name: ");
							Optional<String> answer = d.showAndWait();
							out.println(answer.get());
						} else if (line.startsWith("NAMEACCEPTED")) {
							textBox.setEditable(true);
						} else if(line.startsWith("JOINED ")) {
							names.add(line.substring(7));
							leftTextArea.appendText(line.substring(7)+"\n");
						}
						else if(line.startsWith("LEFT ")) {
							names.remove(line.substring(5));
							leftTextArea.setText("");
							for(String s: names) {
								leftTextArea.appendText(s + "\n");
							}
						}
						else {
							messageArea.appendText(line.substring(8) + "\n");
						}
					}

				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	};
	

	
	@FXML
	void sendButtonClicked(ActionEvent event) {
		out.println(textBox.getText());
		textBox.clear();
	}

	@FXML
	void keyboardInput(KeyEvent e){
		if(e.getCode().equals(KeyCode.ENTER)) {
			out.println(textBox.getText());
			textBox.clear();
		}
	}




	@FXML 
	void initialize() throws IOException{
				
		textBox.setEditable(false);
		messageArea.setEditable(false);

		String serverAddress = getServerAddress();
		SSLSocketFactory sslFact = (SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket s = null;
		s = (SSLSocket)sslFact.createSocket(serverAddress, 9001);
		String[] suites = s.getSupportedCipherSuites();
		s.setEnabledCipherSuites(suites);

		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);

		Main.getStage().addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				t.start();				
			}

		});
	}


	private String getServerAddress() {
		TextInputDialog d = new TextInputDialog();
		d.initStyle(StageStyle.UNDECORATED);
		d.setHeaderText("Setup");
		d.setContentText("Please enter IP address of server: ");
		d.getEditor().setText("10.50.206.16");
		Optional<String> answer = d.showAndWait();
		d.close();
		return answer.get();
	}



}
