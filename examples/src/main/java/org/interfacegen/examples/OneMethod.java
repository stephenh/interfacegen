package org.interfacegen.examples;

import org.interfacegen.GenInterface;

@GenInterface
public class OneMethod implements IOneMethod {

	public void fooMethod() {
	}

	@SuppressWarnings("unused")
	private void privateMethodIsSkipped() {
	}

	protected void protectedMethodIsSkipped() {
	}

	void packagePrivateMethodIsSkipped() {
	}

	public static void staticMethodIsSkipped() {
	}
}
