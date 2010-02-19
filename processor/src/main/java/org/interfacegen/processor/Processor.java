package org.interfacegen.processor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.interfacegen.GenInterface;

/** A processor that generates interfaces from a class.
 *
 * There is one {@link Processor} created per compilation run. Within that run,
 * there are several rounds, with <code>process</code> being called for each round the compiler
 * decides this processor should be a part of.
 *
 * For javac, there is one big compilation run with all classes. For Eclipse, there is one
 * initial large compilation run, and then many small compilation runs each time the user
 * hits save.
 *
 * See the processor <a href="http://java.sun.com/javase/6/docs/api/javax/annotation/processing/Processor.html">javadocs</a>
 * for more details.
 */
@SupportedAnnotationTypes( { "org.interfacegen.GenInterface" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

	private final List<String> methodNamesInObject = new ArrayList<String>();
	private Elements eutils; // this is dumb, but it's shorter than processingEnv.getElementUtils

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.eutils = processingEnv.getElementUtils();
		if (this.methodNamesInObject.size() == 0) {
			for (ExecutableElement m : ElementFilter.methodsIn(this.eutils.getTypeElement("java.lang.Object").getEnclosedElements())) {
				this.methodNamesInObject.add(m.getSimpleName().toString());
			}
		}
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		try {
			for (Element element : roundEnv.getElementsAnnotatedWith(GenInterface.class)) {
				if (element.getKind() == ElementKind.CLASS) {
					this.generateInterface((TypeElement) element);
				} else {
					this.warnElementIsUnhandled(element);
				}
			}
		} catch (Exception e) {
			this.logExceptionToTextFile(e);
		}
		return true;
	}

	private void generateInterface(TypeElement type) {
		GClass g = new GClass(this.getNameWithIPrefix(type)).setInterface();

		// String date = new SimpleDateFormat("yyyy MMM dd hh:mm").format(new Date());
		// g.addImports(Generated.class).addAnnotation("@Generated(value = \"" + Processor.class.getName() + "\", date = \"" + date + "\")");

		GenInterface gi = type.getAnnotation(GenInterface.class);
		if (!"".equals(gi.base())) {
			g.baseClassName(gi.base());
		}

		List<? extends ExecutableElement> all = ElementFilter.methodsIn(this.eutils.getAllMembers(type));
		for (ExecutableElement method : all) {
			if (this.methodNamesInObject.contains(method.getSimpleName().toString())) {
				continue;
			}
			if (this.shouldGenerateMethod(method)) {
				this.generateMethod(g, method);
			}
		}

		this.saveCode(g);
	}

	private void generateMethod(GClass g, ExecutableElement method) {
		List<String> args = this.getArguments(method);
		String nameWithArgs = method.getSimpleName() + "(" + Join.commaSpace(args) + ")";
		GMethod m = g.getMethod(nameWithArgs).returnType(method.getReturnType().toString());

		List<String> params = this.getTypeParameters(method);
		if (params.size() > 0) {
			m.typeParameters(Join.commaSpace(params));
		}
	}

	private List<String> getArguments(ExecutableElement method) {
		List<String> args = new ArrayList<String>();
		for (VariableElement parameter : method.getParameters()) {
			args.add(parameter.asType().toString() + " " + parameter.getSimpleName());
		}
		return args;
	}

	private List<String> getTypeParameters(ExecutableElement method) {
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

	private boolean shouldGenerateMethod(ExecutableElement method) {
		boolean isStatic = method.getModifiers().contains(Modifier.STATIC);
		boolean isPrivate = method.getModifiers().contains(Modifier.PRIVATE);
		boolean notPublic = !method.getModifiers().contains(Modifier.PUBLIC);
		return !isStatic && !isPrivate && !notPublic;
	}

	private String getNameWithIPrefix(TypeElement type) {
		String[] parts = type.toString().split("\\.");
		parts[parts.length - 1] = "I" + parts[parts.length - 1];
		return Join.join(parts, ".");
	}

	private void saveCode(GClass gc, Element... sourceElements) {
		try {
			JavaFileObject jfo = this.processingEnv.getFiler().createSourceFile(gc.getFullClassNameWithoutGeneric(), sourceElements);
			Writer w = jfo.openWriter();
			w.write(gc.toCode());
			w.close();
		} catch (IOException io) {
			this.processingEnv.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

	/** Logs <code>e</code> to <code>SOURCE_OUTPUT/interfacegen-errors.txt</code> */
	private void logExceptionToTextFile(Exception e) {
		try {
			FileObject fo = this.processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, "", "interfacegen-exception.txt");
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
			this.processingEnv.getMessager().printMessage(Kind.ERROR, "Error writing out error message " + e2.getMessage());
		}
	}

	private void warnElementIsUnhandled(Element element) {
		this.processingEnv.getMessager().printMessage(Kind.WARNING, "Unhandled element " + element);
	}

}
