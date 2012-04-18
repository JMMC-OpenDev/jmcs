<?xml version="1.0"?>
<!--
********************************************************************************
* JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
********************************************************************************
NAME
jmcLatexIndex2HsTOC.xsl - convert the latex2html index file into java help TOC 

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
xmlns:exslt="http://exslt.org/common"
xmlns:math="http://exslt.org/math"
xmlns:date="http://exslt.org/dates-and-times"
xmlns:func="http://exslt.org/functions"
xmlns:set="http://exslt.org/sets"
xmlns:str="http://exslt.org/strings"
xmlns:dyn="http://exslt.org/dynamic"
xmlns:saxon="http://icl.com/saxon"
xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
xmlns:xt="http://www.jclark.com/xt"
xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
xmlns:test="http://xmlsoft.org/XSLT/"
xmlns:xhtml="http://www.w3.org/1999/xhtml"
extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
exclude-result-prefixes="math str">
<xsl:output omit-xml-declaration="yes" indent="no"/>
<xsl:param name="directory"></xsl:param>
<xsl:template match="/">
<toc>
<xsl:apply-templates select="//xhtml:ul[not(./ancestor::xhtml:ul)]"/>
</toc>
</xsl:template>

<xsl:template match="xhtml:ul">
<xsl:for-each select="xhtml:li">
<xsl:element name="tocitem">
<xsl:attribute name="text">
<xsl:value-of select="normalize-space(xhtml:a)"/>
</xsl:attribute>
<xsl:attribute name="target">
    <xsl:if test="$directory">
        <xsl:value-of select="concat($directory,'.')"/>
    </xsl:if>
<xsl:value-of select="substring-before(xhtml:a/@href, '.htm')"/>
</xsl:attribute>
<xsl:value-of select="'&#10;'"/>
<xsl:apply-templates select="./xhtml:ul"/>
</xsl:element>
</xsl:for-each>
<xsl:value-of select="'&#10;'"/>
<xsl:value-of select="'&#10;'"/>
</xsl:template>

</xsl:stylesheet>

