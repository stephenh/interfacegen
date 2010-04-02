package org.interfacegen.examples;

import java.util.Collection;
import java.util.List;

import org.interfacegen.GenInterface;

@GenInterface
public class InlineTemplatesTest implements IInlineTemplatesTest {
	@Override
	public <T extends List<String> & Collection<String>> T foo(T t) {
		return t;
	}
}
