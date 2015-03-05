package com.DotA3.main;

import java.util.ArrayList;

public class ActionQueue<Element> extends ArrayList<Element> {
	public void insert(int index, Element element){
		int size = size();
		add(element);
		for(int i = size; i > index; i--){
			set(i, get(i - 1));
		}
		set(index, element);
	}
	public boolean containsActions(){
		return !isEmpty();
	}
	public void completeAction(Element action){
		remove(action);
	}
	public boolean containsType(AIAction.ActionName actionName){
		for(Element action: this){
			if(((AIAction)action).actionName == actionName)return true;
		}
		return false;
	}
}
