/*
 * Copyright (C) 2011-2019 rhktrth
 * This software is under the terms of MIT license.
 * For details, see the web site: https://github.com/rhktrth/TcpRedirector/
 */

package com.github.rhktrth.tcpredirector;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TcpRedirector extends Thread {
	private DataTunnel inDataTunnel;
	private DataTunnel outDataTunnel;

	public TcpRedirector(Socket clientSocket, Socket serverSocket) {
		inDataTunnel = new DataTunnel(clientSocket, serverSocket);
		outDataTunnel = new DataTunnel(serverSocket, clientSocket);
	}

	public void run() {
		inDataTunnel.start();
		outDataTunnel.start();
	}

	private class DataTunnel extends Thread {
		private final static int BUFFER_SIZE = 10000;

		private InputStream inputStream;
		private BufferedOutputStream outputStream;
		private byte readBuffer[] = new byte[BUFFER_SIZE];

		public DataTunnel(Socket inputSocket, Socket outputSocket) {
			try {
				inputStream = inputSocket.getInputStream();
				outputStream = new BufferedOutputStream(outputSocket.getOutputStream(), BUFFER_SIZE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			int n = 0;
			try {
				while ((n = inputStream.read(readBuffer)) > 0) {
					outputStream.write(readBuffer, 0, n);
					if (inputStream.available() == 0) {
						outputStream.flush();
					}
				}
			} catch (IOException e) {
				// nothing to do
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					// nothing to do
				}

				if (n < 0) {
					n = 0;
				}
				try {
					outputStream.write(readBuffer, 0, n);
					outputStream.flush();
					outputStream.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
	}
}
