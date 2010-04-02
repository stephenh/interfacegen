package org.interfacegen.examples;

import junit.framework.TestCase;

public class BaseTest extends TestCase {
	public void testIsRunnable() {
		Base b = new Base();
		Runnable r = b;
		r.run();
	}
}
