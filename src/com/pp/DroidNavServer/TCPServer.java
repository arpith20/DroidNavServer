package com.pp.DroidNavServer;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable {
	
	public Thread t = null;
	private static Robot robot = null;
	private int PORT;
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private BufferedReader in = null;
	private Process p = null;
	private String message = null;
	private String value = null;
	private char type;

	public TCPServer(int PORT) {
		this.PORT = PORT;
		t = new Thread(this, "TCP");
		t.start();
	}

	@Override
	public void run() {

		try {
			serverSocket = new ServerSocket(PORT, 10);
			System.out.println("TCP Server created.");
			clientSocket = serverSocket.accept();
			System.out.println("Connection recieved from : "
					+ clientSocket.getInetAddress().getHostName());
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}

			do {
				message = in.readLine();
				if (message == null || message.isEmpty())
					break;
				System.out.println(message);
				type = message.charAt(0);
				value = message.substring(2);
				switch (type) {

				case 'k':
					keyPress(value);
					break;
				case 't':
					textRecieved(value);
					break;
				case 'c':
					mouseClick(value.charAt(0));
					break;
				case 'p':
					processProximity(value);
					break;
				case 's':
					processSwipe(value.charAt(0));
					break;
				case 'v':
					changeVolume(value.charAt(0));
					break;
				}
			} while (serverSocket.isBound());
		} catch (IOException e) {
			System.err.println("IOExcpetion Error");
		}

		try {
			in.close();
			serverSocket.close();
			System.out.println("Client disconnected.");
		} catch (IOException e) {
			System.err.println("Closing error");
		}

	} // end of run

	private void changeVolume(char value) {
		Runtime r = Runtime.getRuntime();
		switch(value)
		{
			case 'i' : try {
							p = r.exec("nircmd changesysvolume " + Common.VOLUME_AMOUNT );
						} catch (Exception e) {
							System.out.println("Error executing process.");
						}
					break;
			case 'd' : try {
							p = r.exec("nircmd changesysvolume " + -Common.VOLUME_AMOUNT );
						} catch (Exception e) {
							System.out.println("Error executing process.");
						}
					break;
		}
	}

	private void processSwipe(char value) {
		switch (value) {
		case 'u':
			robot.mouseWheel(-Common.SCROLL_AMOUNT);
			break;
		case 'd':
			robot.mouseWheel(Common.SCROLL_AMOUNT);
			break;
		}
	}

	private void processProximity(String value) {
		Runtime r = Runtime.getRuntime();
		if(value.equals("0.0"))
		{
			try {
				p = r.exec("nircmd monitor off");
			} catch (Exception e) {
				System.out.println("Error executing process.");
			}
		}
		else if(value.equals("1.0") )
		{
			robot.keyPress(KeyEvent.VK_ESCAPE);
			if(p!=null) 
				p.destroy();
			p = null;
		}
	}

	private static void textRecieved(String value) {
		StringSelection stringSelection = new StringSelection(value);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		robot.keyRelease(KeyEvent.VK_V);
	}

	private static void keyPress(String k) {
		try {
			robot.keyPress(Integer.parseInt(k));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private static void mouseClick(char value) {
		switch (value) {
		case 'l':
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			break;
		case 'r':
			robot.mousePress(InputEvent.BUTTON3_MASK);
			robot.mouseRelease(InputEvent.BUTTON3_MASK);
			break;
		}
	}
}
