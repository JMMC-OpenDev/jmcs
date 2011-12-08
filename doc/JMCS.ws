<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.6.dtd">
<Workset version="6">
  <WorksetName>JMCS</WorksetName>
  <Options auto-reload="no" />
  <Variables>
    <Variable name="JMCS_LIB" value="/home/bourgesl/dev/jmcs/lib/" />
    <Variable name="JMCS_DIST" value="/home/bourgesl/NetBeansProjects/jmcs/dist/" />
  </Variables>
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">{JMCS_DIST}jmcs.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}AppleJavaExtensions.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}BrowserLauncher2-1_3.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}activation.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}commons-codec-1.3.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}commons-httpclient-3.1.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}commons-lang-2.6.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}java-getopt-1.0.13.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jaxb-api.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jaxb-impl.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jcl-over-slf4j-1.6.4.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jhall.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jsamp-1.3.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jsr173_1.0_api.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}jul-to-slf4j-1.6.4.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}logback-classic-1.0.0.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}logback-core-1.0.0.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}slf4j-api-1.6.4.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMCS_LIB}swing-worker-1.2.jar</ClasspathPart>
  </Classpath>
  <ViewFilters>
    <PatternFilter active="yes">java.*</PatternFilter>
    <PatternFilter active="yes">javax.*</PatternFilter>
    <PatternFilter active="yes">com.sun.*</PatternFilter>
    <PatternFilter active="yes">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes">org.omg.*</PatternFilter>
    <PatternFilter active="yes">org.w3c.dom.*</PatternFilter>
  </ViewFilters>
  <IgnoreFilters>
    <PatternFilter active="yes">java.*</PatternFilter>
    <PatternFilter active="yes">javax.*</PatternFilter>
    <PatternFilter active="yes">com.sun.*</PatternFilter>
    <PatternFilter active="yes">org.xml.sax*</PatternFilter>
    <PatternFilter active="yes">org.omg.*</PatternFilter>
    <PatternFilter active="yes">org.w3c.dom.*</PatternFilter>
  </IgnoreFilters>
  <Architecture>
    <ComponentModel name="Default" />
  </Architecture>
</Workset>