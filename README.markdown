
Intro
=====

interfacegen is meant to relieve some of the boilerplate crap that happens on Java projects.

Unfortunately, Interfaces Are Nice
==================================

When testing Java-based systems, it is often times easier to mock/stub out an interface than a class.

E.g. a constructor of:

    public class Foo(ISomeInterface foo) {
    }

Is more likely to be testable than:

    public class Foo(SomeClass foo) {
    }

Because `SomeClass` will have constructor parameters/etc. that depend on real-world things.

Unfortunately, This Means One Interface Per Class
=================================================

Having an interface with only one implementation, just to satisfy mocking/whatever is very annoying.

So, interfacegen is here to help.

If you provide:

    @GenInterface
    public class SomeClass implements ISomeClass {
        public void doFoo() {
           // implementation
        }
    }

interfacegen will spit out:

    public interface ISomeClass {
        void doFoo();
    }

Any methods you add/remove change to `SomeClass` will automatically get updated in `ISomeClass`.

You are basically driving the definition of the interface from its implementation class.

Backwards? Yes.

But so is the whole one-implementation-per-interface-so-we-can-mock predicament.

This is just a band-aid.

