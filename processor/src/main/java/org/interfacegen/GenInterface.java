package org.interfacegen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/** Marks a class to have an interface generated for it. */
@Inherited
@Target(value = { ElementType.TYPE })
public @interface GenInterface {
	/** The base class for the generated interface. */
	String base() default "";
}
