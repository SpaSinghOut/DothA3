package com.DotA3.main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import org.lwjgl.input.Keyboard;

public class ConsoleKeyListener implements KeyListener{
	Console owner;
	public ConsoleKeyListener(Console setOwner){
		owner = setOwner;
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER)owner.takeCommand();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
