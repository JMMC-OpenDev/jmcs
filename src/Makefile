#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.32 2010-02-03 09:37:30 bourgesl Exp $"
#
# Makefile of jmcs module
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.31  2010/02/03 09:27:07  bourgesl
# added lut files in jmcs jar file
#
# Revision 1.30  2010/01/18 15:27:00  lafrasse
# Added installation of sptype.jar for jmcsLD2UD to work outside of jmcs/* .
#
# Revision 1.29  2010/01/07 10:37:31  lafrasse
# Renamed all executables to include the mandatory 'jmcs' prefix.
# Added jmcsLD2UD executable.
#
# Revision 1.28  2009/12/01 14:18:00  lafrasse
# Added 'ALX' executable for test purpose.
#
# Revision 1.27  2009/11/27 15:57:39  lafrasse
# Added fr/jmmc/mcs/astro/Catalog;
#
# Revision 1.26  2009/10/07 15:59:32  lafrasse
# Added StarResolverWidget.
#
# Revision 1.25  2009/10/06 15:54:57  lafrasse
# *** empty log message ***
#
# Revision 1.24  2009/06/26 09:40:52  mella
# fix generation of TOC into the javaHelpSet
#
# Revision 1.23  2009/03/21 07:20:29  mella
# revert to previous castor
#
# Revision 1.22  2009/03/20 10:03:36  mella
# remove unsed jar
#
# Revision 1.21  2009/03/20 06:23:48  mella
# move to castor1.3
#
# Revision 1.20  2009/01/15 16:00:14  mella
# user can give a module name or existing html directory
#
# Revision 1.19  2008/06/23 07:45:49  bcolucci
# Use apache common lang library.
#
# Revision 1.18  2008/06/13 08:11:18  bcolucci
# *** empty log message ***
#
# Revision 1.17  2008/06/11 08:17:19  mella
# Get all png file from fr/jmmc/mcs/gui dir
#
# Revision 1.16  2008/05/27 14:21:55  mella
# Add jnlp deployement into install target
#
# Revision 1.15  2008/05/26 11:26:45  mella
# Add jmcsDeployJnlp script
#
# Revision 1.14  2008/05/20 08:43:10  bcolucci
# Added Tidy and getopt jars.
#
# Revision 1.13  2008/05/16 12:31:49  bcolucci
# Added default Application Data Model values.
#
# Revision 1.12  2008/04/29 14:28:58  bcolucci
# Added JavaHelp support and automatic documentation generation from HTML.
#
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
				  jmcsHTML2HelpSet \
				  jmcsDeployJnlp
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
jmcs_EXTRAS   = fr/jmmc/mcs/gui/*.png \
                fr/jmmc/mcs/gui/ApplicationData.xml \
                fr/jmmc/mcs/image/lut/*.lut

#
# Java programs
# ---------------------------------
JAVA_EXECUTABLES = jmcsStarResolver \
                   jmcsStarResolverWidget \
				   jmcsCatalogs \
				   jmcsALX \
				   jmcsLD2UD

#
# <brief description of jmcsGenerateHelpsetFromHtml java program>
jmcsStarResolver_CLASS        = fr.jmmc.mcs.astro.star.StarResolver
jmcsStarResolverWidget_CLASS  = fr.jmmc.mcs.astro.star.StarResolverWidget
jmcsCatalogs_CLASS            = fr.jmmc.mcs.astro.Catalog
jmcsALX_CLASS                 = fr.jmmc.mcs.astro.ALX
jmcsLD2UD_CLASS               = fr.jmmc.mcs.astro.LD2UD

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
		../lib/Tidy.jar \
		../lib/java-getopt-1.0.13.jar \
		../lib/AppleJavaExtensions.jar \
		../lib/commons-lang-2.4.jar \
		../lib/sptype.jar \
		../config/jmcsLatexIndex2HsTOC.xsl

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
	@echo "Deploy Jmcs.jnlp and install Jmcs.jar"
	jmcsDeployJnlp Jmcs.jnlp
	@cp $$HOME/public_html/Jmcs/Jmcs.jar $(INSTALL_ROOT)/lib
	@echo " . . . installation done"

#___oOo___
