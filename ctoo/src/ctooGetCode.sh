#! /bin/bash

#*******************************************************************************
# JMMC project
#
# "@(#) $Id: ctooGetCode.sh,v 1.2 2004-09-15 14:47:24 gluck Exp $"
#
# who       when        what
# --------  --------    ------------------------------------------------
# lgluck    14/05/04    Created
#
#*******************************************************************************

#/**
# \file
# Interactive script to get standard templates for coding.
#
# \synopsis
# ctooGetCode 
#
# \details
# Interactive utility to get template files for code. Following templates are
# available :
#   -# c-main
#   -# c-procedure
#   -# h-file
#   -# c++-small-main
#   -# c++-class-file
#   -# c++-h-file
#   -# script
#   -# Makefile
# 
# \sa ctooGetTemplateForCoding.sh, ctooGetTemplateFile.sh, ctooGetTemplate.sh
#  
# \n 
# 
# */


# signal trap (if any)


# Print out the menu
cat <<xyz
--------------------------------------------------------------------------------
This menu allows you to copy one of the available templates with a filename of
your choice in the current directory (the output file must not already exist).


Templates are available for:

        1- c-main
        2- c-procedure
        3- h-file

        4- c++-small-main
        5- c++-class-file
        6- c++-h-file
        
        7- script
        
        8- Makefile
xyz

# Propose the user to enter his choice
echo -e "\n-> Enter the number corresponding to the template type you need or"
echo -e "   press <Enter> to exit: \c"

# Read the choice
read choice


# Treat the choice

# Test if the choice is not empty
if test -n "$choice"
then
    # Examine the choice
    case $choice in
        1)  # c-main choice
            ctooGetTemplateForCoding c-main
            ;;
        
        2)  # c-procedure choice
            ctooGetTemplateForCoding c-procedure
            ;;
        
        3)  # h-file choice
            ctooGetTemplateForCoding h-file
            ;;
        
        4)  # c++-small-main choice
            ctooGetTemplateForCoding c++-small-main 
            ;;
        
        5)  # c++-class-file choice
            ctooGetTemplateForCoding c++-class-file 
            ;;
        
        6)  # c++-h-file choice
            ctooGetTemplateForCoding c++-h-file
            ;;
        
        7)  # script file choice
            ctooGetTemplateForCoding script
            ;;
        
        8)  # Makefile choice
            ctooGetTemplateForCoding Makefile
            ;;
        
        *) echo "ERROR : invalid choice";;
        
    esac

else
    # <Enter> was pressed
    exit
fi

#
# ___oOo___
