#!/bin/bash
# Java program file: ../bin/jmalTestConcurrentModel

# Generate the class path for Java
CLASSPATH=`mkfMakeJavaClasspath`

echo "Java version:"
java -version

echo "Starting Benchmark ..."

# fixed java heap settings + headless mode:    
java -client -Xms384m -Xmx384m -Djava.awt.headless=true -classpath $CLASSPATH fr.jmmc.jmal.model.test.ConcurrentModelTest "$@"
