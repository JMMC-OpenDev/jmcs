<?xml version="1.0"?>
<!--
********************************************************************************
*                  jMCS project ( http://www.jmmc.fr/dev/jmcs )
********************************************************************************
*  Copyright (c) 2013, CNRS. All rights reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions are met:
*      - Redistributions of source code must retain the above copyright
*        notice, this list of conditions and the following disclaimer.
*      - Redistributions in binary form must reproduce the above copyright
*        notice, this list of conditions and the following disclaimer in the
*        documentation and/or other materials provided with the distribution.
*      - Neither the name of the CNRS nor the names of its contributors may be
*        used to endorse or promote products derived from this software without
*        specific prior written permission.
*
*  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
*  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
*  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
*  ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
*  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
*  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
*  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
*  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
*  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
********************************************************************************
-->

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
