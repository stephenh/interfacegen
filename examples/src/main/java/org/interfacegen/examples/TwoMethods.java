package org.interfacegen.examples;

import org.interfacegen.GenInterface;

@GenInterface
public class TwoMethods implements ITwoMethods {

	public void fooMethod() {
	}

	public String fooMethod(String arg) {
		return arg + arg;
	}

}
