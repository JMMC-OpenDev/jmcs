<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>

<!--
********************************************************************************
* JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
********************************************************************************
-->
    
    <xsl:param name="moduleName"/>
    <xsl:param name="cdfFilename"/>
    <xsl:param name="outputFilename"/>
    
    <xsl:variable name="autoGeneratedC"><xsl:text>/*
 * 
 * This file has been automatically generated
 * 
 * !!!!!!!!!!!  DO NOT MANUALLY EDIT THIS FILE  !!!!!!!!!!!
 */

 </xsl:text>
</xsl:variable>

    <!-- ********************************************************* -->
    <!-- This TEMPLATE is the main part                            -->
    <!-- it calls one main template for each cmd nodes             -->
    <!-- INPUT : the module name                                   -->
    <!-- OUTPUT: the generated files  usind <mod><MNEMO>CB.C       -->
    <!-- ********************************************************* -->
    <xsl:template match="/">
 
        <xsl:for-each select="/cmd">
            <xsl:variable name="properModuleName"><xsl:call-template name="convertcase">
                    <xsl:with-param name="toconvert" select="$moduleName"/>
                    <xsl:with-param name="conversion">proper</xsl:with-param>
            </xsl:call-template></xsl:variable>    
            <xsl:variable name="properMnemonic"><xsl:call-template name="convertcase">
                    <xsl:with-param name="toconvert" select="./mnemonic"/>
                    <xsl:with-param name="conversion">proper</xsl:with-param>
            </xsl:call-template></xsl:variable>   
            <xsl:document href="{$outputFilename}" method="text">
            <xsl:call-template name="cmdToJavaInterface">
            </xsl:call-template>
            </xsl:document> 
        </xsl:for-each>
    </xsl:template>
        
    <!-- ********************************************************* -->
    <!-- This TEMPLATE is the main part to generate the c++ header -->
    <!-- file from a given command node.                           -->
    <!-- ********************************************************* -->
    <xsl:template name="cmdToJavaInterface">
        <xsl:variable name="lowerMnemo"><xsl:call-template name="convertcase">
                <xsl:with-param name="toconvert" select="./mnemonic"/>
                <xsl:with-param name="conversion">lower</xsl:with-param>
        </xsl:call-template></xsl:variable>               
/**
 * Method for <xsl:value-of select="./mnemonic"/> command.
 * <xsl:value-of select="./desc" />
 * <xsl:for-each select=".//param">
 * @param <xsl:value-of select="concat(./name,concat(' ',./desc))" />
 </xsl:for-each>
 *
 * @return a message 
 */
public static String <xsl:value-of select="$lowerMnemo"/>(<xsl:for-each select=".//param">
     <xsl:call-template name="getJavaType">
         <xsl:with-param name="type"><xsl:value-of select="./type"/></xsl:with-param></xsl:call-template><xsl:value-of select="concat(' ',./name)" /><xsl:if test="not(position()=last())">, </xsl:if>
 </xsl:for-each>){

 return sendCommand("<xsl:value-of select="./mnemonic"/>",""
 <xsl:for-each select=".//param">                     + " -<xsl:value-of select="./name"/> " + <xsl:value-of select="./name"/>
     <xsl:if test="not(position()=last())"><xsl:value-of select="'&#xA;'"/></xsl:if>
 </xsl:for-each> );
 
 }
 
</xsl:template>


<xsl:template name='getJavaType'>
    <xsl:param name='type' />
    <xsl:choose>
        <xsl:when test='$type="integer"'>
            <xsl:value-of select='"int"' />
        </xsl:when>
        <xsl:when test='$type="string"'>
            <xsl:value-of select='"String"' />
        </xsl:when>
        <xsl:otherwise>
            <xsl:value-of select='$type' />
        </xsl:otherwise>
    </xsl:choose>
</xsl:template>

  <xsl:variable name="lcletters">abcdefghijklmnopqrstuvwxyz</xsl:variable>

  <xsl:variable name="ucletters">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

  <xsl:template name='convertcase'>
    <xsl:param name='toconvert' />

    <xsl:param name='conversion' />

    <xsl:choose>
      <xsl:when test='$conversion="lower"'>
        <xsl:value-of
        select="translate($toconvert,$ucletters,$lcletters)" />
      </xsl:when>

      <xsl:when test='$conversion="upper"'>
        <xsl:value-of
        select="translate($toconvert,$lcletters,$ucletters)" />
      </xsl:when>

      <xsl:when test='$conversion="proper"'>
        <xsl:call-template name='convertpropercase'>
          <xsl:with-param name='toconvert'>
            <xsl:value-of
            select="translate($toconvert,$ucletters,$lcletters)" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>
      
      <xsl:when test='$conversion="upfirst"'>
        <xsl:call-template name='convertpropercase'>
          <xsl:with-param name='toconvert'>
            <xsl:value-of
                select="$toconvert" />
          </xsl:with-param>
        </xsl:call-template>
      </xsl:when>

      <xsl:otherwise>
        <xsl:value-of select='$toconvert' />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name='convertpropercase'>
    <xsl:param name='toconvert' />

    <xsl:if test="string-length($toconvert) > 0">
      <xsl:variable name='f'
      select='substring($toconvert, 1, 1)' />

      <xsl:variable name='s' select='substring($toconvert, 2)' />

      <xsl:call-template name='convertcase'>
        <xsl:with-param name='toconvert' select='$f' />

        <xsl:with-param name='conversion'>upper</xsl:with-param>
      </xsl:call-template>

      <xsl:choose>
        <xsl:when test="contains($s,' ')">
        <xsl:value-of select='substring-before($s," ")' />

          
        <xsl:call-template name='convertpropercase'>
          <xsl:with-param name='toconvert'
          select='substring-after($s," ")' />
        </xsl:call-template>
        </xsl:when>

        <xsl:otherwise>
          <xsl:value-of select='$s' />
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

</xsl:stylesheet>
