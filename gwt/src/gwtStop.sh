#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
