package com.pp.DroidNavServer;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer implements Runnable {

	private int PORT;
	private Robot robot = null;
	private DatagramSocket serverSocket = null;
	public Thread t = null;
	private byte[] buffer = null;
	private DatagramPacket packet = null;
	private String message = null;
	private String value = null;
	private char type;

	public UDPServer(int PORT) {
		this.PORT = PORT;
		t = new Thread(this, "UDP");
		t.start();
	}

	@Override
	public void run() {
		buffer = new byte[Common.UDP_BUFFER_SIZE];
		packet = new DatagramPacket(buffer, Common.UDP_BUFFER_SIZE);
		try {
			serverSocket = new DatagramSocket(PORT);
			System.out.println("UDP Server created.");
		} catch (SocketException e) {
			e.printStackTrace();
		}

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		do {
			try {
				serverSocket.receive(packet);
				message = new String(packet.getData(), 0, packet.getLength(),
						"UTF-8");
				if (message == null || message.isEmpty()
						|| message.contains("bye"))
					break;
				message = message.trim();
				System.out.println(message);
				type = message.charAt(0);
				value = message.substring(2);
				switch (type) {
				case 'm':
					moveMouse(value);
					break;
				case 'o':
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < Common.UDP_BUFFER_SIZE; i++)
				buffer[i] = ' ';
		} while (serverSocket.isBound()); // end of while

	} // end of run

	private void moveMouse(String value) {
		String array[] = value.split(" ");
		int nX = MouseInfo.getPointerInfo().getLocation().x;
		int nY = MouseInfo.getPointerInfo().getLocation().y;
		int x = Integer.parseInt(array[0]);
		int y = Integer.parseInt(array[1]);
		robot.mouseMove(nX + (int) (Common.MOUSE_SENSITVITY * x), nY
				+ (int) (Common.MOUSE_SENSITVITY * y));
	}
}
