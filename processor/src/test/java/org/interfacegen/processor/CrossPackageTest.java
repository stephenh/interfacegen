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

	@Test
	public void withShim() throws Exception {
		this.compile("t2/foo/Foo.java", "t2/bar/IBar.java", "t2/bar/Bar.java");
	}

}
