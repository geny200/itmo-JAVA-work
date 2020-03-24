#!/bin/bash
cd ../../../../../ && \
javadoc -d "./ru/ifmo/rain/konovalov/implementor/javadoc" \
-link https://docs.oracle.com/en/java/javase/13/docs/api \
-cp "./ru/ifmo/rain/konovalov/implementor/JarImplementor.jar;./../lib/*;" \
-private \
-author \
-version ru.ifmo.rain.konovalov.implementor \
./info/kgeorgiy/java/advanced/implementor/ImplerException.java \
./info/kgeorgiy/java/advanced/implementor/Impler.java \
./info/kgeorgiy/java/advanced/implementor/JarImpler.java