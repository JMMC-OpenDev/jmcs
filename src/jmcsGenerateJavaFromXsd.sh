#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: jmcsGenerateJavaFromXsd.sh,v 1.1 2008-04-16 14:15:27 fgalland Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
#
#*******************************************************************************

#/**
# @file
# brief Generates classes from xsd using castor.
# 
# */


#MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
MODEL_SCHEMA=fr/jmmc/mcs/gui/ApplicationDataSchema.xsd
# generate model java source from xml schema
echo "Generating classes for $MODEL_SCHEMA"
echo " Using classpath : $(mkfMakeJavaClasspath)"
java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGenerator -i ${MODEL_SCHEMA} -f -package fr.jmmc.mcs.gui.castor $*
