#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
    java -classpath $(mkfMakeJavaClasspath) org.exolab.castor.builder.SourceGenerator -types j2 -i ${MODEL_SCHEMA} -f -package fr.jmmc.mcs.gui.castor $*
else
    echo "Generated classes for $MODEL_SCHEMA up-to-date"
fi
