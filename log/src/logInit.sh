#!/bin/sh
#
# mcsLogManger:      MCS Logging Server
#
# Version:      0.1
# processname:  logManager
# description:	Starts and stops the MCS Logging File Server \
#		at boot time and shutdown.
# chkconfig: 2345 60 60

#******************************************************************************* 
# JMMC project
#
# "@(#) $Id: logInit.sh,v 1.1 2005-03-07 13:30:36 mella Exp $"
#
# History:
# --------
# $Log: not supported by cvs2svn $
#******************************************************************************* 

#/**
#  \file 
# MCS Logging Service management script.
# 
# \synopsis
# logInit {start|stop|status|restart}
#
# \details
# This script is used by the init system. You need to copy it under /etc/init.d
# and run chkconfig --add logInit . You can change the name if you prefer.
#
# */


# Source function library.
. /etc/rc.d/init.d/functions

# Source MCS environment stuff
export MCSROOT=/home/MCS
if [ -f $MCSROOT/etc/mcs.sh ]
then
    # gprintf "Working with MCS Environment present in %s/etc/mcs.sh" "$MCSROOT"
    . $MCSROOT/etc/mcs.sh
else
    gprintf "Sorry MCS Environment not found in %s/etc/mcs.sh" "$MCSROOT"
    failure
    echo
    exit 1
fi

# Define some usefull program name and process id
prog="logManager"
processId="logManager"

#handle parameter
case "$1" in
  start)
  	gprintf "Starting %s: " "$prog"
    echo
   
    if [ `pidofproc $processId` ]
       then
           gprintf "Sorry, %s is already running" "$prog"
           failure
       else
       # start real process
       # su swmgr -c "$prog &"
       daemon --user swmgr $prog &
       # $prog &
       if [ $? -eq 0 ]
           then
           	success 
           else
           	failure
           fi
       fi
    echo
	;;
  stop)
	gprintf "Shutting down %s: " "$prog"
	killproc $processId
	echo
	;;
  status)
    gprintf "Working with MCS Environment present in %s/etc/mcs.sh" "$MCSROOT"
    echo
	status $processId
	;;
  restart)
  	gprintf "Re" 
  	gprintf "Starting %s: " "$prog"
	echo
        $0 stop
	$0 start
	;;
  *)
	gprintf "*** Usage: %s {start|stop|status|restart}\n" "$prog"
	exit 1
esac

exit 0
