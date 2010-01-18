---
layout: default
title: Building
---

Building
========

Note that this is for building interfacegen itself--to use interfacegen, you can just use a binary distribution from the [Joist maven repo](http://repo.joist.ws), as described in [setup](setup.html).

Overview
--------

The source for interfacegen is available in the [interfacegen](http://github.com/stephenh/interfacegen) project on GitHub.

interfacegen currently consists of two projects:

* `interfacegen` which has the annotation processor implementation and
* `examples` which provides an integration-level test suite.

Building `interfacegen` is supported via ant or Eclipse.

Building `examples` is supported via ant, Eclipse, or maven. Having `examples` also built via maven ensures `interfacegen` works correctly in its environment.

Building `interfacegen` in Ant
-------------------------

In the `interfacegen/` directory, run `ant jar`. This will create a new `bin/jars/interfacegen.jar` with interfacegen and its dependencies `jarjar`-ed together.

That's it--this one is fairly simple.

Building `interfacegen` in Eclipse
-----------------------------

The `interfacegen` Eclipse project leverages Eclipse RCP to run and debug a separate, child instance of Eclipse for the `examples` project.

This means you can debug the annotation processor as it is running over the `examples` project. This is very useful for development purposes and we extend huge thanks to [Walter Harley](http://www.cafewalter.com/), an Eclipse APT engineer, for introducing us to this method.

This does mean that you'll need the [Eclipse for RCP/Plug-in Developers](http://www.eclipse.org/downloads/) distribution of Eclipse instead of just the Java or Java EE distribution.

After you import `interfacegen/.project` into an Eclipse RCP workspace, the combination of the `plugin.xml`, `META-INF/MANIFEST.MF`, the PluginNature in `.project`, and the `examples/lib/annotations.jar` (updated by running `ant annotations` when you change any interfacegen public API), means you should be able to launch the `examples.launch` target.

This will create a new instance of Eclipse. Initially the workspace will be empty, so you will need to import the `examples/.project` file.

Now you should be set. Setting debug points in the interfacegen implementation and then either saving files or running clean in the `examples` Eclipse should hit your debug points.

Building `examples` in Ant
--------------------------

Run `ant tests`.

Note that this will use the latest `interfacegen/bin/jars/interfacegen.jar`, so you will need to run `ant jar` in the `interfacegen` project first (and each time you want to see your interfacegen changes in the `examples` project).

Building `examples` in Eclipse
------------------------------

See the previous section on building `interfacegen` in Eclipse--the RCP child Eclipse instance that runs against `examples` is the best way of working with the `examples` project in Eclipse.

Building `examples` in Maven
----------------------------

To test the snapshot version of interfacegen, first in `interfacegen` run `ant ivy.publish-maven-user` to get `interfacegen-SNAPSHOT` into your `~/.m2/repository`.

Now in `examples` run `mvn clean test`.

