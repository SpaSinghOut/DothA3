package com.DotA3.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;

public class Console implements MouseListener{
	JTextField consoleInput;
	int consoleInputHeight, consoleInputWidth;
	Gui gui;
	String[] commandNames;
	public Console(Gui setGui){
		gui = setGui;
		consoleInput = new JTextField("write something here");
		consoleInput.addKeyListener(new ConsoleKeyListener(this));
		consoleInput.setLocation(gui.scrollPane.getLocation().x, gui.scrollPane.getLocation().y + gui.scrollPane.getSize().height);
		consoleInputWidth = gui.scrollPane.getSize().width;
		consoleInputHeight = (int)(gui.screenY * (0.025));
		consoleInput.setSize(consoleInputWidth, consoleInputHeight);
		gui.getLayeredPane().add(consoleInput);
		initializeCommandNames();
	}
	private void initializeCommandNames(){
		commandNames = new String[6];
		commandNames[0] = "help";
		commandNames[1] = "resetHeroLocation";
		commandNames[2] = "setHeroLocation";
		commandNames[3] = "changeAbilityLevel";
		commandNames[4] = "changeHeroStat";
		commandNames[5] = "respawn";
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		char[] realString = consoleInput.getText().toCharArray(), testString = "write something here".toCharArray();
		boolean match = true;
		for(int i = 0; i < testString.length; i++)if(i < realString.length && realString[i] != testString[i])match = false;
		if(match)consoleInput.setText("");
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void takeCommand() {
		Command command = new Command(this, consoleInput.getText());
		gui.out("The command name is: " + command.commandName);
		switch(command.commandName){
		case "help":
			for(String s: commandNames)gui.out(s);
			break;
		case "resetHeroLocation":
			gui.out("Command recognised: " + command.commandName);
			gui.owner.hero.setLocation(new Location(Head.getScreenDimensions().getX() / 2, 750));
			break;	
		case "setHeroLocation":
			gui.out("Command recognised: " + command.commandName);
			gui.out("Setting hero location to: " + command.numericParameters[0] + ", " + command.numericParameters[1]);
			gui.owner.hero.setLocation(new Location(command.numericParameters[0], command.numericParameters[1]));
			gui.owner.camera.centralPoint = new Location(command.numericParameters[0], command.numericParameters[1]);
			break;
		case "changeAbilityLevel":
			gui.owner.hero.abilities.get((int) command.numericParameters[0]).level += command.numericParameters[1];
			break;
		case "changeHeroStat":
			gui.out("Command recognized: " + command.commandName);
			if(command.parameterTypes[0] == Command.ParameterType.NUMERIC){
				gui.out("The stat: " + command.numericParameters[0] + " was changed from " + gui.owner.hero.getStat((int)command.numericParameters[0]) + " to ");
				gui.owner.hero.changeStat((int)command.numericParameters[0], command.numericParameters[1]);
				gui.out(String.valueOf(gui.owner.hero.getStat((int)command.numericParameters[0])));
			}
			else{
				gui.out("With the alphabetic parameter: " + command.alphabeticParameters[0]);
				if(Constants.convertString(command.alphabeticParameters[0]) == -1){
					gui.out("Invalid parameter: " + command.alphabeticParameters[0]);
					gui.out("Parameter: " + command.alphabeticParameters[0] + " either does not exist or has not been accounted for.");
				}
				gui.out("The stat: " + command.alphabeticParameters[0] + " was changed from "
				+ gui.owner.hero.getStat(Constants.convertString(command.alphabeticParameters[0])) + " to ");
				gui.owner.hero.changeStat(Constants.convertString(command.alphabeticParameters[0]), command.numericParameters[0]);
				gui.out(String.valueOf(gui.owner.hero.getStat(Constants.convertString(command.alphabeticParameters[0]))));
			}
			break;
		case "respawn":
			gui.owner.respawn();
			break;
		default:
			gui.out("Command: " + command.commandName + " does not exist");
			return;
		}
	}
}
