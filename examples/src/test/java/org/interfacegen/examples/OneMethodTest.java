package org.interfacegen.examples;

import junit.framework.TestCase;

public class OneMethodTest extends TestCase {

	public void testOneMethod() {
		IOneMethod m = new OneMethod();
		m.fooMethod();
	}

}
