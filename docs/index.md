---
layout: default
title: interfacegen
---

interfacegen
============

Overview
--------

interfacegen makes boilerplate interfaces.

See [examples](examples.html) eventually.

Approach
--------

interfacegen uses code generation, but is implemented as a JDK6 annotation processor to provide (in Eclipse) a seamless editing/generation experience. The generated code is kept up to date as soon as "save" is hit.

When save is hit, interfacegen inspects the class that just changed and generates a mirror `IXxx` interface.

Sections
--------

* [Examples](examples.html)
* [Setup](setup.html)
* [Changelog](changelog.html)
* [Building](building.html)
* [Community](community.html)

