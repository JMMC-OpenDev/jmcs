<?xml version="1.0" encoding="ISO-8859-1"?>
<!--
********************************************************************************
 JMMC project

 "@(#) $Id: errXmlToH.xsl,v 1.7 2005-02-15 12:55:56 gzins Exp $"

 History 
 ~~~~~~~
 $Log: not supported by cvs2svn $
 Revision 1.6  2005/02/15 07:34:14  gzins
 Added file header
 
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text"/>

    <xsl:variable name="modName">
        <xsl:value-of select="//@moduleName"/>
    </xsl:variable>

    <xsl:template match="/">

        <!-- Print Header -->

<xsl:text>/*
 * Error Include File    Created on ...
 *
 * This file has been generated by errXmlToH utility
 * 
 * !!!!!!!!!!!  DO NOT MANUALLY EDIT THIS FILE  !!!!!!!!!!!
 */

 /**
 * \file 
 * Generated file for the define list of errors.
 */
</xsl:text>

        <!-- For each error print associated define with doxygen comment-->

        <xsl:for-each select="//error">
            <xsl:text>#define </xsl:text>
            <xsl:value-of select="$modName" />
            <xsl:text>ERR_</xsl:text>
            <xsl:value-of select="errName" />
            <xsl:text> </xsl:text>
            <xsl:value-of select="@id" />
            <xsl:text>   /**&lt;  </xsl:text>
            <xsl:call-template name="PlaceXmlEntities">
                <xsl:with-param name="str" select="errFormat"/>
            </xsl:call-template>
            <xsl:text> */&#xA;</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="PlaceXmlEntities">
        <xsl:param name="str"/>
        <xsl:variable name="tmp">
            <xsl:call-template name="SubstringReplace">
                <xsl:with-param name="stringIn" select="$str"/>
                <xsl:with-param name="substringIn" select="'&gt;'"/>
                <xsl:with-param name="substringOut" select="'&amp;gt;'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="tmp2">
            <xsl:call-template name="SubstringReplace">
                <xsl:with-param name="stringIn" select="$tmp"/>
                <xsl:with-param name="substringIn" select="'&lt;'"/>
                <xsl:with-param name="substringOut" select="'&amp;lt;'"/>
            </xsl:call-template>
        </xsl:variable>
        
        <xsl:value-of select="$tmp2"/>
    </xsl:template>
    
    <xsl:template name="SubstringReplace">
        <xsl:param name="stringIn"/>
        <xsl:param name="substringIn"/>
        <xsl:param name="substringOut"/>
        <xsl:choose>
            <xsl:when
                test="contains($stringIn,$substringIn)">
                <xsl:value-of select="concat(substring-before($stringIn,$substringIn),$substringOut)"/>
                <xsl:call-template name="SubstringReplace">
                    <xsl:with-param name="stringIn" select="substring-after($stringIn,$substringIn)"/>
                    <xsl:with-param name="substringIn" select="$substringIn"/>
                    <xsl:with-param name="substringOut" select="$substringOut"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$stringIn"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
