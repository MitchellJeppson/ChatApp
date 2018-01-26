package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;


public class ChatServer{

	private static final int PORT = 9001;

	private static HashSet<String> names = new HashSet<String>();

	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

	private static SSLServerSocket s;

	/*
	 * Start server, close when finished.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("The chat server is running.");
		SSLServerSocketFactory sslSrvFact = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
		s = (SSLServerSocket)sslSrvFact.createServerSocket(PORT);
		String[] suites = s.getSupportedCipherSuites();

		s.setEnabledCipherSuites(suites);
		try {
			while (true) {
				new Handler((SSLSocket)s.accept()).start();
			}
		} finally {
			s.close();
		}
	}

	/*
	 * Hanlder to handle multithreaded communicaiton with chat clients.
	 */
	private static class Handler extends Thread {
		private String name;
		private SSLSocket socket;
		private BufferedReader in;
		private PrintWriter out;

		public Handler(SSLSocket socket) throws IOException {
			this.socket = socket;
		}

		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				while (true) {
					out.println("SUBMITNAME");
					name = in.readLine();
					if (name == null) {
						return;
					}
					synchronized (names) {
						if (!names.contains(name)) {
							names.add(name);
							break;
						}
					}
				}

				out.println("NAMEACCEPTED");

				for(PrintWriter writer: writers) {
					writer.println("JOINED "+name);

				}
				writers.add(out);
				for(String s: names) {
					out.println("JOINED "+s);
				}
				
				while (true) {
					String input = in.readLine();
					if (input == null) {
						return;
					}
					for (PrintWriter writer : writers) {
						writer.println("MESSAGE " + name + ": " + input);
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {

				if (name != null) {
					names.remove(name);
					for(PrintWriter writer: writers) {
						writer.println("LEFT "+name);
					}
				}
				if (out != null) {
					writers.remove(out);
				}
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}