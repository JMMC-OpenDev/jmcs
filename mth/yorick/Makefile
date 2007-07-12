#*******************************************************************************
# JMMC project 
#
# Yorick plugin for MCS mathematical library 
#
# "@(#) $Id: Makefile,v 1.1 2007-07-09 12:52:42 gzins Exp $"
#
# History
# -------
# $Log: not supported by cvs2svn $
#  

# these values filled in by    yorick -batch make.i
Y_MAKEDIR=/home/users/gzins/DISTRIB/yorick/lib/yorick/2.1
Y_EXE=/home/users/gzins/DISTRIB/yorick/lib/yorick/2.1/bin/yorick
Y_EXE_PKGS=
Y_EXE_HOME=/home/users/gzins/DISTRIB/yorick/lib/yorick/2.1
Y_EXE_SITE=/home/users/gzins/DISTRIB/yorick/share/yorick/2.1

# ----------------------------------------------------- optimization flags

# options for make command line, e.g.-   make COPT=-g TGT=exe
COPT=$(COPT_DEFAULT)
TGT=$(DEFAULT_TGT)

# ------------------------------------------------ macros for this package

PKG_NAME=mth
PKG_INCLUDES=../include/$(PKG_NAME)Interp.h
PKG_I=$(PKG_NAME).i $(PKG_NAME)Wrapper.i

OBJS=

# change to give the executable a name other than yorick
PKG_EXENAME=yorick

# PKG_DEPLIBS=-Lsomedir -lsomelib   for dependencies of this package
PKG_DEPLIBS=../object/mthInterp.o -L$(INTROOT)/lib 
# set compiler (or rarely loader) flags specific to this package
PKG_CFLAGS=$(EXTRA_INC_PATH)
PKG_LDFLAGS=$(EXTRA_LIB_PATH)

# list of additional package names you want in PKG_EXENAME
# (typically Y_EXE_PKGS should be first here)
EXTRA_PKGS=$(Y_EXE_PKGS)

# list of additional files for clean
PKG_CLEAN=../object/$(PKG_NAME)Wrapper.h ../object/$(PKG_NAME)Wrapper.xml

# autoload file for this package, if any
PKG_I_START=
# non-pkg.i include files for this package, if any
PKG_I_EXTRA= mthPlugin.i

# *.gs gist files for this package 
PKG_GS = 

# -------------------------------- standard targets and rules (in Makepkg)

# set macros Makepkg uses in target and dependency names
# DLL_TARGETS, LIB_TARGETS, EXE_TARGETS
# are any additional targets (defined below) prerequisite to
# the plugin library, archive library, and executable, respectively
PKG_I_DEPS=$(PKG_I)
Y_DISTMAKE=distmake

include $(Y_MAKEDIR)/Make.cfg
include $(Y_MAKEDIR)/Makepkg
include $(Y_MAKEDIR)/Make$(TGT)

# override macros Makepkg sets for rules and other macros
# Y_HOME and Y_SITE in Make.cfg may not be correct (e.g.- relocatable)
Y_HOME=$(Y_EXE_HOME)
Y_SITE=$(Y_EXE_SITE)

# reduce chance of yorick-1.5 corrupting this Makefile
MAKE_TEMPLATE = protect-against-1.5

# ------------------------------------- targets and rules for this package

# simple example:
#myfunc.o: myapi.h
# more complex example (also consider using PKG_CFLAGS above):
#myfunc.o: myapi.h myfunc.c
#	$(CC) $(CPPFLAGS) $(CFLAGS) -DMY_SWITCH -o $@ -c myfunc.c

#
# Generation of Yorick wrapper
#
C2YORICK_XSL = $(shell miscLocateFile mkfSTKToYorickWrapperForC.xsl)

# Replace MCS types with C types, and remove const which disturb Yorick wrapper
# generation
../object/$(PKG_NAME)Wrapper.h: $(PKG_INCLUDES)
	cat $(PKG_INCLUDES) | sed 's/mcsINT32/int/g'| \
		sed 's/const//g' | \
		sed 's/mcsDOUBLE/double/g' | \
		sed 's/mcsCOMPL_STAT/int/g' > ../object/$(PKG_NAME)Wrapper.h

../object/$(PKG_NAME)Wrapper.xml: ../object/$(PKG_NAME)Wrapper.h
	swig -DEXPORT -xml -module $(PKG_NAME) -o  ../object/$(PKG_NAME)Wrapper.xml\
    ../object/$(PKG_NAME)Wrapper.h 

update: ../object/$(PKG_NAME)Wrapper.xml
	xsltproc --path ../config -o $(PKG_NAME)Wrapper.i $(C2YORICK_XSL) \
		../object/$(PKG_NAME)Wrapper.xml
	sed 's/void \*thisPtr/int thisPtr/'  $(PKG_NAME)Wrapper.i > \
		$(PKG_NAME)Wrapper_tmp.i
	mv $(PKG_NAME)Wrapper_tmp.i $(PKG_NAME)Wrapper.i

install::
	@echo "Installing into target: $(Y_SITE)"
	@echo ".....include:"
	@echo "           $(PKG_I) $(PKG_I_EXTRA)"
	@cp -f $(PKG_I) $(PKG_I_EXTRA) $(Y_SITE)/i
	@echo ".....gist:"
	@echo "Installing into target: $(INTROOT)"
	@echo ".....library:"
	@echo "           $(PKG_NAME).so"
	@cp -f $(PKG_NAME).so $(INTROOT)/lib

# -------------------------------------------------------- end of Makefile
