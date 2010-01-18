---
layout: default
title: Setup
---

Setup
=====

interfacegen is an annotation processor that you configure the Java compiler to run during its compilation run.

Ivy/Maven Repository
--------------------

You can download interfacegen releases from the [Joist repo](http://repo.joist.ws). It has both Ivy `ixy.xml` and Maven `pom.xml` artifacts, see:

[http://repo.joist.ws/org/interfacegen/interfacegen/](http://repo.joist.ws/org/interfacegen/interfacegen/)

You will only need `interfacegen.jar` on your project's classpath.

Ant/`javac`
-----------

If you're using Ant/`javac`, you just need to:

* Run Ant/`javac` with a JDK6 `JAVA_HOME` (annotation processors are a feature of JDK6)
* Have `interfacegen.jar` on your application's classpath

So your code can be as little as:

<pre name="code" class="xml">
    &lt;javac destdir="bin/main"&gt;
      &lt;classpath refid="main.classpath"/&gt;
      &lt;src path="src/main/java"/&gt;
    &lt;/javac&gt;
</pre>

The `javac` compiler will auto-detect the interfacegen processor on your `main.classpath` via a [Service Loader](http://java.sun.com/javase/6/docs/api/java/util/ServiceLoader.html) manifest file.

If you want to tweak some of the annotation processor options, you can be more explicit, e.g.:

<pre name="code" class="xml">
    &lt;javac destdir="bin/main"&gt;
      &lt;classpath refid="main.classpath"/&gt;
      &lt;src path="src/main/java"/&gt;
      &lt;compilerarg value="-s"/&gt;
      &lt;compilerarg value="bin/apt"/&gt;
      &lt;compilerarg value="-processor"/&gt;
      &lt;compilerarg value="org.interfacegen.processor.Processor"/&gt;
    &lt;/javac&gt;
</pre>

Briefly:

* `main.classpath` is a classpath reference that includes `interfacegen.jar`
* `-s bin/apt` configures where all APT-generated code is output (defaults to the `destdir`)
* `-processor org.interfacegen.processor.Processor` explicitly tells `javac` the processor class name (optional)

Eclipse
-------

The Eclipse annotation processing is more involved than `javac` configuration. It requires both 1.6 compiler output and also an explicit reference to `interfacegen.jar` instead of auto-detecting it on your classath.

To setup Eclipse, use the Project Properties. Right-click on your Eclipse project and go to:

<pre name="code">
  Project Properties:
    Java Compiler:
      Ensure the "Compiler compliance level" is 1.6
      Annotation Processing:
        Check "Enable annotation processing"
        Check "Enable processing in editor"
        Optionally set "Generated source directory" to something like "bin/apt"
        Factory Path:
          Click "Add JARs" and select interfacegen.jar
</pre>

If you are having problems, you want to ensure that under `Factory Path`, clicking the "Advanced"  button lists the `org.interfacegen.processor.Processor` class. If it does not, something is not setup correctly. Try the above steps again and potentially open/close the project and/or Eclipse itself.

