package org.interfacegen.examples.inheritance;

import org.interfacegen.GenInterface;

@GenInterface
public class Sub extends Base implements ISub {
	public String getFromSub() {
		return "sub";
	}
}
