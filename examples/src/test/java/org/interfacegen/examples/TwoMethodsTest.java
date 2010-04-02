package org.interfacegen.examples;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TwoMethodsTest extends TestCase {

	public void testTwoMethods() {
		ITwoMethods m = new TwoMethods();
		m.fooMethod();
		Assert.assertEquals("22", m.fooMethod("2"));
	}

}
