<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE Workset SYSTEM "http://dependency-analyzer.org/schema/dtd/workset-1.6.dtd">
<Workset version="6">
  <WorksetName>JMAL</WorksetName>
  <Options auto-reload="no" />
  <Variables>
    <Variable name="JMAL_LIB" value="/home/bourgesl/dev/jmal/lib/" />
    <Variable name="JMAL_DIST" value="/home/bourgesl/NetBeansProjects/jmal/dist/" />
  </Variables>
  <Classpath shortContainerNames="yes">
    <ClasspathPart type="bin-class">{JMAL_DIST}jmal.jar</ClasspathPart>
    <ClasspathPart type="bin-class">{JMAL_DIST}**/*.jar</ClasspathPart>
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