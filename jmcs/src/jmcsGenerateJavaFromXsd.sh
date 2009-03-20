#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: jmcsGenerateJavaFromXsd.sh,v 1.6 2009-03-20 06:23:48 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.5  2008/06/19 13:05:38  bcolucci
# Change the XML model name.
#
# Revision 1.4  2008/06/11 07:13:53  mella
# Fix wrong commit
#
# Revision 1.2  2008/04/22 12:57:35  mella
# Add castor handling
#
# Revision 1.1  2008/04/16 14:15:27  fgalland
# Creation.
#
#
#*******************************************************************************

#/**
# @file
# brief Generates classes from xsd using castor.
# 
# */


#MODEL_SCHEMA=$(miscLocateFile mfmdl.xsd)
MODEL_SCHEMA=fr/jmmc/mcs/gui/ApplicationDataModel.xsd
if [ "$MODEL_SCHEMA" -nt "fr/jmmc/mcs/gui/castor" ]
then
    # generate model java source from xml schema
    echo "Generating classes for $MODEL_SCHEMA"
    echo " Using classpath : $(mkfMakeJavaClasspath)"
    java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGeneratorMain -i ${MODEL_SCHEMA} -f -package fr.jmmc.mcs.gui.castor $*
else
    echo "Generated classes for $MODEL_SCHEMA up-to-date"
fi
