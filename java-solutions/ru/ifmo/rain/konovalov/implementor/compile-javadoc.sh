#!/bin/bash
cd ../../../../../ && \
javadoc -d "./ru/ifmo/rain/konovalov/implementor/javadoc" \
-link https://docs.oracle.com/en/java/javase/13/docs/api \
-cp "./../../java-advanced-2020/modules/*;./../../java-advanced-2020/lib/*;" \
-private \
-author \
-version ru.ifmo.rain.konovalov.implementor \
./../../java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/ImplerException.java \
./../../java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/Impler.java \
./../../java-advanced-2020/modules/info.kgeorgiy.java.advanced.implementor/info/kgeorgiy/java/advanced/implementor/JarImpler.java