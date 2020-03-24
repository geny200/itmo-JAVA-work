#!/bin/bash
cd ../../../../../
javac -d temp ./ru/ifmo/rain/konovalov/implementor/JarImplementor.java
cd temp
jar cfm JarImplementor.jar ./../ru/ifmo/rain/konovalov/implementor/Manifest.txt \
     ru/ifmo/rain/konovalov/implementor/*.class \
     info/kgeorgiy/java/advanced/implementor/*.class
mv JarImplementor.jar ./../ru/ifmo/rain/konovalov/implementor
cd ./../
rm -r -f temp