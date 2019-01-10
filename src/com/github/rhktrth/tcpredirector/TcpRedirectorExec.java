/*
 * Copyright (C) 2011-2019 rhktrth
 * This software is under the terms of MIT license.
 * For details, see the web site: https://github.com/rhktrth/TcpRedirector/
 */

package com.github.rhktrth.tcpredirector;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpRedirectorExec {
	private static final int DEFAULT_LISTEN_PORT = 8080;
	private static final String DEFAULT_DESTINATION_HOST = "localhost";
	private static final int DEFAULT_DESTINATION_PORT = 80;

	public static void main(String[] args) {
		int listenPort = DEFAULT_LISTEN_PORT;
		String destinationHost = DEFAULT_DESTINATION_HOST;
		int destinationPort = DEFAULT_DESTINATION_PORT;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			if (arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("-?")
					|| arg.equalsIgnoreCase("/?")) {
				System.err.println("Usage: TcpRedirector {options}");
				System.err.println();
				System.err.println("Options are:");
				System.err.println("  -listenport  TCP port on which TcpRedirector listens (default: 8080)");
				System.err
						.println("  -desthost    hostname to which TcpRedirector redirects data (default: localhost)");
				System.err.println("  -destport    port to which TcpRedirector redirects data (default: 80)");
				System.err.println("  -help        print this help");
				System.err.println();
				System.err.println("Example:");
				System.err.println("  Listen on port 3128 and send requests to www.example.com:80");
				System.err.println("    TcpRedirector -listenport 3128 -desthost www.example.com");
				return;
			} else if (arg.equalsIgnoreCase("-listenport")) {
				i++;
				listenPort = Integer.parseInt(args[i]);
			} else if (arg.equalsIgnoreCase("-desthost")) {
				i++;
				destinationHost = args[i];
			} else if (arg.equalsIgnoreCase("-destport")) {
				i++;
				destinationPort = Integer.parseInt(args[i]);
			} else {
				System.err.println("TcpRedirector - TCP port redirect utility");
				System.err.println();
				System.err.println("Usage: TcpRedirector -help");
				System.err.println();
				return;
			}
		}

		ServerSocket myListener = null;
		try {
			/* Open a socket of server side */
			myListener = new ServerSocket(listenPort);
			while (true) {
				/* wait a connection from client side and get a socket */
				Socket clientSocket = myListener.accept();
				/* connect the server and get a socket */
				Socket serverSocket = new Socket(destinationHost, destinationPort);

				/* create a redirector class and start the session */
				Thread t = new TcpRedirector(clientSocket, serverSocket);
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				myListener.close();
			} catch (IOException e) {
				// nothing to do
			}
		}
	}
}
