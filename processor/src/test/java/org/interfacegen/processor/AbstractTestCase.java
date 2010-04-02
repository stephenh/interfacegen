package org.interfacegen.processor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.processing.Processor;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.junit.Before;
import org.junit.BeforeClass;

public class AbstractTestCase {

	private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();
	private static final File outputBase = new File(new File(System.getProperty("java.io.tmpdir")), "interfacegen");
	private static int testNumber = 0;
	private final HashMap<String, String> aptProperties = new HashMap<String, String>();
	private final File outputSub = new File(outputBase, String.valueOf(testNumber++));

	@BeforeClass
	public static void resetBase() {
		if (outputBase.exists() && !recursiveDelete(outputBase)) {
			System.err.println("Cannot delete " + outputBase);
		}
		outputBase.mkdirs();
	}

	@Before
	public void mkdirsOutputSub() {
		this.outputSub.mkdirs();
	}

	protected ClassLoader compile(String... files) throws IOException {
		System.out.println(COMPILER);
		DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(diagnosticCollector, null, null);

		List<File> compilationUnits = new ArrayList<File>(files.length);
		for (String file : files) {
			compilationUnits.add(new File("src/test/templates/" + file));
		}

		CompilationTask task = COMPILER.getTask(
			null,
			fileManager,
			diagnosticCollector,
			this.compileProps("-d", this.outputSub.getAbsolutePath()),
			null,
			fileManager.getJavaFileObjectsFromFiles(compilationUnits));
		task.setProcessors(Arrays.asList(new Processor[] { new org.interfacegen.processor.Processor() }));
		Boolean result = task.call();

		fileManager.close();
		System.out.println(this.outputSub.getAbsolutePath());

		if (!result) {
			throw new RuntimeException(message(diagnosticCollector));
		}

		return new URLClassLoader(new URL[] { this.outputSub.getAbsoluteFile().toURI().toURL() }, this.getClass().getClassLoader());
	}

	private List<String> compileProps(String... props) {
		List<String> result = new ArrayList<String>();
		result.addAll(Arrays.asList(props));
		result.add("-XprintRounds");
		for (Entry<String, String> prop : this.aptProperties.entrySet()) {
			result.add("-A" + prop.getKey() + "=" + prop.getValue());
		}
		return result;
	}

	protected static String filePath(String qualifiedClassName) {
		return qualifiedClassName.replace(".", "/") + ".java";
	}

	private static boolean recursiveDelete(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File each : files) {
				boolean worked = recursiveDelete(each);
				if (!worked) {
					return false;
				}
			}
		}
		return file.delete();
	}

	private static String message(DiagnosticCollector<JavaFileObject> dc) {
		StringBuilder message = new StringBuilder();
		for (Diagnostic<?> d : dc.getDiagnostics()) {
			switch (d.getKind()) {
				case ERROR:
					message.append("ERROR: " + d.toString() + "\n");
			}
		}
		return message.toString();
	}
}
