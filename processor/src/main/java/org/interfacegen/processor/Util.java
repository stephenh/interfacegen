package org.interfacegen.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.util.Join;

public class Util {

	public static void saveCode(ProcessingEnvironment env, GClass gc, Element... sourceElements) {
		try {
			JavaFileObject jfo = env.getFiler().createSourceFile(gc.getFullClassNameWithoutGeneric(), sourceElements);
			Writer w = jfo.openWriter();
			w.write(gc.toCode());
			w.close();
		} catch (IOException io) {
			env.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

	/** Logs <code>e</code> to <code>SOURCE_OUTPUT/interfacegen-errors.txt</code> */
	public static void logExceptionToTextFile(ProcessingEnvironment env, Exception e) {
		try {
			FileObject fo = env.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "interfacegen-exception.txt");
			OutputStream out = fo.openOutputStream();
			e.printStackTrace(new PrintStream(out));
			// Specifically for Eclipse's AbortCompilation exception which has a useless printStackTrace output
			try {
				Field f = e.getClass().getField("problem");
				Object problem = f.get(e);
				out.write(problem.toString().getBytes());
			} catch (NoSuchFieldException nsfe) {
			}
			out.close();
		} catch (Exception e2) {
			env.getMessager().printMessage(Kind.ERROR, "Error writing out error message " + e2.getMessage());
		}
	}

	public static List<String> getArguments(ExecutableElement method) {
		List<String> args = new ArrayList<String>();
		for (VariableElement parameter : method.getParameters()) {
			args.add(parameter.asType().toString() + " " + parameter.getSimpleName());
		}
		return args;
	}

	public static List<String> getTypeParameters(ExecutableElement method) {
		List<String> params = new ArrayList<String>();
		if (method.getTypeParameters().size() != 0) {
			for (TypeParameterElement p : method.getTypeParameters()) {
				String base = p.toString();
				if (p.getBounds().size() > 0) {
					List<String> bounds = new ArrayList<String>();
					for (TypeMirror tm : p.getBounds()) {
						bounds.add(tm.toString());
					}
					base += " extends " + Join.join(bounds, " & ");
				}
				params.add(base);
			}
		}
		return params;
	}

}
