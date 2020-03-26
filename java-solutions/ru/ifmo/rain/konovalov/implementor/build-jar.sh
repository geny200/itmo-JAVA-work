#!/bin/bash
mkdir "_build" && \
javac -d "./_build" -cp "../../../../../../../java-advanced-2020/artifacts/*" "*.java" && \
cd "./_build" && \
jar xf ../../../../../../../../java-advanced-2020/artifacts/info.kgeorgiy.java.advanced.implementor.jar \
info/kgeorgiy/java/advanced/implementor/Impler.class \
info/kgeorgiy/java/advanced/implementor/JarImpler.class \
info/kgeorgiy/java/advanced/implementor/ImplerException.class && \
jar cfm _implemetor.jar ./../Manifest.txt \
     ru/ifmo/rain/konovalov/implementor/*.class \
     info/kgeorgiy/java/advanced/implementor/*.class && \
mv _implemetor.jar ./../ && \
cd ./../ && \
rm -r -f _build