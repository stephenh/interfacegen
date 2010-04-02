package org.interfacegen.examples.inheritance;

public class Base extends Root implements HasGetFromBase {
	public String getFromBase(String b) {
		return "base";
	}

	public String getFromRoot() {
		return "root-base";
	}
}
