#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************

# define default values 
# use hardcoded value : used to be : CODEBASE="http://$(uname -n)/~$USER"
CODEBASE="http://apps.jmmc.fr/~$USER"
SHAREDJARPATH="/var/www/html/jnlp/jar"
SHAREDJARURL="http://apps.jmmc.fr/jnlp/jar"
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
#    -u "//extension/@href" -x "str:replace(@href,'@SHARED@','')" \

    cd $(dirname $LONGGIVENJNLP 2> /dev/null)

    # transformation builds shell variables : 
    # eval command source them into into bash world
    TMPVARS=$(xml sel -t -o "local INCLUDEDJNLPLIST=\"" \
    -m "//extension/@href[not(contains(.,'tp://') or contains(.,'../'))]" -v "." -o " " -b \
    -o "\";" -n \
    -o "local INCLUDEDJARLIST=\"" \
    -m "//jar" -v "@href[not(contains(.,'tp://'))]" -o " " -b -o "\";" -n \
    -o "local INCLUDEDICONLIST=\"" \
    -m "//icon" -v "@href[not(contains(.,'tp://'))]" -o " " -b -o "\";" -n \
    $SHORTGIVENJNLP)

   eval $TMPVARS

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

        shllibEchoDebug "current jar $jar :: $jarname"

        shared=false
        sharedExist=false
        if [[ $jar =~ ^@SHARED@ ]]
        then
            shared=true

            if [ -e "$SHAREDJARPATH/$jarname" ]
            then
                sharedExist=true
            fi
        fi
        shllibEchoDebug "shared library = $shared. exist = $sharedExist"

        if srcjar=$(miscLocateFile "$jarname" "../lib:$COMMANDROOT/lib:$MODULEROOT/lib:$SCRIPTROOT/lib:$INTROOT/lib:$MCSROOT/lib:$USERSEARCHPATH")
        then

            if [ $shared == true ]
            then
                if [ $sharedExist == true ]
                then
                    echo "$jarname already present in '$SHAREDJARPATH' should be the same ..."

                    sharedSize=$(stat -c%s $SHAREDJARPATH/original/$jarname)
                    libSize=$(stat -c%s $srcjar)

                    if (( $sharedSize != $libSize )) 
                    then
                        shllibEchoError "Shared library was modified : '$srcjar' ($libSize) <> '$SHAREDJARPATH/original/$jarname' ($sharedSize)"
                        exit 1
                    fi

                else
                    echo "copy original library to $SHAREDJARPATH/original"
                    cp $srcjar $SHAREDJARPATH/original/$jarname

                    destjar=$SHAREDJARPATH/$jarname

                    #shared directory already exist, only use jarname:
                    shllibEchoInfo "Copying/signing '$srcjar' into '$destjar'"
                    cp $srcjar $destjar
  
                    if ! echo "$MYKEY" | jarsigner -keystore $KEYSTOREFILE $destjar mykey &> /dev/null
                    then
                        shllibEchoError "Can't sign '$destjar'"
                        exit 1
                    fi
                fi

                #replace @SHARED@ token by correct URL:
                shllibEchoDebug "replace $jar by $SHAREDJARURL/$jarname ..."

                jarEscaped=$(echo "$jar" | sed -e 's/\(\/\|\\\|&\)/\\&/g')
                urlEscaped=$(echo "$SHAREDJARURL/$jarname" | sed -e 's/\(\/\|\\\|&\)/\\&/g')

                shllibEchoDebug "escaped jar = $jarEscaped"
                shllibEchoDebug "escaped URL = $urlEscaped"
                shllibEchoDebug "cmd : sed -i 's/$jarEscaped/$urlEscaped/g' $destJnlp "

                sed -i "s/$jarEscaped/$urlEscaped/g" $destJnlp 
#                cat $destJnlp


# hack: still copy jars into destDir to keep working createAppJar:
                destjar=$destDir/shared/$jarname
                # since jarpath can be on the form dir/dir/toto.jar , we have to ensure that
                # $destDir/dir/dir does exist
                mkdir -p $(dirname $destjar) &>/dev/null
                shllibEchoInfo "Copying '$srcjar' into '$destjar'"
                cp $srcjar  $destjar

            else
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
            fi
        else
            shllibEchoError "Can't find '$jar'"
            exit 1
        fi
    
    if ! jmcsCheckJarCert "$destjar"
    then
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
    shllibEchoDebug "Working directory is $PWD "

    # Include JmcsLibs.jar if the given jnlp contains one JmcsLibs.jnlp links
    # (even in comments)
    if grep "JmcsLibs.jnlp" "$GIVENJNLPFILE" &> /dev/null
    then
        jmcsLibsJarFile=$(miscLocateFile "JmcsLibs.jar" "../lib:$COMMANDROOT/lib:$MODULEROOT/lib:$SCRIPTROOT/lib:$INTROOT/lib:$MCSROOT/lib:$USERSEARCHPATH")
    fi

    for jarpath in  $(find $APP_WEBROOT -name '*.jar') $jmcsLibsJarFile
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

GIVENJNLPFILE="$(readlink -f $1)"
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
