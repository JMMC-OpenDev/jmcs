#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.12 2008-04-29 14:28:58 bcolucci Exp $"
#
# Makefile of jmcs module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.11  2008/04/22 12:57:35  mella
# Add castor handling
#
# Revision 1.10  2008/04/22 09:15:56  bcolucci
# Created FeedbackReport.
#
# Revision 1.9  2008/04/16 14:24:11  fgalland
# Removed installation of AppFramework-1.03.jar
#
# Revision 1.8  2008/04/16 14:15:27  fgalland
# Creation.
#
# Revision 1.7  2008/01/28 12:19:32  mella
# add jfits jar to installed files
#
# Revision 1.6  2007/03/22 15:07:52  mella
# Added ptplot jar into file to install
#
# Revision 1.5  2007/02/26 10:21:07  mella
# Replace LogGui jar file
#
# Revision 1.4  2007/02/19 08:52:39  mella
# Append logGui.jar
#
# Revision 1.3  2007/02/14 09:58:09  mella
# Add missing pngs
#
# Revision 1.2  2007/02/13 13:48:51  lafrasse
# Moved sources from sclgui/src/jmmc into jmcs/src/fr and rename packages
#
# Revision 1.1  2007/02/13 10:31:40  ccmgr
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
INCLUDES        =

#
# Libraries (public and local)
# ----------------------------
LIBRARIES       =
LIBRARIES_L     =

#
# <brief description of lllll library>
lllll_OBJECTS   =

#
# Scripts (public and local)
# --------------------------
SCRIPTS         = jmcsGenerateJavaFromXsd \
				  jmcsHTML2HelpSet
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
JARFILES        = jmcs

#
# <brief description of jmcs jar files>
jmcs_DIRS     = fr
jmcs_EXTRAS   = fr/jmmc/mcs/gui/logo.jpg

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = jmcsGenerateHelpsetFromHtml

#
# <brief description of jmcsGenerateHelpsetFromHtml java program>
jmcsGenerateHelpsetFromHtml_CLASS    = fr.jmmc.mcs.gui.jmcsGenerateHelpsetFromHtml

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
INSTALL_FILES = ../lib/loggui.jar \
		../lib/ptplot5.6.jar \
		../lib/jfits-0.93.jar \
		../lib/castor.jar \
		../lib/BrowserLauncher2-1_3.jar \
		../lib/xercesImpl.jar \
		../lib/xmlParserAPIs.jar \
		../lib/commons-httpclient-3.1.jar \
		../lib/commons-logging-1.1.1.jar \
		../lib/commons-codec-1.3.jar \
		../lib/xmlenc.jar \
		../lib/jhall.jar \
		../lib/jhelpdev.jar \

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
all:	do_castor do_all
	@echo " . . . 'all' done" 

do_castor:
	./jmcsGenerateJavaFromXsd.sh
	
clean : clean_castor clean_all 
	@echo " . . . clean done"

clean_castor:
	rm -rf fr/jmmc/mcs/gui/castor

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all
	@echo " . . . installation done"

#___oOo___
