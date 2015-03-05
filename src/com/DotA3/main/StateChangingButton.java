package com.DotA3.main;

import javax.swing.JButton;

public class StateChangingButton extends JButton{

	protected State state;
	Gui owner;
	
	protected StateChangingButton(Gui setOwner){
		owner = setOwner;
		state = State.IDLE;
	}
	
	enum State{
		USE, LEVEL, IDLE;
	}

	public void setState(State setState){
		state = setState;
	}
}
