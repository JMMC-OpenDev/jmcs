#! /bin/sh
#*******************************************************************************
# JMMC project
#
# "@(#) $Id: mkfMakeExecutableDependencies.sh,v 1.4 2005-02-15 08:40:15 gzins Exp $" 
#
# History
# -------
# $Log: not supported by cvs2svn $
# gzins     26-Aug-2004  Adapted from VLT
# gzins     18-Nov-2004  Added MCS C++ libraries only when MCS and C++ are
#                        specified
# gzins     18-Nov-2004  Fixed bug related to MCS C++ libraries
#
#************************************************************************
#   NAME
#   mkfMakeExecutableDependencies - create the makefile to build an executable
# 
#   SYNOPSIS
#
#   mkfMakeExecutableDependencies <exeName> <objectList> <ldFlags> <libList>
# 
#   DESCRIPTION
#   Utility used by mkfMakefile to create the makefile to build an executable.
#   It is not intended to be used as a standalone command.
#
#   An executable depends on the objects and the libraries(1) that 
#   it uses, so the final target rule is:
#
#   ../bin/<exeName>: ... ../object/<obj-i>.o ... -l<lib-i> ...
#   <TAB>   $(CC) $(LDFLAGS) $(ldFlags)  \
#                   ...objects...  ...libraries...  -o ../bin/<exeName>
#
#   (1) see also GNU Make 3.64, 4.3.5 pag 37
#
#
#   This rule implies to check the state of update of each object and of
#   libraries. How to make an object is given by the implicit pattern rule 
#   defined in mkfMakefile. How to make a library is given by the explicit
#   rule created by mkfMakeLibraryDependencies.
#
#   The .dx itself depends to Makefile.
#
#   The rules is written to standard output.
#
#   <exeName>     The name of the  executable 
#                 (Without directory)
#
#   <objectList>  The list of the object used to build the executable
#                 (Without neither directory nor .a suffix)
#
#   <ldFlags>     additional link flags,
#
#   <libList>     the list of libraries needed to link.
#                 The list can contain "conventional" names (like RTAP)
#                 that direct the procedure to create the needed generation
#                 rules
#
#   FILES
#   $MCSROOT/include/mkfMakefile   
#
#   ENVIRONMENT
#
#   RETURN VALUES
#
#   SEE ALSO 
#   mkfMakefile, Makefile, (GNU) make
#
#   BUGS    
#
#----------------------------------------------------------------------

exeName=$1
objectList=$2
ldFlags=$3
libList=$4

echo "# Dependency file for program: ${exeName}"
echo "# Created automatically by mkfMakeExecutableDependencies -  `date '+%d.%m.%y %T'`"
echo "# DO NOT EDIT THIS FILE"

#
# define the dependency file dependent to the Makefile
echo "../object/${exeName}.dx: Makefile"
echo ""

#
# define PHONY the target, so make does not try to make the target
# when no object are specified. (due to the fact that the same list of objects is
# used to build the list of exe both to be produced and to be installed).
echo ".PHONY: ${exeName} "

#
# if the list of objects is not empty, the rule to build the exe is written on output.
if [ "${objectList}" != "" ]
then
    #
    # prepare the list of all objects (full filename)
    for member in ${objectList}
    do
        oList="${oList} ../object/${member}.o"
    done

    #
    # prepare the lists in the "-l<name>" format of all libraries used in linking
    # some library names can be conventional names that are expanded in a series of
    # predefined libraries or flags
    for member in ${libList}
    do
        case $member in

        MCS):
            mcs="yes"
            ;;

        C++):
            cpp="yes"
            case "`uname`" in

            HP-UX):
                case "`uname -r`" in
                B.10.20):
                      lList="${lList} -lstdc++ -ldce"
                      ;;
                B.11.00):
                      lList="${lList} -lstdc++"
                      ;;
                esac
                ;;

            SunOS):
                lList="${lList} -lstdc++ "
                ;;

            Linux):
                lList="${lList} -lstdc++ "
                ;;

            *):
                MESSAGE="$MESSAGE -- error: this Operating System is not supported"
                ;;
            esac
            ;;

        stdc++):
            lList="${lList} -l${member}"
            MESSAGE="$MESSAGE -- warning: in ${exeName}_LIB: please remove ${member} and use  C++  to link c++ libraries"
            ;;

        g++):
            MESSAGE="$MESSAGE -- warning: in ${exeName}_LIB: egcs does not provide g++ anylonger. Please remove ${member} and use  C++  to link c++ libraries"
            ;;

        iostream):
            MESSAGE="$MESSAGE -- warning: in ${exeName}_LIB: ${member} is not supported since GCC 2.7, please remove it."
            ;;

        *):
            lList="${lList} -l${member}"
            ;;
        esac
    done

    #
    # create a target with the <name> of the executable (make <name>)
    echo "${exeName}: ../bin/${exeName} "
    echo ""

    #
    # output the rule to build the executable file
    #
    echo "../bin/${exeName}: ${oList}  "
    echo "	-@echo == Building executable: ../bin/${exeName} "  

    #
    # if any, print wanings
    if [ "${MESSAGE}" != "" ]
    then
        echo "	-@echo"
        echo "	-@echo $MESSAGE"
        echo "	-@echo"
    fi

    echo "	-@echo"

    # There is the possibility to use or to use not shared libraries.
    # The dependency file is build only when either the source or Makefile is
    # touched, therefore to allow the user to chose at run time, make
    # variables are used so they get the actual value and something like  make
    # MAKE_NOSHARED=on  works.
    # build up the list of libraries with and without directive to use share
    # lib.

    libraryList="\$(GEN_LIBLIST)"
    libraryListNoshared="\$(GEN_LIBLIST_NOSHARED)"

    if [ "${lList}" != "" ]
    then
        libraryList="${libraryList} ${lList}"
        libraryListNoshared="${libraryListNoshared} \$(NOSHARED_ON) ${lList} \$(NOSHARED_OFF)"
    fi

    if [ "${mcs}" = "yes" ]
    then 
        if [ "${cpp}" = "yes" ]
        then 
            libraryList="${libraryList} \$(MCSCPP_LIBLIST)"
            libraryListNoshared="${libraryListNoshared} \$(MCSCPP_LIBLIST_NOSHARED)"
        else
            libraryList="${libraryList} \$(MCS_LIBLIST)"
            libraryListNoshared="${libraryListNoshared} \$(MCS_LIBLIST_NOSHARED)"
        fi
        libraryList="${libraryList} \$(MCSSTD_LIBLIST)"
        libraryListNoshared="${libraryListNoshared} \$(MCSSTD_LIBLIST)"
    fi

    # The target is build as a conditional statement controlled by variables
    # that can also be specified on the command line         
    echo "ifeq (\$(strip \$(MAKE_NOSHARED) \$(${exeName}_NOSHARED)),)"
    echo "	\$(AT)\$(PURIFY) \$(PURECOV) \$(LD) \$(CFLAGS) \$(LDFLAGS) \$(L_PATH) ${ldFlags} ${oList} ${libraryList}         -o ../bin/${exeName}"
    echo "else"
    echo "	\$(AT)\$(PURIFY) \$(PURECOV) \$(LD) \$(CFLAGS) \$(LDFLAGS) \$(L_PATH) ${ldFlags} ${oList} ${libraryListNoshared} -o ../bin/${exeName}"
    echo "endif"

    #
    echo "	-@echo"

else
    echo "# ${exeName}_OBJECTS is not defined. Nothing to do here."
    echo "# Makefile should define the action for target  '${exeName}:'"
fi

#
# ___oOo___
