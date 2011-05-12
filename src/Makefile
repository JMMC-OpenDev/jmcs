#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
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
# Most of required jars are installed during Jnlp deployement
# leave here the jars that are used by executables but not jnlp applications
# See ../lib/README.txt
INSTALL_FILES = ../lib/Tidy.jar \
				../lib/jhelpdev.jar \
				../lib/xmlenc.jar \
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
