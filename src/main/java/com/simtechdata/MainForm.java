package com.simtechdata;

import com.simtechdata.easyfxcontrols.containers.CHBox;
import com.simtechdata.easyfxcontrols.containers.CVBox;
import com.simtechdata.easyfxcontrols.controls.Button;
import com.simtechdata.easyfxcontrols.controls.CLabel;
import com.simtechdata.easyfxcontrols.controls.CTextArea;
import com.simtechdata.easyfxcontrols.controls.CTextField;
import com.simtechdata.sceneonefx.SceneOne;
import javafx.geometry.Pos;

public class MainForm {

	public MainForm() {
		buildControls();
		setControlActions();
		SceneOne.set(sceneId, vbox, width, height).centered().onCloseEvent(e-> System.exit(0)).show();
		UDPServer.start();
	}

	private final String sceneId = SceneOne.getRandom(10);
	private final double width   = 800;
	private final double height  = 900;

	private        CTextField tfCommand;
	private static CTextArea  taResponse;
	private        CVBox      vbox;
	private        CLabel     lblStatusWifi;
	private        CLabel     lblStatusNano;

	private void buildControls() {
		CLabel lblCommand  = new CLabel.Builder("Command").build();
		CLabel lblResponse = new CLabel.Builder("Response").build();
		CLabel lblInfoWifi = new CLabel.Builder("Wifi Status: ").width(75).alignment(Pos.CENTER_LEFT).build();
		CLabel lblInfoNano = new CLabel.Builder("Nano Status: ").width(75).alignment(Pos.CENTER_LEFT).build();
		lblStatusWifi = new CLabel.Builder().width(75).alignment(Pos.CENTER_LEFT).build();
		lblStatusNano = new CLabel.Builder().width(75).alignment(Pos.CENTER_LEFT).build();
		tfCommand     = new CTextField.Builder(width * .95).build();
		taResponse    = new CTextArea.Builder().size(width * .95, height * .75).build();
		Button btnQuit   = new Button.Builder("Quit", 45, 25).onAction(e -> close()).build();
		Button btnClear  = new Button.Builder("Clear", 55, 25).onAction(e -> clear()).build();
		CHBox  boxStatus = new CHBox.Builder(10, lblInfoWifi, lblStatusWifi, lblInfoNano, lblStatusNano).build();
		CHBox  boxButton = new CHBox.Builder(55, btnQuit, btnClear).alignment(Pos.CENTER).build();
		vbox = new CVBox.Builder(10, 20, boxStatus, lblCommand, tfCommand, lblResponse, taResponse, boxButton).size(width, height).build();
	}

	private void setControlActions() {
		tfCommand.setOnAction(e -> {
			UDPServer.send(tfCommand.getText() + "\n");
			tfCommand.requestFocus();
			tfCommand.selectAll();
		});
		taResponse.setEditable(false);
		lblStatusWifi.styleProperty().bind(UDPServer.getStatusWiFiProperty());
		lblStatusWifi.textProperty().bind(UDPServer.getStatusWiFiText());
		lblStatusNano.styleProperty().bind(UDPServer.getStatusNanoProperty());
		lblStatusNano.textProperty().bind(UDPServer.getStatusNanoText());
	}

	private void clear() {
		taResponse.setText("");
		tfCommand.requestFocus();
	}

	private void close() {
		System.exit(0);
	}

	public static void appendMessage(String message) {
		taResponse.appendText(message + "\n");
	}
}
