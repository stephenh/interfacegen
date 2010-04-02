package org.interfacegen.processor;

import org.junit.Test;

public class CrossPackageTest extends AbstractTestCase {

	@Test
	public void dependentFirst() throws Exception {
		this.compile("t1/bar/Bar.java", "t1/foo/Foo.java");
	}

	@Test
	public void dependentLast() throws Exception {
		this.compile("t1/foo/Foo.java", "t1/bar/Bar.java");
	}

}
