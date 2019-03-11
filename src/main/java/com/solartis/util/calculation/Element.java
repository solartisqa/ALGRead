package com.solartis.util.calculation;

public class Element {
	private String string;
	private boolean operator;
	
	public Element(String string, boolean operator) {
		setString(string);
		setOperator(operator);
	}
	
	public String getString() {
		return string;
	}
	public void setString(String string) {
		this.string = string;
	}
	public boolean isOperator() {
		return operator;
	}
	public void setOperator(boolean operator) {
		this.operator = operator;
	}
}
