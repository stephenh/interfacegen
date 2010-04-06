package org.interfacegen.processor;

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
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
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
			TypeElement object = this.eutils.getTypeElement("java.lang.Object");
			if (object != null) {
				for (ExecutableElement m : ElementFilter.methodsIn(object.getEnclosedElements())) {
					this.methodNamesInObject.add(m.getSimpleName().toString());
				}
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
			Util.logExceptionToTextFile(this.processingEnv, e);
		}
		return true;
	}

	private void generateInterface(TypeElement type) {
		GenInterface gi = type.getAnnotation(GenInterface.class);

		GClass g = new GClass(this.fullClassName(gi, type)).setInterface();
		if (!"".equals(gi.base())) {
			g.baseClassName(gi.base());
		}
		for (String annotation : gi.annotations()) {
			g.addAnnotation("@" + annotation);
		}

		for (ExecutableElement method : ElementFilter.methodsIn(this.eutils.getAllMembers(type))) {
			if (this.methodNamesInObject.contains(method.getSimpleName().toString())) {
				continue;
			}
			if (this.shouldGenerateMethod(method)) {
				if (method.getEnclosingElement().toString().equals(g.getFullClassNameWithoutGeneric())) {
					continue;
				}
				this.generateMethod(g, method);
			}
		}

		Util.addGenerated(g, Processor.class);
		Util.saveCode(this.processingEnv, g);
	}

	private String fullClassName(GenInterface gi, TypeElement type) {
		String fullClassName;
		if (!"".equals(gi.name())) {
			fullClassName = Util.simpleOrFull(this.processingEnv, type, gi.name());
		} else {
			fullClassName = this.getNameWithIPrefix(type);
		}
		return fullClassName + new GenericSuffix(type).varsWithBounds;
	}

	private void generateMethod(GClass g, ExecutableElement method) {
		List<String> args = Util.getArguments(method);
		String nameWithArgs = method.getSimpleName() + "(" + Join.commaSpace(args) + ")";
		GMethod m = g.getMethod(nameWithArgs).returnType(method.getReturnType().toString());

		List<String> params = Util.getTypeParameters(method);
		if (params.size() > 0) {
			m.typeParameters(Join.commaSpace(params));
		}
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

	private void warnElementIsUnhandled(Element element) {
		this.processingEnv.getMessager().printMessage(Kind.WARNING, "Unhandled element " + element);
	}

}
