package org.interfacegen.examples;

import org.interfacegen.GenInterface;

@GenInterface
public class GenericMethods<T> implements IGenericMethods<T> {

	public T doFoo(T t) {
		return t;
	}

}
