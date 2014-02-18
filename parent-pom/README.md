PARENT POM
==========

This directory contains the jMCS parent pom.
It may be used for any other project that want to use it as parent.

Proceed first to its installation.

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
</parent>
```

By default parent pom sign jar of classes. To skip this operation (developer profile), please set the *jarsigner.skip=true* property.
Signing step requires to prepare a keystore and some properties (see below) to make signing process valid
Else you will have :

```xml
<properties>
...
    <jarsigner.keystore>/home/MCS/etc/globalsign.jks</jarsigner.keystore>
    <jarsigner.alias>codesigningcert</jarsigner.alias>
    <jarsigner.storepass>Osug2013DC</jarsigner.storepass>
    <jarsigner.keypass>Osug2013DC</jarsigner.keypass>
    <jarsigner.skip>true</jarsigner.skip>
...
</properties>
```

