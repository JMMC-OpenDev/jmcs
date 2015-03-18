# Libraries that should be installed in your maven local repository:
# grep "mvn install" ../pom.xml

mvn install:install-file -Dfile=lib/jafama-2.0.jar -DgroupId=jafama -DartifactId=jafama -Dversion=2.0 -Dpackaging=jar
mvn install:install-file -Dfile=lib/sptype.jar -DgroupId=cds -DartifactId=sptype -Dversion=1.0 -Dpackaging=jar
