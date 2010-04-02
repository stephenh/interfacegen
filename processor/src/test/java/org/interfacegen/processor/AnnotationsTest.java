package org.interfacegen.processor;

import org.junit.Assert;
import org.junit.Test;

public class AnnotationsTest extends AbstractTestCase {

	@Test
	public void deprecated() throws Exception {
		ClassLoader cl = this.compile("t3/Foo.java");
		Class<?> iFoo = cl.loadClass("t3.IFoo");
		Deprecated d = iFoo.getAnnotation(Deprecated.class);
		Assert.assertNotNull(d);
	}
}
