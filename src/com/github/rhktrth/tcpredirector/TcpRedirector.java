/*
 * Copyright (C) 2011-2019 rhktrth
 * This software is under the terms of MIT license.
 * For details, see the web site: https://github.com/rhktrth/TcpRedirector/
 */

package com.github.rhktrth.tcpredirector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		private final static int BUFFER_SIZE = 1000;

		private InputStream inputStream;
		private OutputStream outputStream;
		private byte streamBuffer[] = new byte[BUFFER_SIZE];

		public DataTunnel(Socket inputSocket, Socket outputSocket) {
			try {
				inputStream = inputSocket.getInputStream();
				outputStream = outputSocket.getOutputStream();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void run() {
			try {
				int n;
				while ((n = inputStream.read(streamBuffer)) > 0) {
					outputStream.write(streamBuffer, 0, n);
					outputStream.flush();
				}
			} catch (IOException e) {
				// nothing to do
			} finally {
				try {
					inputStream.close();
					outputStream.close();
				} catch (IOException e) {
					// nothing to do
				}
			}
		}
	}
}
