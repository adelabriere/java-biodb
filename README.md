TESTS
=====

The tests use the `JRIEngine` for running R from Java. This requires some setup as explained here.

`JRIEngine` is an implementation using [JRI](http://www.rforge.net/JRI/index.html), a Java/R Interface. It uses a native library, which must be findable by Java, and needs the variable environment `R_HOME` being properly set. 

On OSX:

 * Install `rJava` library in R.
 * Set `R_HOME` to the right path. You can find R home right value, by executing command `R.home()`.
 * Find the file `libjri.jnilib` inside the `R_HOME` path. For R version 3.2, it should be in `/Library/Frameworks/R.framework/Versions/3.2/Resources/library/rJava/jri`.
 * Make a symbolic link of `libjri.jnilib` inside one of the directories listed in the Java System property `java.library.path`. To get the value of this property, run `System.getProperties().getProperty("java.library.path");` in Java. One possibility is inside your personal Library path: `$HOME/Library/Java/Extensions`.
