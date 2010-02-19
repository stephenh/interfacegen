package org.interfacegen.examples.inheritance;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SubTest extends TestCase {
	public void testBoth() {
		ISub s = new Sub();
		Assert.assertEquals("sub", s.getFromSub());
		Assert.assertEquals("base", s.getFromBase());
	}
}
