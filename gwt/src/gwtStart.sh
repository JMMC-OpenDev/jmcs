#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************

#/**
# \file
# start the GuiClient and one gateway syste to accept display of local
# applications.
#
# \synopsis
# gwtStart 
#
# \env
# OPTIONAL. If needed, environmental variables accessed by the program. For
# each variable, name, and usage description, as below.
# \envvar GILDASGUIPORT :  indicates tcp port number to use
# 
# */


# signal trap (if any)

if [ -z "$GILDASGUIPORT" ]
then
    echo "Please declare GILDASGUIPORT environment variable"
    exit 1
fi
    
if [ -f "../lib/GuiClient.jar" ]
then
    CPATH="../lib/GuiClient.jar"
elif [ -f "${0%/*}/../lib/GuiClient.jar" ]
then
    CPATH="${0%/*}/../lib/GuiClient.jar"
else
    echo "Can't find Jar file; please execute from the bin directory"
    exit 1
fi
   


echo Starting in background:
echo - one GatewayController on $GILDASGUIPORT port
echo - one GuiClient on localhost:$GILDASGUIPORT

java -classpath $CPATH laog/jmmc/gateway/GatewayController $GILDASGUIPORT $* &
java -classpath $CPATH laog/jmmc/gui/GuiClient localhost $GILDASGUIPORT $* &

#___oOo___
