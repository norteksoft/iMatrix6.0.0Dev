package com.example.expense.entity;

public enum ColorEnum {
	RED("color.red"),
	BLUE("color.blue"),
	YELLOW("color.yellow"),
	GREEN("color.green"),
	ORANGE("color.orange"),
	PURPLE("color.purple"),
	CANCEL("color.white");
	String code;
	private ColorEnum(String code) {
		this.code=code;
	}
	public String getCode() {
		return code;
	}

}
