package com.pp.DroidNavServer;

import java.net.*;
import java.util.Enumeration;

public class Server {

	public static void main(String[] args) {

		int PORT = Common.DEFAULT_PORT;

		System.out.print("IP Address : ");
		getIpAddresses();
		System.out.println("\nPort : " + PORT);

		TCPServer tcpServer = new TCPServer(PORT);
		UDPServer udpServer = new UDPServer(PORT);

		try {
			tcpServer.t.join();
			System.out.println("TCP Server thread exited");
			udpServer.t.join();
			System.out.println("UDP Server thread exited");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Main thread exited.");
	}

	private static void getIpAddresses() {
		Enumeration<NetworkInterface> enumNI = null;
		try {
			enumNI = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (enumNI.hasMoreElements()) {
			NetworkInterface ifc = enumNI.nextElement();
			try {
				if (ifc.isUp()) {
					Enumeration<InetAddress> enumAdds = ifc.getInetAddresses();
					while (enumAdds.hasMoreElements()) {
						InetAddress addr = enumAdds.nextElement();
						if (!addr.isLoopbackAddress()
								&& addr instanceof Inet4Address)
							System.out.print(addr.getHostAddress() + " ");
					}
				}
			} catch (SocketException e) {
				e.printStackTrace();
			}
		}
	}
}