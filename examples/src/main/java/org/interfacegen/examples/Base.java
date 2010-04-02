package org.interfacegen.examples;

import org.interfacegen.GenInterface;

@GenInterface(base = "java.lang.Runnable")
public class Base implements IBase {
	public void run() {
	}
}
