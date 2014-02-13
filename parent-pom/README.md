PARENT POM
==========

This directory contains the jMCS parent pom.
It may be used for any other project that want to use it as parent.

Use it giving *relativePath* or proceed first to its installation.

### Install:

```bash
cd parent-pom
mvn install
```

### Use:

```xml
<parent>
    <groupId>fr.jmmc</groupId>
    <artifactId>jmmc</artifactId>
    <version>TRUNK</version>
    <!-- <relativePath>./parent-pom</relativePath> -->
</parent>

<properties>
    <jarsigner.skip>true</jarsigner.skip>
    ...
</properties>
```

Add *jarsigner.skip=true* to your properties if you want to prevent archive signing. Else you will have to prepare a keystore and provide the following properties to make signing process valid:

```xml
<properties>
...
    <jarsigner.keystore>/home/MCS/etc/globalsign.jks</jarsigner.keystore>
    <jarsigner.alias>codesigningcert</jarsigner.alias>
    <jarsigner.storepass>Osug2013DC</jarsigner.storepass>
    <jarsigner.keypass>Osug2013DC</jarsigner.keypass>
...
</properties>
```

