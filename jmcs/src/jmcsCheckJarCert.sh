#!/bin/bash
################################################################################
#                  jMCS project ( http://www.jmmc.fr/dev/jmcs )
################################################################################
#  Copyright (c) 2013, CNRS. All rights reserved.
#
#  Redistribution and use in source and binary forms, with or without
#  modification, are permitted provided that the following conditions are met:
#      - Redistributions of source code must retain the above copyright
#        notice, this list of conditions and the following disclaimer.
#      - Redistributions in binary form must reproduce the above copyright
#        notice, this list of conditions and the following disclaimer in the
#        documentation and/or other materials provided with the distribution.
#      - Neither the name of the CNRS nor the names of its contributors may be
#        used to endorse or promote products derived from this software without
#        specific prior written permission.
#
#  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
#  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
#  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
#  ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
#  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
#  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
#  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
#  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
#  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
#  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
################################################################################

# define script name
SCRIPTNAME=$(basename "$0" 2> /dev/null)

# define ModuleArea
# (leave endding slash to allow files retrieval even if some dirs are aliases
SCRIPTROOT=$(cd ..; pwd)/

# define command root
COMMANDROOT=$(pwd)

_usage()
{
echo
echo "Usage: $SCRIPTNAME [--verbose] [--help] <JNLP files>"
echo "  --verbose        : optional - request verbose messages."
echo "  <JNLP file>      : this (xml) file must be well formed and have 'jnlp' extension"
echo "" 
echo "" 
echo "  This command try to read certificates on given jars and complains for any files that get multiples signatures. This can detect possible conflict before deployement of new releases."
echo ""
echo "  Examples:"
echo "    # $SCRIPTNAME App.jar"
echo "" 
echo ""
}

shllibSetDebugOn()
{
    export ECHOTRACEON=true
}
shllibSetDebugOff()
{
    unset ECHOTRACEON
}
shllibEchoError ()
{
    ARGS=$*
        echo -e "ERROR>: $ARGS"
}
shllibEchoInfo ()
{
    ARGS=$*
        echo -e "$ARGS"
}
shllibEchoDebug ()
{
    ARGS=$*
        local DEBUGTMP v
        if [ -n "$ECHOTRACEON" ]
            then
                let idx=0
                for DEBUGTMP in $(caller 0)
                    do
                        v[$idx]="$DEBUGTMP"
                            let idx=$idx+1
                            done
                            echo -e "DEBUG>: ${v[2]}:${v[0]} ${v[1]}\n       $ARGS" \
                            >&2
                            fi
}




# This check given jar
# actually it only detect multiple certificates signatures
checkJar()
{
    jarFile="$1"
    echo "Checking '$jarFile' ..."
    RES=$(jarsigner -verify -verbose -certs $jarFile | grep "X.509" | sort | uniq 2>/dev/null)
    ret=$?
    if [ $ret -ne 0 ]
    then
        jarsigner -verify $jarFile
        return 1
    elif [ $(echo "$RES" | wc -l ) -gt 1 ]
    then
        echo 
        echo -n "ERROR>: Your jarfile contains multiple signatures which can"
        echo "cause troubles on some clients"
        echo 
        echo "Commands for one strong cleanning:"
        echo "mkdir newjar; cd newjar"
        echo "jar xvf $jarFile"
        echo "rm -rf META-INF"
        echo "cd .."
        echo "touch MANIFEST.MF"
        echo "jar cfm newjar.jar MANIFEST.MF -C newjar/ ."
        echo 
        exit 1
    fi
}


# This clean the given jar file just by removing the META-INF directory
# !!! Not yet active !!! 

cleanJar()
{
    echo "Cleaning ${APPNAME}.jar ..."
    mkdir tmpbigjar
    cd tmpbigjar
    shllibEchoDebug "Working directory is $PWD "

    jar xf ../$jarpath
    rm -rf META-INF

    touch MANIFEST.MF
    jar cfm $APPNAME.jar MANIFEST.MF -C tmpbigjar/ . 
    rm MANIFEST.MF
    echo "    done"
    rm -rf tmpbigjar
}

cleanup()
{
# actually nothing should be leaved
    true
}

# Perform command option analysis
#
# Note that we use `"$@"' to let each command-line parameter expand to a
# separate word. The quotes around `$@' are essential!
# We need TEMP as the `eval set --' would nuke the return value of getopt.
TEMP=`getopt -o "" --long verbose,help \
     -n "$SCRIPTNAME" -- "$@"`
     if [ $? != 0 ] ; then _usage >&2 ; exit 1 ; fi
# Note the quotes around `$TEMP': they are essential!
     eval set -- "$TEMP"
     while true ; do
     case "$1" in
     --help ) _usage ; exit ; shift ;;
     --verbose ) shllibSetDebugOn ; shift ;;
     -- ) shift ; break ;;
     * ) echo "Internal error!" ; exit 1 ;;
     esac
     done

# prepare housekeeping 
    trap "cleanup" 0 1 2 5 15

# Do really interresting job...
    for jar in "$@"
    do
    checkJar $jar
    done

