#!/bin/bash
cd ../../../../../ && \
javac -d _build ./ru/ifmo/rain/konovalov/implementor/JarImplementor.java && \
cd _build && \
jar cfm _implemetor.jar ./../ru/ifmo/rain/konovalov/implementor/Manifest.txt \
     ru/ifmo/rain/konovalov/implementor/*.class \
     info/kgeorgiy/java/advanced/implementor/*.class && \
mv JarImplementor.jar ./../ru/ifmo/rain/konovalov/implementor && \
cd ./../ && \
rm -r -f _build