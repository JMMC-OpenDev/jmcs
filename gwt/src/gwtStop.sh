#!/bin/bash
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: gwtStop.sh,v 1.1 2005-11-29 09:27:04 mella Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
#*******************************************************************************

#/**
# \file
# Stop processes launched by gwtStart command.
#
# \synopsis
# gwtStop
# 
# */


# signal trap (if any)
processIdList=$(ps ax|grep java|grep GuiClient |cut -d ' ' -f 2)

for processId in $processIdList
do
    kill $processId &> /dev/null
done

#___oOo___
