#*******************************************************************************
# JMMC project
#
# "@(#) $Id: Makefile,v 1.11 2005-11-29 09:27:04 mella Exp $"
#
# Makefile of gwt module 
#
# History
# -------
# $Log: not supported by cvs2svn $
# Revision 1.10  2005/02/24 10:36:46  mella
# Add new LABEL Widget
#
# Revision 1.9  2005/02/15 12:26:39  gzins
# Removed unused target
#
# Revision 1.8  2005/02/15 12:17:52  gzins
# Added CVS log as file modification history
#
# mella     13-Sep-2004  Created
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
INCLUDES        = gwt.h \
				  gwtGUI.h \
				  gwtWIDGET.h \
				  gwtCONTAINER.h \
				  gwtWINDOW.h \
				  gwtBUTTON.h \
				  gwtTEXTFIELD.h \
				  gwtTEXTAREA.h \
				  gwtTABLE.h \
				  gwtCELL.h \
				  gwtMENU.h \
				  gwtMENUITEM.h \
				  gwtXML_PRODUCER.h \
				  gwtCOMMAND.h \
				  gwtSEPARATOR.h \
				  gwtCHOICE.h \
				  gwtCHECKBOX.h \
				  gwtLABEL.h \
				  gwtSUBPANEL.h


#
# Libraries (public and local)
# ----------------------------
LIBRARIES       = gwt
LIBRARIES_L     =

#
# <brief description of lllll library>
gwt_OBJECTS   =	gwtGUI \
				gwtWIDGET \
				gwtCONTAINER \
				gwtWINDOW \
				gwtBUTTON \
				gwtTEXTFIELD \
				gwtTEXTAREA \
				gwtTABLE \
				gwtCELL \
				gwtMENU \
				gwtMENUITEM \
				gwtCOMMAND \
				gwtXML_PRODUCER \
				gwtSEPARATOR \
				gwtCHOICE \
				gwtCHECKBOX \
				gwtLABEL \
				gwtSUBPANEL

#
# Scripts (public and local)
# --------------------------
SCRIPTS         = gwtStart \
				  gwtStop
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
JAVA_JAR = GuiClient.jar
#
# INCLUDE STANDARDS
# -----------------
MAKEDIR = $(MCSROOT)/include
include $(MAKEDIR)/mkfMakefile

#
# TARGETS
# -------
all:	copy_local_jar do_all
	@echo " . . . 'all' done" 

copy_local_jar:
	@echo "Copy local JAR files into ../lib"
	@$(foreach file, $(JAVA_JAR), echo "        $(file)" ;\
	cp -f ../jar/$(file) ../lib;)
	@echo ""

clean : clean_all 
	@echo " . . . clean done"

man   : do_man 
	@echo " . . . man page(s) done"

install : install_all install_local_jar
	@echo " . . . installation done"

install_local_jar:
	@echo "Copy local JAR files into $(INSTALL_ROOT)/lib"
	@$(foreach file, $(JAVA_JAR), echo "        $(file)" ;\
	      cp -f ../lib/$(file) $(INSTALL_ROOT)/lib;)
	@echo ""
#___oOo___
