package com.simtechdata;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class UDPServer {

	private static final int            port               = 7373;
	private static       InetAddress    espIPAddy;
	private static       int            clientPort;
	private static       DatagramSocket socket;
	private static final StringProperty statusWiFiProperty = new SimpleStringProperty("");
	private static final StringProperty statusWiFiText     = new SimpleStringProperty("");
	private static final StringProperty statusNanoProperty = new SimpleStringProperty("");
	private static final StringProperty statusNanoText     = new SimpleStringProperty("");
	private static       Date           wifiPingTime       = new Date(System.currentTimeMillis());
	private static       Date           nanoPingTime       = new Date(System.currentTimeMillis());
	private static final Timer          pingTimer          = new Timer();
	private static       boolean        wifiPingReceived   = false;
	private static       boolean        nanoPingReceived   = false;
	private static final String         red                = "-fx-text-fill: rgb(155,0,0);";
	private static final String         green              = "-fx-text-fill: rgb(0,155,0);";

	public static void start() {
		try {
			socket = new DatagramSocket(port);
			startServer();
			pingTimer.scheduleAtFixedRate(pingTimerTask(), 2500, 2500);
		}
		catch (SocketException e) {
			throw new RuntimeException(e);
		}
	}

	private static void startServer() {
		new Thread(() -> {
			byte[] buff = new byte[500];
			while (true) {
				try {
					DatagramPacket request = new DatagramPacket(buff, buff.length);
					socket.receive(request);
					espIPAddy  = request.getAddress();
					clientPort = request.getPort();
					String inText = new String(request.getData());
					if (inText.contains("pingwifi")) {
						wifiPingTime     = new Date(System.currentTimeMillis());
						wifiPingReceived = true;
					}
					else if (inText.contains("pingnano")) {
						nanoPingTime     = new Date(System.currentTimeMillis());
						nanoPingReceived = true;
					}
					else {
						String dataIn = new String(request.getData());
						MainForm.appendMessage(dataIn);
					}
					buff = new byte[500];
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}

			}
		}).start();
	}

	public static void send(String text) {
		try {
			if (espIPAddy != null) {
				byte[]         message       = text.getBytes();
				DatagramPacket messagePacket = new DatagramPacket(message, message.length, espIPAddy, clientPort);
				socket.send(messagePacket);
				MainForm.appendMessage("\t" + text.trim() + " sent");
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static TimerTask pingTimerTask() {
		return new TimerTask() {
			@Override public void run() {
				Date     nowDate      = new Date(System.currentTimeMillis());
				long     now          = nowDate.getTime();
				long     wifiThen     = wifiPingTime.getTime();
				long     nanoThen     = nanoPingTime.getTime();
				long     deltaWifi    = now - wifiThen;
				long     deltaNano    = now - nanoThen;
				Duration durationWifi = Duration.ofMillis(deltaWifi);
				Duration durationNano = Duration.ofMillis(deltaNano);
				boolean  wifiDown     = durationWifi.getSeconds() > 6 || !wifiPingReceived;
				boolean  nanoDown     = durationNano.getSeconds() > 6 || !nanoPingReceived;
				Platform.runLater(() -> {
					statusWiFiText.setValue(wifiDown ? "Offline" : "Online");
					statusWiFiProperty.setValue(wifiDown ? red : green);
					statusNanoText.setValue(nanoDown ? "Offline" : "Online");
					statusNanoProperty.setValue(nanoDown ? red : green);
				});

			}
		};
	}

	public static StringProperty getStatusWiFiProperty() {
		return statusWiFiProperty;
	}

	public static StringProperty getStatusWiFiText() {
		return statusWiFiText;
	}

	public static StringProperty getStatusNanoProperty() {
		return statusNanoProperty;
	}

	public static StringProperty getStatusNanoText() {
		return statusNanoText;
	}

}
