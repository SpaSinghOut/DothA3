package com.DotA3.main;

public class Command {
	String commandName;
	int numParameters;
	double[] numericParameters;
	String[] alphabeticParameters;
	int[] parameterLocations;
	ParameterType[] parameterTypes;
	Console owner;
	char[] input;
	public Command(Console setOwner, String originalString){
		owner = setOwner;
		input = originalString.toCharArray();
		commandName = getCommandName(originalString);
		owner.gui.out("Recognized command name: " + commandName);
		sortParameters();
		if(numParameters > 0)placeParameters();
	}
	enum ParameterType{
		NUMERIC, ALPHABETIC,;
	}
	private String getCommandName(String string){
		String commandString = "";
		char[] stringAsCharArray = string.toCharArray();
		for(int i = 0; i < string.length(); i++){
			if(Character.isAlphabetic(stringAsCharArray[i]))commandString += stringAsCharArray[i];else break;
		}
		return commandString;
	}
	private int getNumberOfParameters(){
		int n = 0;
		for(int i = 0; i < input.length; i++){
			if(Character.isWhitespace(input[i]))
				n++;
		}
		return n;
	}
	private void sortParameters(){
		numParameters = getNumberOfParameters();
		parameterLocations = new int[numParameters];
		parameterTypes = new ParameterType[numParameters];
		owner.gui.out("The number of parameters is: " + numParameters);
		int n = 0;
		for(int i = 0; i < input.length; i++){
			if(Character.isWhitespace(input[i])){
				parameterLocations[n] = i + 1;
				owner.gui.out("Parameter number: " + n + " is located at " + parameterLocations[n]);
				if(Character.isLetter(input[i+1]))parameterTypes[n++] = ParameterType.ALPHABETIC;
				else parameterTypes[n++] = ParameterType.NUMERIC;
				owner.gui.out("And its parameter type is: " + parameterTypes[n-1]);
			}
		}
		int numNumericParameters = 0, numAlphabeticParameters = 0;
		for(ParameterType pt: parameterTypes)if(pt == ParameterType.ALPHABETIC)numAlphabeticParameters++;else numNumericParameters++;
		numericParameters = new double[numNumericParameters]; alphabeticParameters = new String[numAlphabeticParameters];
		owner.gui.out("The number of numeric parameters is: " + numNumericParameters);
		owner.gui.out("The number of alphabetic parameters is: " + numAlphabeticParameters);
	}
	private char[] getParameterCharArray(){
		String parameterString = "";
		boolean start = false;
		for(int i = 0; i < input.length; i++){
			if(start)parameterString += input[i];
			if(Character.isWhitespace(input[i]))start = true;
		}
		return parameterString.toCharArray();
	}
	private void placeParameters(){
		int a = 0, n = 0;
		String parameter = "";
		char[] parsedArray = (String.valueOf(input) + " ").toCharArray();
		owner.gui.out(String.valueOf(parsedArray));
		for(int i = parameterLocations[0]; i < parsedArray.length; i++)
			if(!Character.isWhitespace(parsedArray[i]))parameter += input[i];
			else{
				owner.gui.out(String.valueOf(getArgumentEndsAt(i)));
				if(parameterTypes[getArgumentEndsAt(i)] == ParameterType.ALPHABETIC)alphabeticParameters[a++] = parameter;
				else numericParameters[n++] = Integer.parseInt(parameter);
				parameter = "";
			}
	}
	private int getArgumentEndsAt(int i){
		int argNumber = 0;
		int argLocation = 0;
		if(i-- == input.length)argNumber = numParameters;
		else while(i > argLocation)argLocation = parameterLocations[++argNumber] - 2;
		return argNumber - 1;
	}
}
