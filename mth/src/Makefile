#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.6 2007-07-09 15:31:01 gluck Exp $"
#
# Makefile of mth module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.5  2007/07/09 12:52:06  gzins
# Added mthInterp
# Added targets related to Yorick plugin
#
# Revision 1.4  2006/06/09 12:15:24  gzins
# Moved mthNORM to mthGAUSSIAN_RANDOM
#
# Revision 1.3  2006/06/02 13:25:35  lsauge
# Add class mthGAUSSIAN_RANDOM
#
# Revision 1.2  2006/01/31 10:34:54  gzins
# Implemented mthBessKO function
#
# Revision 1.1  2006/01/31 01:13:28  sccmgr
# Fix directory structure and add additional files
#
#*******************************************************************************
# REMARKS
#    Makefile based on the VLT/ALMA Makefile
#
#-------------------------------------------------------------------------------

#
# user definable C-compilation flags
#USER_CFLAGS = 
#OPTIMIZE=3

#
# user definable javac compilation flags
#USER_JFLAGS = 

#
# additional include and library search paths
#USER_INC = 
#USER_LIB = 

#
# MODULE CODE DESCRIPTION:
# ------------------------
# As a general rule:  public file are "cleaned" and "installed"  
#                     local (_L) are not "installed".

#
# C programs (public and local)
# -----------------------------
EXECUTABLES     =
EXECUTABLES_L   = 

#
# <brief description of xxxxx program>
xxxxx_OBJECTS   =
xxxxx_LDFLAGS   =
xxxxx_LIBS      =

#
# special compilation flags for single c sources
#yyyyy_CFLAGS   = 

#
# Includes (.h) files (public only)
# ---------------------------------
INCLUDES        = mth.h                  \
                  mthBesselK.h           \
                  mthGAUSSIAN_RANDOM.h   \
                  mthInterp.h

#
# Libraries (public and local)
# ----------------------------
LIBRARIES       = mth
LIBRARIES_L     =

#
# Mathematical library 
mth_OBJECTS   = mthBesselK	        \
				mthInterp           \
				mthGAUSSIAN_RANDOM

#
# Scripts (public and local)
# --------------------------
SCRIPTS         =
SCRIPTS_L       =

#
# TCL scripts (public and local)
# ------------------------------
TCL_SCRIPTS     =
TCL_SCRIPTS_L   =

#
# <brief description of tttttt tcl-script>
tttttt_OBJECTS  =
tttttt_TCLSH    = 
tttttt_LIBS     = 

#
# TCL libraries (public and local)
# --------------------------------
TCL_LIBRARIES   =
TCL_LIBRARIES_L =

#
# <brief description of tttlll library>
tttlll_OBJECTS  = 

#
# Python scripts (public and local)
# ---------------------------------
PY_SCRIPTS     =
PY_SCRIPTS_L   =

#
# Jarfiles and their directories
# ---------------------------------
JARFILES        =

#
# <brief description of jjjfff jar files>
jjjfff_DIRS     =
jjjfff_EXTRAS   = 

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES=

#
# <brief description of jjjjjj java program>
jjjjjj_CLASS    =

#
# Command Definition Files (CDF)
# ---------------------------------
CDF_FILES       =

#
# man pages to be done
# --------------------
MANSECTIONS =
MAN1 =
MAN3 =
MAN5 =
MAN7 =
MAN8 =

#
# other files to be installed
#----------------------------
INSTALL_FILES =

#
# list of all possible C-sources (used to create automatic dependencies)
# ----------------------------------------------------------------------
CSOURCENAMES = \
	$(foreach exe, $(EXECUTABLES) $(EXECUTABLES_L), $($(exe)_OBJECTS)) \
	$(foreach lib, $(LIBRARIES) $(LIBRARIES_L), $($(lib)_OBJECTS))

#
#>>>>> END OF standard rules

#
# INCLUDE STANDARDS
# -----------------
MAKEDIR = $(MCSROOT)/include
include $(MAKEDIR)/mkfMakefile

#
# TARGETS
# -------
all:	do_all
	@echo " . . . 'all' done" 

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
	@echo " . . . installation done"

#
# Targets related to yorick plugin
# --------------------------------
YORICK := $(shell which yorick 2> /dev/null)
do_plugin:
	@if [ "$(YORICK)" == "" ]; then \
	    echo "ERROR - yorick program not found in your PATH"; \
	    echo "Please check your installation"; \
	    exit 1; \
	fi
	@(cd ../yorick; yorick -batch make.i; make all)

clean_plugin:
	@if [ "$(YORICK)" == "" ]; then \
	    echo "ERROR - yorick program not found in your PATH"; \
	    echo "Please check your installation"; \
	    exit 1; \
	fi
	@(cd ../yorick; yorick -batch make.i; make clean)

install_plugin:
	@if [ "$(YORICK)" == "" ]; then \
	    echo "ERROR - yorick program not found in your PATH"; \
	    echo "Please check your installation"; \
	    exit 1; \
	fi
	@(cd ../yorick; yorick -batch make.i; make install)


#___oOo___
