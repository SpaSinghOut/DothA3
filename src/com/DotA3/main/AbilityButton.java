package com.DotA3.main;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import org.lwjgl.input.Mouse;

import com.DotA3.main.Player.LeftClickState;

public class AbilityButton extends StateChangingButton implements MouseListener{
	Ability correspondingAbility;
	private static int numButtons = 0;
	static final double abilityButtonWidth = Head.getScreenDimensions().x * .0341;
	static final double abilityButtonHeight = Head.getScreenDimensions().y * .057;
	static long leveledUseProtection;
	public AbilityButton(Ability setCorrespondingAbility, Gui setOwner){
		super(setOwner);
		correspondingAbility = setCorrespondingAbility;
		setLocation((int) (Head.getScreenDimensions().x * .4 + abilityButtonWidth * 1.5 * numButtons++), (int) (Head.getScreenDimensions().y * .837));
		setSize((int)(abilityButtonWidth), (int) abilityButtonHeight);
		setBackground(Util.getAsJavaColor(correspondingAbility.abilityType.color));
		setOpaque(true);
		setVisible(true);
		addMouseListener(this);
	}
	
	public void setOwner(Gui gui){
		owner = gui;
	}
	public void setBorder(boolean b){
		Rectangle bound = getBounds();
		int offset = 3;
		if(b)getBorder().paintBorder(this, this.getGraphics(), 0, 0, bound.width, bound.height);
		else offset = 0;
		getBorder().paintBorder(this, this.getGraphics(), offset, offset, bound.width - offset * 2, bound.height - offset * 2);
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(Head.tickCount > leveledUseProtection + 0.5 * Head.tickRate)
		switch(state){
		case USE:
			if(correspondingAbility.abilityType.castType == Ability.AbilityType.CastType.POINTTARGET
			|| correspondingAbility.abilityType.castType == Ability.AbilityType.CastType.ALIVETARGET
			|| correspondingAbility.abilityType.castType == Ability.AbilityType.CastType.CHANNELING){
				((Player)owner.owner).selectedAbility = correspondingAbility;
				((Player)owner.owner).leftClickState = LeftClickState.ABILITYUSE;
				owner.out("Player's left click state is set to ability use");
			}
			//else if(correspondingAbility.castType == Ability.CastType.TOGGLE)
			else if(correspondingAbility.abilityType.castType == Ability.AbilityType.CastType.INSTANT || 
					correspondingAbility.abilityType.castType == Ability.AbilityType.CastType.TOGGLE)
				owner.owner.hero.castSpell(correspondingAbility);
			break;
		case LEVEL:
			//prevents leveling the ability if it is maxed
			if(correspondingAbility.level == correspondingAbility.abilityType.levelRequirements.length)
				break;
			//prevents leveling the ability if hero level requirements are not met
			else if(owner.owner.hero.getStat(Constants.level) >= 
			correspondingAbility.abilityType.levelRequirements[correspondingAbility.level] ){
				correspondingAbility.levelAbility();
				leveledUseProtection = Head.tickCount;
				((Player)owner.owner).leftClickRestriction = (int) (Head.tickRate * 0.5);
				mouseExited(new MouseEvent(this, 0,System.nanoTime(),0,Mouse.getX(),Mouse.getY(),0, true));
				if(arg0.getX() != 0)mouseEntered(new MouseEvent(this, 1,System.nanoTime(),0,Mouse.getX(), Mouse.getY(),0, true));
			}
			break;
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		owner.showDecription(true, correspondingAbility.abilityType.name() + " level: " + correspondingAbility.level);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		owner.showDecription(false, "");
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
}
