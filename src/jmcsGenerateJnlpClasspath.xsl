<?xml version="1.0"?>
<!--
Help to get JNLP and application Data package list uptodate
Usage sample (from XXX/jmcs/src):
xsltproc jmcsGenerateJnlpClasspath.xsl fr/jmmc/mcs/gui/ApplicationData.xml
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:str="http://exslt.org/strings"
 extension-element-prefixes="str"
 exclude-result-prefixes="str">
<xsl:output omit-xml-declaration="yes" indent="no"/>
<xsl:template match="/">

<xsl:comment>Begin of generated list from ApplicationData.xml</xsl:comment>
<xsl:value-of select="'&#10;'"/>
  <xsl:for-each select="//dependences/package">
  <xsl:comment>
  <xsl:value-of select="concat(@name,' - ')"/>
  <xsl:value-of select=".//preceding-sibling::comment()[1]"/>
  </xsl:comment>
  <xsl:for-each select="str:split(./@jars)">
  <xsl:element name="jar">
    <xsl:attribute name="href">
    <xsl:value-of select="concat('jar/',.)"/>
    </xsl:attribute>
  </xsl:element>
    <xsl:value-of select="'&#10;'"/>
  </xsl:for-each>
  <xsl:value-of select="'&#10;'"/>
  </xsl:for-each>

  <xsl:comment> End of generated list </xsl:comment>

  </xsl:template>


</xsl:stylesheet>
