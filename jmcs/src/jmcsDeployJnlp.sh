#*******************************************************************************
# JMMC project
#
# "@(#) $Id: jmcsDeployJnlp.sh,v 1.29 2011-04-13 13:23:29 mella Exp $"
#
# History
#
# $Log: not supported by cvs2svn $
# Revision 1.28  2011/02/24 10:22:51  mella
# Add information into generated MANIFEST.MF file
#
# Revision 1.27  2011/02/21 17:10:32  mella
# improve deployement on config where aliases are used
#
# Revision 1.26  2011/02/15 17:02:21  mella
# generate one credits.htm page
#
# Revision 1.25  2010/09/10 07:59:12  mella
# Add programm name before version number in rss file
#
# Revision 1.24  2010/02/18 12:27:51  mella
# add css to index.html
#
# Revision 1.23  2009/09/20 20:21:35  mella
# add acknowledgement built
#
# Revision 1.22  2009/09/18 10:57:52  mella
# Add generation of acknowledgement
#
# Revision 1.21  2009/03/20 06:23:48  mella
# move to castor1.3
#
# Revision 1.20  2008/12/10 20:47:24  mella
# add comment
#
# Revision 1.19  2008/10/15 07:16:27  mella
# Add link onto releases into index.htm generated file
#
# Revision 1.18  2008/10/06 15:08:14  mella
# Fix update with prereleases check
#
# Revision 1.17  2008/10/02 20:16:55  mella
# Fix .htm instead of .html extensions
#
# Revision 1.16  2008/10/02 20:14:54  mella
# Do only copy pubDate of previous releases instead of all release elements
#
# Revision 1.15  2008/09/25 07:26:18  mella
# typo
#
# Revision 1.14  2008/09/24 15:43:47  mella
# - fix minor html build bugs
# - add css stylesheet to html page
# - add rss link into html page
#
# Revision 1.13  2008/09/24 15:32:27  mella
# First working prototype that generates releasenotes.(rss|html)
#
# Revision 1.12  2008/09/24 08:06:20  mella
# One failure in the build process does not erase previous running webroot
#
# Revision 1.11  2008/08/26 08:14:58  mella
# Add searchpath option to perform additionnal search of jar files
#
# Revision 1.10  2008/08/25 14:30:00  mella
# Use key file for signing if present
#
# Revision 1.9  2008/06/11 07:52:24  mella
# exit when one jnlp file is not found
#
# Revision 1.8  2008/05/30 13:14:06  ccmgr
# Remove bigManfistest creation since it creates security pbs on Mac OS X
#
# Revision 1.7  2008/05/30 12:24:50  mella
# Build a MANIFEST.MF file that accumulate entries from previous jar
# Use {CommandLine}/lib working directory to search jars into
#
# Revision 1.6  2008/05/29 15:09:11  mella
# Exit if icon file can't be found
#
# Revision 1.5  2008/05/27 05:50:42  mella
# exit if any jar is not found
#
# Revision 1.4  2008/05/26 14:24:47  mella
# Fix better output messages
#
# Revision 1.3  2008/05/26 14:02:37  mella
# fix error redirection
#
# Revision 1.2  2008/05/26 11:26:07  mella
# Remove some error lines
#
# Revision 1.1  2008/04/29 12:56:12  mella
# first revision
#
#
#
#*******************************************************************************

#*******************************************************************************
# This script aims to build a directory with all required jar to make 
# automatic distribution of application on JNLP and JAR form
#*******************************************************************************

# define default values 
# use hardcoded value : used to be : CODEBASE="http://$(uname -n)/~$USER"
CODEBASE="http://apps.jmmc.fr/~$USER"
WEBROOT="$HOME/public_html"

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
    echo "Usage: $SCRIPTNAME [--codebase <url>] [--webroot <dir>] [--help] <JNLP file>"
    echo "  --codebase <url> : optional - specify code base attribute of the generated JNLP file."
    echo "                     (default value: http://<hostname>/~<username>/<software name>')"
    echo "  --webroot  <dir> : optional - specify directory were JNLP file and related jar will be copied."
    echo "                     (default value: ~<username>/public_html/<software name>')"
    echo "  --searchpath  <dir1>:<dir2> : optional - specify one additional list of directory to search jar file into."
    echo "                     (default value: ~<username>/public_html/<software name>')"
    echo "  --verbose        : optional - request verbose messages."
    echo "  <JNLP file>      : this (xml) file must be well formed and have 'jnlp' extension"
    echo "" 
    echo "" 
    echo "  This command try to install material described by given JNLP file. One directory that takes JNLP file name (without extension) is created under the WEBROOT. The associated application should be delivered over network (then some xml attributes will be changed on fly by this script)."
    echo ""
    echo "  Examples:"
    echo "    # $SCRIPTNAME App.jnlp"
    echo "    # $SCRIPTNAME --codebase $CODEBASE --webroot $WEBROOT App.jnlp"
    echo -n "    ( On this machine both examples creates one new directory '$WEBROOT/App' that" 
    echo " should be reached from '$CODEBASE/App') " 
    echo "" 
    echo ""
}

createXsltFiles()
{
    cat > setDateOfReleases.xsl <<EOF
    <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exslt="http://exslt.org/common"
    xmlns:math="http://exslt.org/math"
    xmlns:date="http://exslt.org/dates-and-times"
    xmlns:func="http://exslt.org/functions"
    xmlns:set="http://exslt.org/sets"
    xmlns:str="http://exslt.org/strings"
    xmlns:dyn="http://exslt.org/dynamic"
    xmlns:saxon="http://icl.com/saxon"
    xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
    xmlns:xt="http://www.jclark.com/xt"
    xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
    xmlns:test="http://xmlsoft.org/XSLT/"
    extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
    exclude-result-prefixes="math str">
    <xsl:output omit-xml-declaration="yes" indent="no"/>
    <xsl:param name="releaseFile">ApplicationReleases.xml</xsl:param>
    <xsl:variable name="releasenotes" select="document(\$releaseFile)"/>
    <!--                                                             -->
    <!-- Update releaseFile with all new releases of given document  -->
    <!--   and update pubDates                                       -->
    <!--                                                             -->
    <xsl:template match="/">
    <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="release" priority="2">
    <xsl:variable name="version" select="./@version" />
    <xsl:variable name="prereleases">
    <xsl:for-each select="./prerelease">
    <xsl:value-of select="@version"/>
    </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="oldprereleases">
    <xsl:for-each select="exslt:node-set(\$releasenotes)//release[@version=\$version]//prerelease" >
    <xsl:value-of select="@version"/>
    </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="pubDate">
    <xsl:element name="pubDate">
    <xsl:value-of select="concat(date:day-abbreviation(), ', ',
    format-number(date:day-in-month(), '00'), ' ',
    date:month-abbreviation(), ' ',
    date:year(), ' ',
    format-number(date:hour-in-day(), '00'), ':',
    format-number(date:minute-in-hour(), '00'), ':',
    format-number(date:second-in-minute(), '00'), ' GMT')"/>
    </xsl:element>
    </xsl:variable>
    <xsl:variable name="oldPubDate">
    <xsl:element name="pubDate">
    <xsl:choose>
    <xsl:when test="exslt:node-set(\$releasenotes)//release[@version=\$version]/pubDate">
    <xsl:copy-of select="exslt:node-set(\$releasenotes)//release[@version=\$version]/pubDate"/>
    </xsl:when><xsl:otherwise>
    <xsl:value-of select="concat(date:day-abbreviation(), ', ',
    format-number(date:day-in-month(), '00'), ' ',
    date:month-abbreviation(), ' ',
    date:year(), ' ',
    format-number(date:hour-in-day(), '00'), ':',
    format-number(date:minute-in-hour(), '00'), ':',
    format-number(date:second-in-minute(), '00'), ' GMT')"/>
    </xsl:otherwise>
    </xsl:choose>
    </xsl:element>
    </xsl:variable>


    <xsl:element name="{name()}">
    <xsl:apply-templates select="./@*"/>
    <xsl:choose>
    <xsl:when test="\$prereleases=\$oldprereleases">
    <xsl:message>release <xsl:value-of select="\$version"/> keep old pubDate (<xsl:value-of select="\$oldPubDate"/>)</xsl:message>
    <xsl:copy-of select="\$oldPubDate"/>
    </xsl:when>
<!--    <xsl:when test="set:difference(\$oldprereleases, \$prereleases)">
    <xsl:message>prerelease detect</xsl:message>
    </xsl:when>
    <xsl:when test="exslt:node-set(\$releasenotes)//release[@version=\$version]/pubDate">
    <xsl:message><xsl:copy-of select="set:difference(\$prereleases, \$oldprereleases)"/></xsl:message>
    <xsl:message>release <xsl:value-of select="\$version"/> keep same pubDate</xsl:message>
    </xsl:when>
    -->
    <xsl:otherwise>
    <xsl:message>release <xsl:value-of select="\$version"/> get new pubDate (<xsl:value-of select="\$pubDate"/>)</xsl:message>
    <xsl:copy-of select="\$pubDate"/>
    <!-- Check that old release hae date and all prereleases-->
    </xsl:otherwise>
    </xsl:choose>

    <xsl:apply-templates />
    </xsl:element>

    </xsl:template>

    <!-- Copy elements as original -->
    <xsl:template match="*" priority="1">
    <xsl:element name="{name()}">
    <xsl:apply-templates select="./@*"/>
    <xsl:apply-templates />
    </xsl:element>
    </xsl:template>

    <!-- Copy comment text and attributes as original -->
    <xsl:template match="comment()|text()|@*">
    <xsl:copy-of select="."/>
    </xsl:template>
    </xsl:stylesheet>
EOF

    cat > applicationReleaseToRss.xsl <<EOF
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:exslt="http://exslt.org/common"
 xmlns:math="http://exslt.org/math"
 xmlns:date="http://exslt.org/dates-and-times"
 xmlns:func="http://exslt.org/functions"
 xmlns:set="http://exslt.org/sets"
 xmlns:str="http://exslt.org/strings"
 xmlns:dyn="http://exslt.org/dynamic"
 xmlns:saxon="http://icl.com/saxon"
 xmlns:xalanredirect="org.apache.xalan.xslt.extensions.Redirect"
 xmlns:xt="http://www.jclark.com/xt"
 xmlns:libxslt="http://xmlsoft.org/XSLT/namespace"
 xmlns:test="http://xmlsoft.org/XSLT/"
 extension-element-prefixes="exslt math date func set str dyn saxon xalanredirect xt libxslt test"
 exclude-result-prefixes="math str">
<xsl:output omit-xml-declaration="no" indent="yes"/>
<xsl:param name="inputFile">-</xsl:param>
<xsl:template match="/">
  <xsl:call-template name="t1"/>
</xsl:template>
<xsl:template name="t1">
  <xsl:element name="rss">
    <xsl:attribute name="version">
      <xsl:value-of select="'2.0'"/>
    </xsl:attribute>
    <xsl:element name="channel">
      <xsl:element name="title">
        <xsl:value-of select="//program/@name"/>
        <xsl:value-of select="' releases'"/>
      </xsl:element>
      <xsl:element name="description">
        <xsl:value-of select="'This RSS feed summarizes each software releases.'"/>
      </xsl:element>
      <xsl:element name="link">
        <xsl:value-of select="//ApplicationData/@link"/>
        <xsl:value-of select="'/releasenotes.htm'"/>
      </xsl:element>
      <xsl:for-each select="//release">
        <xsl:element name="item">
          <xsl:element name="title">
          <xsl:value-of select="//program/@name"/>
          <xsl:value-of select="' version '"/>
            <xsl:value-of select="@version"/>
          </xsl:element>
          <xsl:element name="pubDate">
            <xsl:value-of select="pubDate"/>
          </xsl:element>
          <xsl:element name="link">
            <xsl:value-of select="//ApplicationData/@link"/>
            <xsl:value-of select="'/releasenotes.htm'"/>
            <xsl:value-of select="'#'"/>
            <xsl:value-of select="@version"/>
        </xsl:element>
        <xsl:element name="description">
            <!--<![CDATA[<![CDATA[]]>-->
            <xsl:value-of select="'&lt;![CDATA['" disable-output-escaping="yes"/>
            <ul>
                <xsl:for-each select=".//change">
                <xsl:sort select="./@type"/>
                    <li>
                        <xsl:if test="./@type"><xsl:value-of select="./@type"/>: </xsl:if><xsl:value-of select="."/>
                    </li>
                </xsl:for-each>
            </ul>
            <xsl:value-of select="']]>'" disable-output-escaping="yes"/>
          </xsl:element>
        </xsl:element>
      </xsl:for-each>
    </xsl:element>
  </xsl:element>
</xsl:template>
</xsl:stylesheet>
EOF
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

copyJnlpAndRelated()
{
    local LONGGIVENJNLP=$1
    local SHORTGIVENJNLP=$(basename $1 2> /dev/null)
    local destDir=$2
    local destCodeBase=$3

    shllibEchoDebug "Copy '$LONGGIVENJNLP' into '$destDir'"
    mkdir -p $destDir 2> /dev/null
    if ! cp $LONGGIVENJNLP $destDir
    then
        shllibEchoError "Can't find '$LONGGIVENJNLP'"
        exit 1
    fi

    local destJnlp=$destDir/$SHORTGIVENJNLP
    shllibEchoDebug "Set new codebase attribute of '$destJnlp' : '$destCodeBase'"
    shllibEchoDebug "Set new href     attribute of '$destJnlp' : '$SHORTGIVENJNLP'"
    xml ed -u "/jnlp/@codebase" -v "$destCodeBase" \
    -u "/jnlp/@href" -v "$SHORTGIVENJNLP" \
    $LONGGIVENJNLP > $destJnlp

    cd $(dirname $LONGGIVENJNLP 2> /dev/null)

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
        mkdir -p $(dirname $destDir/$icon) 2> /dev/null
        if ! cp $icon $destDir/$icon &> /dev/null
        then
            shllibEchoError "Can't find file '$icon'"
            exit 1
        fi
    done

    for jar in $INCLUDEDJARLIST
    do
        jarname=$(basename $jar 2> /dev/null) 
        if srcjar=$(miscLocateFile "$jarname" "../lib:$COMMANDROOT/lib:$MODULEROOT/lib:$SCRIPTROOT/lib:$INTROOT/lib:$MCSROOT/lib:$USERSEARCHPATH")
        then
            destjar=$destDir/$jar
            # since jarpath can be on the form dir/dir/toto.jar , we have to ensure that
            # $destDir/dir/dir does exist
            mkdir -p $(dirname $destjar) &>/dev/null
            shllibEchoInfo "Copying/signing '$srcjar' into '$destjar'"
            cp $srcjar  $destjar  
            if ! echo "$MYKEY" | jarsigner -keystore $KEYSTOREFILE $destjar mykey &> /dev/null
            then
                shllibEchoError "Can't sign '$destjar'"
                exit 1
            fi
        else
            shllibEchoError "Can't find '$jar'"
            exit 1
        fi
    done

    # copy each jnlp files and check that subfolder does exist
    for jnlp in $INCLUDEDJNLPLIST
    do
        shllibEchoDebug "Found '$jnlp' into '$SHORTGIVENJNLP' rep='$destDir'"
        relativePath=$(dirname $jnlp 2> /dev/null)
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

# This function signs each jar and make a big jar file to build final APPNAME.jar file
createAppJar()
{
    echo "Creating '$APPNAME.jar' ... "
    # BIGMANIFEST file will content of previous entries
    #BIGMANIFEST=$APP_WEBROOT/BIG_MANIFEST.MF
    cd $APP_WEBROOT
    mkdir tmpbigjar
    cd tmpbigjar
    shllibEchoDebug "Working dirtectory is $PWD "
    for jarpath in  $(find $APP_WEBROOT -name '*.jar')
    do
        shllibEchoDebug " Add '$jarpath' content into tmpbigjar"
        jar xf $jarpath
        #  cat META-INF/MANIFEST.MF | awk '{if ( match($1,"Name: *") == 1 )p=1; if( length($1) == 0 ){p=0; print} ; if (p==1)print ;}' >> $BIGMANIFEST
        rm -rf META-INF
    done

    # remove old META-INF of previous jar if any and build new MANIFEST file
    # only main class is included from jnlp because MANIFEST can't handle arguments
    rm -rf META-INF
    MAINCLASS=$(xml sel -t -v "//application-desc/@main-class"  $APP_WEBROOT/$JNLPFILE)
    cd ..

    echo "Built-By: JMMC (jmmc-tech-group@ujf-grenoble.fr)" > MANIFEST.MF
    echo "Built-Date: $(date)" >> MANIFEST.MF
    echo "Specification-Vendor: www.jmmc.fr" >> MANIFEST.MF
    echo "Implementation-Title: $APPNAME" >> MANIFEST.MF
    # TODO add version
    # echo "Implementation-Version: $APPVERSION" >> MANIFEST.MF
    echo "Main-class: $MAINCLASS" >> MANIFEST.MF

    #echo "" >> MANIFEST.MF
    #cat $BIGMANIFEST >> MANIFEST.MF
    if [ -e "$APPNAME.jar" ]
    then
        shllibEchoError "'$APPNAME.jar' already exists"
        shllibEchoError "  Can't build new application jar with same name\n" 
        exit 1
    fi
    jar cfm $APPNAME.jar MANIFEST.MF -C tmpbigjar/ . 
    rm MANIFEST.MF
    echo "    done"
    rm -rf tmpbigjar
}

createHtmlAcknowledgement()
{
    APPLICATION_DATA_XML=$(find $SCRIPTROOT -name ApplicationData.xml)
    if [ -f "$APPLICATION_DATA_XML" ]
    then
        OUTPUTFILE=acknowledgement.htm
        echo "Creating '$OUTPUTFILE' ... "
        cd $APP_WEBROOT
        xml sel -I -t -e "html" \
        -e "head" \
        -e "title" -o "$APPNAME acknowledgment" -b -b \
        -e "body" \
        -e "pre" -v "//acknowledgment" \
        -b -b \
        ${APPLICATION_DATA_XML} > $OUTPUTFILE
        cd -
        echo "    done"
    fi
}


createHtmlIndex()
{
    OUTPUTFILE=index.htm
    echo "Creating '$OUTPUTFILE' ... "
    cd $APP_WEBROOT
    JARFILE=$(xml sel -t -v "concat(substring-before(/jnlp/@href, '.jnlp'),'.jar')" ${APPNAME}.jnlp)
    xml sel -I -t -e "html" \
    -e "head" \
    -e "title" -v "//title" -b -b \
    -e "link" -a "rel" -o "alternate" -b -a "type" -o "application/rss+xml" -b  -a "title" -o "RSS" -b -a "href" -o "./releasenotes.rss" -b -b \
    -e "link" -a "rel" -o "stylesheet" -b -a "type" -o "text/css" -b  -a "href" -o "/css/2col_leftNav.css" -b -b \
    -e "body" \
    -e "div" -a "id" -o "content" -b \
    -e "p" -m "//icon/@href" -e "a" -a "href" -v "/jnlp/@href" -b \
    -e "image" -a "width" -o "64" -b -a "height" -o "64" -b -a "src" -v "." -b -b \
    -b -b -b \
    -e "h1" -v "//title" -b \
    -e "p" -v "//description" -b \
    -e "p" -o "By " -e "a" -a "href" -v "//homepage/@href" -b -v "//vendor" -b -b \
    -e "p" -o "Run Java Webstart application" -e "a" -a "href" -v "/jnlp/@href" -b \
    -o " using this link on main jnlp file" -b -b \
    -e "p" -o "- OR -" -b \
    -e "p" -e "a" -a "href" -o "$JARFILE" -b -o "Download JAR " -b \
    -o "of application and run following command:" -b \
    -e "p" -e "pre" -o "java -jar $JARFILE " \
    -m "//application-desc/argument" -o "&quot;" -v "." -o "&quot; " -b -b -b \
    -i "//j2se" \
    -e "h4" -o "List of supported Java 2 SE Runtime Environment (JRE) versions:" -b \
    -e "ul" -m "//j2se" -e "li" -v "@version" -b -b -b \
    -e "p" -e "a" -a "href" -o "./credits.htm" -b -o "View credits." -b -b \
    -e "p" -e "a" -a "href" -o "./releasenotes.htm" -b -o "View release notes." -b -b \
    -e "p" -e "a" -a "href" -o "./releasenotes.rss" -b -o "Subscribe to this rss feed to be informed of future releases." -b -b \
    -e "small" -e "pre" -o "--" -n \
    -o "This page and previous content is generated by $SCRIPTNAME on " -v "date:date()" -b -b -b \
    ${APPNAME}.jnlp > $OUTPUTFILE 
    cd -
    echo "    done"
}

# this function try to complete previous release file and generates html and rss
# version of this release file
# if the previous release file is not found a new one is generated
# in fact the reference file is the same as applicationData.xml with addition of
# pubDate elements
# Warning next code works if only one Applicationdata is in the module
# To solve it we could search ApplicationDara.xml under the package of main
# class of jnlp file...
createReleaseFiles()
{
    APPLICATION_DATA_XML=$(find $SCRIPTROOT -name ApplicationData.xml)
    cd $APP_WEBROOT
    createXsltFiles
    if [ -f "$APPLICATION_DATA_XML" ]
    then
        echo "Creating releasenotes files ... "

        # recover release from previous installation or build new one
        XML_RELEASE_FILE=ApplicationRelease.xml
        if [ -e $REAL_APP_WEBROOT/$XML_RELEASE_FILE ]
        then
            OLDXML_RELEASE_FILE=$REAL_APP_WEBROOT/$XML_RELEASE_FILE
        else
            OLDXML_RELEASE_FILE=$APPLICATION_DATA_XML
        fi

        # complete OLD release file with ones comming from given APPLICATION_DATA_XML
        # and set pubDate
        xsltproc --path .:$PWD \
        --stringparam releaseFile $OLDXML_RELEASE_FILE \
        --output $XML_RELEASE_FILE \
        setDateOfReleases.xsl $APPLICATION_DATA_XML 

        # transform into html and rss format
        HTML_RELEASE_NOTES=releasenotes.htm
        echo "Creating '$HTML_RELEASE_NOTES'"
        xml sel -I -t -e "html" \
        -e "head" \
        -e "title" -v "//program/@name" -o " " -v "//program/@version" -o "releases"  -b \
        -e "link" -a "rel" -o "alternate" -b -a "type" -o "application/rss+xml" -b  -a "title" -o "RSS" -b -a "href" -o "./releasenotes.rss" -b -b \
        -e "link" -a "rel" -o "stylesheet" -b -a "type" -o "text/css" -b -a "href" -o "http://www.jmmc.fr/css/2col_leftNav.css" -b -b \
        -b \
        -e "body" \
        -e "h1" -e "a" -a "href" -v "//ApplicationData/@link" -b  -v "//program/@name" -o " " -v "//program/@version" -b -o " release notes" -b \
        -m "//release" \
        -e "a" -a "name" -v "@version" -b -b \
        -e "h2" -o "Version " -v "@version" -b \
        -e "p" -v "pubDate" \
        -e "ul" \
        -m ".//change" -s A:T:U "./@type" -e "li" -i "./@type" -v "./@type" -o ": " -b -v "." -b\
        -b -b -b -b \
        $XML_RELEASE_FILE > $HTML_RELEASE_NOTES 

        OUTPUTFILE=releasenotes.rss
        echo "Creating '$OUTPUTFILE'"
        xsltproc --output $OUTPUTFILE applicationReleaseToRss.xsl $XML_RELEASE_FILE
        echo "    done"
    fi
    cd -
}


# this function generates one credit page
# Warning next code works if only one Applicationdata is in the module
# To solve it we could search ApplicationDara.xml under the package of main
# class of jnlp file...
createCreditFile()
{
    APPLICATION_DATA_XML=$(find $SCRIPTROOT -name ApplicationData.xml)
    OUTPUTFILE=credits.htm
    echo "Creating '$OUTPUTFILE' ... "
    cd $APP_WEBROOT
    xml sel -I -t -e "html" \
    -e "head" \
    -e "title" -o "Credits of " -v "/ApplicationData/program/@name" -b -b \
    -e "link" -a "rel" -o "alternate" -b -a "type" -o "application/rss+xml" -b  -a "title" -o "RSS" -b -a "href" -o "./releasenotes.rss" -b -b \
    -e "link" -a "rel" -o "stylesheet" -b -a "type" -o "text/css" -b  -a "href" -o "/css/2col_leftNav.css" -b -b \
    -e "body" \
    -e "div" -a "id" -o "content" -b \
    -e "h1" -e a -a href -o "./" -b -v "/ApplicationData/program/@name" -b -o " credits" -b \
    -e "ul" -m "//package" \
    -e "li" -e a -a href  -v "@link" -b -v "@name" -b -e br -b -v "@description" -b -b \
    -b \
    -e "small" -e "pre" -o "--" -n \
    -o "This page and previous content was generated on " -v "date:date()" -b -b -b \
    ${APPLICATION_DATA_XML} > $OUTPUTFILE 
    cd -
    echo "    done"
}

cleanup()
{
    if [ -d $APP_WEBROOT ]
    then
        rm -rf $APP_WEBROOT
    fi
}

# Perform command option analysis
#
# Note that we use `"$@"' to let each command-line parameter expand to a
# separate word. The quotes around `$@' are essential!
# We need TEMP as the `eval set --' would nuke the return value of getopt.
TEMP=`getopt -o "" --long codebase:,webroot:,searchpath:,verbose,help \
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
        --searchpath )
        USERSEARCHPATH="$2"; shift 2 ;;
        --help ) _usage ; exit ; shift ;;
        --verbose ) shllibSetDebugOn ; shift ;;
        -- ) shift ; break ;;
        * ) echo "Internal error!" ; exit 1 ;;
    esac
done

# Check input file
# Change to directory of given jnlp file and use its parent as MODULEROOT
cd $(dirname "$1")
MODULEROOT=$(cd ..;pwd)
JNLPFILE=$(basename "$1" 2> /dev/null)
if [ ! -f  "$JNLPFILE" ]
then
    echo "Missing JNLP file"
    _usage
    exit 1
fi

# define application name from given jnlp
APPNAME=$(basename $JNLPFILE .jnlp 2> /dev/null)

if [ "$APPNAME" == "$(basename $JNLPFILE 2> /dev/null)" ]
then
    echo "Given JNLP file does not end with '.jnlp' extension"
    exit 1
fi

# define directory where application material should be installed into 
REAL_APP_WEBROOT=$WEBROOT/$APPNAME
APP_WEBROOT=$WEBROOT/$APPNAME.$(date +%s)
APP_CODEBASE=$CODEBASE/$APPNAME

# search keystore file
KEYSTOREFILE="$MCSTOP/etc/keystore"
echo "Signing step uses '$KEYSTOREFILE' keystore"
# read keyword password from file or from prompt if key file is not present
KEYFILE="${KEYSTOREFILE}.key"
if [ -f "$KEYFILE" ]
then
    echo "Using '$KEYFILE' as key file"
    MYKEY=$(cat $KEYFILE)
else
    read -s -p "Enter 'mykey' password to sign every jar files:" MYKEY
    echo -e "\n"    
fi

# Check webroot directory
if [ ! -d "$WEBROOT" ]
then
    shllibEchoError "Can't install application into '$REAL_APP_WEBROOT' directory." 
    echo "'$WEBROOT' directory does not exist, please create it before." 
    exit 1
fi
mkdir "$APP_WEBROOT"

# prepare housekeeping 
trap "cleanup" 0 1 2 5 15

# Do really interresting job...
createReleaseFiles
copyJnlpAndRelated $JNLPFILE $APP_WEBROOT $APP_CODEBASE || exit $?
createAppJar
createHtmlIndex
createCreditFile
createHtmlAcknowledgement

echo "Installing application into '$REAL_APP_WEBROOT' directory..." 
if [ -e "$REAL_APP_WEBROOT" ]
then
    OLDAPP_WEBROOT=$REAL_APP_WEBROOT.$(date +"%Y.%m.%d.%m.%S")
    mv $REAL_APP_WEBROOT $OLDAPP_WEBROOT
    echo 
    echo "WARNING: '$REAL_APP_WEBROOT' already exists, renamed '$(basename $OLDAPP_WEBROOT 2> /dev/null)'" 
    echo
fi

mv $APP_WEBROOT $REAL_APP_WEBROOT 

echo
echo "Deployement has been made into '$REAL_APP_WEBROOT'" 
echo "Please test deployement onto: $APP_CODEBASE"
