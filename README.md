leveldb (https://code.google.com/p/leveldb/) for Android

Very simple bindings for using Leveldb from android.
There's an excellent project leveldbjni out there for using leveldb from Java, but it seems a bit too much. It has a bunch of dependencies, including a code generator, which might or might not work on Android. At least it is not trivial to get started.


Installation.

The only external dependencies are Android SDK and NDK. To add NDK support for eclipse, follow the Android documentation from http://tools.android.com/recent/usingthendkplugin (you don't need to add native support for the project, it has already been done).

Once that is done, import the project as usual into eclipse. For some reason Eclipse doesn't automatically build native projects (even if you have it enabled in general), so Project -> Build All is needed.