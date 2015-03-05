package com.DotA3.main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;

public class HeroChoice extends JMenuItem implements MouseListener{
	Hero.HeroType represents;
	Gui owner;
	public HeroChoice(Hero.HeroType setHeroType, Gui gui){
		setText(setHeroType.toString());
		represents = setHeroType;
		addMouseListener(this);
		owner = gui;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		owner.owner.hero.heroType = represents;
		owner.owner.hero.ready = true;//initHeroType(represents);
		owner.getLayeredPane().remove(owner.menuBar);
		Head.out(owner.owner + " picked: " + represents);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}
