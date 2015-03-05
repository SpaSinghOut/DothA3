package com.DotA3.main;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;


public class StatsButton extends StateChangingButton implements MouseListener{
	StatsButton(Gui setOwner){
		super(setOwner);
		setSize(80, 80);
		setLocation((int)(Head.getScreenDimensions().getX() * .27),
				(int)(Head.getScreenDimensions().getY() * .79));
		addMouseListener(this); 
		setOpaque(false);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(state == State.LEVEL
		&& owner.owner.hero.getStat(Constants.abilityPoints) > 0){
			owner.owner.hero.changeStat(Constants.abilityPoints, -1);
			owner.owner.hero.changeStat(Constants.allAttributesBonus, 2);
			setState(State.IDLE);
		}
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

}
