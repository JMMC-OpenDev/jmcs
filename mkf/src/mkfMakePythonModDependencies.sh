#! /bin/bash
#*******************************************************************************
# E.S.O. - VLT project
#
# "@(#) $Id: mkfMakePythonModDependencies.sh,v 1.2 2008-05-25 14:22:50 gzins Exp $" 
#
# who       when      what
# --------  --------  ----------------------------------------------
# almamgr 2002-03-01 Now /bin/ksh
# gfilippi  24/10/94  created
# gfilippi  25/10/94  stop on error ("-" removed from the make command)
# gfilippi  08/12/94  handle one script at time
# gfilippi  20/12/95  use +wx (cmmCopy gives r--r--r-- files)
# rschmutz 1999-04-03 use mkfMakeScript to copy the script to ../bin.

#************************************************************************
#   NAME
#   mkfMakePythonModDependencies - create the makefile to build one python module
# 
#   SYNOPSIS
#
#   mkfMakePythonModDependencies <moduleName>
# 
#   DESCRIPTION
#   Utility used by mkfMakefile to create the makefile to build one scripts.
#   It is not intended to be used as a standalone command.
#
#   Each ../bin-executable script depends on the source file:
#
#   ../lib/site-packages/<moduleName>.py: <moduleName>.py Makefile
#   <TAB>   -$(AT) echo "== Making module: $(moduleName).py"
#   <TAB>   -$(AT) mkfMakeScript $(moduleName)
#
#   The rule is written to standard output.
#
#   <moduleName>  The script to be treated
#
#
#   FILES
#   $VLTROOT/include/mkfMakefile.all  
#
#   ENVIRONMENT
#
#   RETURN VALUES
#
#   SEE ALSO 
#   mkfMakefile.all, Makefile, (GNU) make
#
#   GNU make - Edition 0.41, for make Version 3.64 Beta, April 93
#   VLT Software - Programming Standard - 1.0 10/03/93
#
#   BUGS    
#
#----------------------------------------------------------------------

echo "PYTHON MODULE = $1"
if [ "${1}" != "" ]
then
    moduleName=$1
else
    echo "ERROR: mkfMakePythonModDependencies called with no parameters" >&2
    exit 1
fi

echo "# Dependency file for module: ${moduleName}"
echo "# Created automatically by mkfMakePythonModDependencies -  `date '+%d.%m.%y %T'`"
echo "# DO NOT EDIT THIS FILE"

#
# define the dependency file dependent to the Makefile
echo "../object/${moduleName}.dpms: Makefile"
echo ""

#
# write on output the rule to build the script.
echo "../lib/python/site-packages/${moduleName}.py: ${moduleName}.py Makefile"

echo "	@echo \"== Making python module: ../lib/python/site-packages/${moduleName}.py\" "
echo "	\$(AT)mkfMakePythonModule ${moduleName} "
#
# ___oOo___
