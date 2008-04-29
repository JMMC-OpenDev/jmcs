#*******************************************************************************
# JMMC project
#
# "@(#) $Id: jmcsDeployJnlp.sh,v 1.1 2008-04-29 12:56:12 mella Exp $"
#
# History
#
# $Log: not supported by cvs2svn $
#
#
#*******************************************************************************

#*******************************************************************************
# This script aims to build a directory with all required jar to make 
# automatic distribution of application on JNLP and JAR form
#*******************************************************************************

# define default values 
CODEBASE="http://$(uname -n)/~$USER"
WEBROOT="$HOME/public_html"

# define script name
SCRIPTNAME=$(basename $0)

# define ModuleArea
SCRIPTROOT=$(cd ..; pwd)

_usage()
{
    echo
    echo "Usage: $(basename $0) [--codebase <url>] [--webroot <dir>] [--help] <JNLP file>"
    echo "  --codebase <url> : optional - specify code base attribute of the generated JNLP file."
    echo "                     (default value: http://<hostname>/~<username>/<software name>')"
    echo "  --webroot  <dir> : optional - specify directory were JNLP file and related jar will be copied."
    echo "                     (default value: ~<username>/public_html/<software name>')"
    echo "  --verbose        : optional - request verbose messages."
    echo "  <JNLP file>      : this (xml) file must be well formed and have 'jnlp' extension"
    echo "" 
    echo "" 
    echo "  This command try to install material described by given JNLP file. One directory that takes JNLP file name (without extension) is created under the WEBROOT. The associated application should be delivered over network (then some xml attributes will be changed on fly by this script)."
    echo ""
    echo "  Examples:"
    echo "    # $(basename $0) App.jnlp"
    echo "    # $(basename $0) --codebase $CODEBASE --webroot $WEBROOT App.jnlp"
    echo -n "    ( On this machine both examples creates one new directory '$WEBROOT/App' that" 
    echo " should be reached from '$CODEBASE/App') " 
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

# Perform command option analysis
#
# Note that we use `"$@"' to let each command-line parameter expand to a
# separate word. The quotes around `$@' are essential!
# We need TEMP as the `eval set --' would nuke the return value of getopt.
TEMP=`getopt -o "" --long codebase:,webroot:,verbose,help \
-n "$SCRIPTNAME" -- "$@"`
if [ $? != 0 ] ; then _usage >&2 ; exit 1 ; fi
# Note the quotes around `$TEMP': they are essential!
eval set -- "$TEMP"
while true ; do
        case "$1" in
                --codebase )
                CODEBASE="$2"; shift 2 ;;
                --webroot )
                WEBROOT="$2"; shift 2 ;;
                --help ) _usage ; exit ; shift ;;
                --verbose ) shllibSetDebugOn ; shift ;;
                -- ) shift ; break ;;
                * ) echo "Internal error!" ; exit 1 ;;
        esac
done

# Check input file
# Change to directory of given jnlp file and use its parent as MODULEROOT
cd $(dirname $1)
MODULEROOT=$(cd ..;pwd)
JNLPFILE=$(basename $1)
if [ ! -f  "$JNLPFILE" ]
then
    echo "Missing JNLP file"
    _usage
    exit 1
fi
# define application name from given jnlp
APPNAME=$(basename $JNLPFILE .jnlp)

if [ "$APPNAME" == "$(basename $JNLPFILE)" ]
then
    echo "Given JNLP file does not end with '.jnlp' extension"
    exit 1
fi

# define directory where application material should be installed into 
APP_WEBROOT=$WEBROOT/$APPNAME
APP_CODEBASE=$CODEBASE/$APPNAME

# search keystore file
KEYSTOREFILE="$MCSTOP/etc/keystore"
echo "Signing step uses '$KEYSTOREFILE' keystore"
# read keyword password 
read -s -p "Enter 'mykey' password to sign every jar files:" MYKEY
echo -e "\n"


# Check webroot directory
if [ -d "$WEBROOT" ]
then
    echo "Installing application into '$APP_WEBROOT' directory..." 
    if [ -e "$APP_WEBROOT" ]
    then
        OLDAPP_WEBROOT=$APP_WEBROOT.$(date +"%Y.%m.%d.%m.%S")
        mv $APP_WEBROOT $OLDAPP_WEBROOT
        echo 
        echo "WARNING: '$APP_WEBROOT' already exists, renamed '$(basename $OLDAPP_WEBROOT)'" 
        echo
    fi
    mkdir "$APP_WEBROOT"
else
    echo "ERROR: Can't install application into '$APP_WEBROOT' directory." 
    echo "'$WEBROOT' directory does not exist, please create it before." 
    exit 1
fi

# Do really interresting job...

copyJnlpAndRelated()
{
    local LONGGIVENJNLP=$1
    local SHORTGIVENJNLP=$(basename $1)
    local destDir=$2
    local destCodeBase=$3
    
    shllibEchoDebug "Copy '$LONGGIVENJNLP' into '$destDir'"
    mkdir -p $destDir &> /dev/null
    cp $LONGGIVENJNLP $destDir

    local destJnlp=$destDir/$SHORTGIVENJNLP
    shllibEchoDebug "Set new codebase attribute of '$destJnlp' : '$destCodeBase'"
    shllibEchoDebug "Set new href     attribute of '$destJnlp' : '$SHORTGIVENJNLP'"
    xml ed -u "/jnlp/@codebase" -v "$destCodeBase" \
    -u "/jnlp/@href" -v "$SHORTGIVENJNLP" \
    $LONGGIVENJNLP > $destJnlp
    
    cd $(dirname $LONGGIVENJNLP)
    
    # transformation builds shell variables : 
    # eval command source them into into bash world
    eval $(xml sel -t -o "local INCLUDEDJNLPLIST=&quot;" \
    -m "//extension/@href[not(contains(.,'tp://'))]" -v "." -o " " -b \
    -o "&quot;;&#10;" \
    -o "local INCLUDEDJARLIST=&quot;" \
    -m "//jar" -v "@href[not(contains(.,'tp://'))]" -o " " -b -o "&quot;;&#10;" \
    -o "local INCLUDEDICONLIST=&quot;" \
    -m "//icon" -v "@href[not(contains(.,'tp://'))]" -o " " -b -o "&quot;;&#10;" \
    $SHORTGIVENJNLP)

    for icon in $INCLUDEDICONLIST
    do
        shllibEchoDebug "Copying '$icon' into '$destDir/$icon'"
        mkdir -p $(dirname $destDir/$icon) &> /dev/null
        cp $icon $destDir/$icon
    done
    
    for jar in $INCLUDEDJARLIST
    do
        jarname=$(basename $jar) 
        if srcjar=$(miscLocateFile "$jarname" "../lib:$MODULEROOT/lib:$SCRIPTROOT/lib:$INTROOT/lib:$MCSROOT/lib")
        then
            destjar=$destDir/$jar
            # since jarpath can be on the form dir/dir/toto.jar , we have to ensure that
            # $destDir/dir/dir does exist
            mkdir -p $(dirname $destjar) &>/dev/null
            shllibEchoDebug "Copying '$srcjar' into '$destjar'"
            cp $srcjar  $destjar  
            shllibEchoDebug "Signing '$destjar'"
            if ! echo "$MYKEY" | jarsigner -keystore $KEYSTOREFILE $destjar mykey &> /dev/null
            then
                echo "ERROR: Can't sign '$destjar'"
                exit 1
            fi
        else
            echo "ERROR: Can't find '$jar'"
            exit 1
        fi
    done

    # copy each jnlp files and check that subfolder does exist
    for jnlp in $INCLUDEDJNLPLIST
    do
        shllibEchoDebug "Found '$jnlp' into '$SHORTGIVENJNLP' rep='$destDir'"
        relativePath=$(dirname $jnlp)
        if [ "$relativePath" = "." ]
        then
            relativePath=""
        else
            relativePath="/$relativePath"
        fi
        if ! copyJnlpAndRelated $jnlp ${destDir}${relativePath} ${destCodeBase}${relativePath}
        then
            return $?
        fi
done
}
    
copyJnlpAndRelated $JNLPFILE $APP_WEBROOT $APP_CODEBASE || exit $?

createAppJar(){
# sign each jar and make a big jar file to build final APPNAME.jar file
echo "Creating '$APPNAME.jar' ... "
cd $APP_WEBROOT
mkdir tmpbigjar
cd tmpbigjar
for jarpath in  $(find $APP_WEBROOT -name '*.jar')
do
    shllibEchoDebug " Add '$jarpath' content into tmpbigjar"
    jar xf $jarpath
done

# remove old META-INF of previous jar if any and build new MANIFEST file
rm -rf META-INF
MAINCLASS=$(xml sel -t -v "//application-desc/@main-class"  $APP_WEBROOT/$JNLPFILE)
cd ..
echo "Main-class: $MAINCLASS" > MANIFEST.MF
if [ -e "$APPNAME.jar" ]
then
  echo -en "\nERROR: '$APPNAME.jar' already exists : can't build new "
  echo -e "application jar with same name\n" 
  exit 1
fi
jar cfm $APPNAME.jar MANIFEST.MF -C tmpbigjar/ . 
rm MANIFEST.MF
echo "    done"
rm -rf tmpbigjar
}

createAppJar

# Generates one default index.html file
echo "Creating index.html ... "
cd $APP_WEBROOT
JARFILE=$(xml sel -t -v "concat(substring-before(/jnlp/@href, '.jnlp'),'.jar')" ${APPNAME}.jnlp)
xml sel -I -t -e "html" \
-e "head" \
-e "title" -v "//title" -b \
-e "body" \
-e "p" -m "//icon/@href" -e "image" -a "src" -v "." -b -b -b -b \
-e "h1" -v "//title" -b \
-e "p" -v "//description" -b \
-e "p" -o "By " -e "a" -a "href" -v "//homepage/@href" -b -v "//vendor" -b -b \
-e "p" -o "Run Java Webstart application" -e "a" -a "href" -v "/jnlp/@href" -b \
-o " using this link on main jnlp file" -b -b \
-e "p" -e "a" -a "href" -o "$JARFILE" -b -o "Download JAR " -b \
-o "of application and run following command:" -b \
-e "p" -e "pre" -o "java -jar $JARFILE " \
-m "//application-desc/argument" -o "&quot;" -v "." -o "&quot; " -b -b -b \
-i "//j2se" \
-e "h4" -o "List of supported Java 2 SE Runtime Environment (JRE) versions:" -b \
-e "ul" -m "//j2se" -e "li" -v "@version" -b -b -b \
-e "small" -e "pre" -o "--" -n -o "This page and previous content has been installed by $SCRIPTNAME on " -v "date:date()" -b -b \
${APPNAME}.jnlp > index.html 
echo "    done"

echo
echo "Please test deployement onto: $APP_CODEBASE"
