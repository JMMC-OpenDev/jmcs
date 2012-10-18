#! /bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************
#  REPLACE_BY_PROJECT_HEADER
#
# This script retrieves modules of the GIVEN project
# The list of projects and their associated modules is defined inside this file 
# use -h version to get list of options
# 
# In its first versions, this script is deployed in several modules. This is
# not the best solution, but allows projects to live independently. We may 
# imagine in the future other way to help modules retrieval.
# So this script must be synchronized ( manually first ) between the following modules:
# - MCS//mkf/src/mkfRepoUtil.sh (master reference)
# - AMBER//amdlib/src/amdlibRepoUtil.sh
# - YOCO//yoco/distrib/repoUtil.sh
# Wish list: 
# - add new action to tag a project
# - add for info action the way to get list of supported versions


# main default repository url
JMMC_SVNROOT=https://svn.jmmc.fr/jmmc-sw
IPAG_SVNROOT=https://forge.osug.fr/svn/ipag-sw
SCRIPTNAME=$(basename $0)

# Print usage 
function printUsage ()
{
    echo -e "Usage: $SCRIPTNAME  [-d outputDir] [-h] [-n] [-m] [-v version] [-r revision] <info<.versions>|checkout|export|install|package|update|tag> <PROJECT> [... <PROJECT_N>]"
    echo -e "\t-d <directory>\tset working area for modules (default is current dir).";
    echo -e "\t-h\tprint this help.";
    echo -e "\t-n\tdisplay svn command which is performed when this option is not set.";
    echo -e "\t-m \tgenerate documentation from code.";
    echo -e "\t-v <version>\tuse given version (default is development one) when retrieving sources or tagging.";
    echo -e "\t-r <revision>\tuse given revision (default is the last one HEAD) when retrieving sources or tagging.";
    echo -e "\tACTIONS  :";
    echo -e "\t   info          : list modules of given projects.";
    echo -e "\t   info.versions : list versions present handled by the source code management.";
    echo -e "\t   checkout      : retrieve the modules of given projects.";
    echo -e "\t   export        : retrieve the modules of given projects without versionning files (packaging cases).";
    echo -e "\t   install       : install the modules of given projects with tag given by -v option.";
    echo -e "\t   package       : package the modules of given projects with tag given by -v option.";
    echo -e "\t   update        : update the modules of given projects.";
    echo -e "\t   tag           : tag the modules of given projects with tag given by -v option.";
    echo -e "\tPROJECTS :";
    echo -e "\t   ${supportedProjects}";
    exit 1
}

function getModules ()
{
    svnOptions="$1"
    project="$2"
    shift 2
    modules=$*
    prjSvnroot=$(getProjectSvnBaseUrl $project)

    case "${svnOptions}" in 
        update)
        CMD="${SVN_COMMAND} ${svnOptions}" ;;
        *)
        CMD="${SVN_COMMAND} ${svnOptions}" ;;
    esac

    echo "Retrieving following modules for '${project}' project :"
    # Checkout module(s) in current dir( if they do not already exists )
    OUTDIR="."
    for module in $modules ; do
        moduleName=${module##*/}
        if [ "$module" == "$modules" ]
        then
                # getmodulesName as output dir because svn cannot export or checkout one module in current dir...
                OUTDIR="$moduleName"
        fi
        if test -d $moduleName -a $svnOptions == "export";
        then
            echo "ERROR: directory '$moduleName' already exists."
            exit 1
        fi
        case "${svnOptions}" in 
            update)
                MODULE_PATH="${moduleName}" ;;
            *)
                MODULE_PATH="${prjSvnroot}/${module}${revision}" ;;
        esac
        CMD="$CMD $MODULE_PATH" 
        echo " '$MODULE_PATH' ..."
    done

    # export/checkout in current dir
    case "${svnOptions}" in 
            update) ;;
            *)
            CMD="$CMD $OUTDIR" ;;
    esac
 
    # executing previously built command:
    $CMD 
    if [ $? != 0 ]
    then
        echo -e "\nERROR: '$CMD' failed ... \n"; 
        #tail "$logFile"
        #echo -e "See log file '$logFile' for details."
        exit 1;
    fi
    
}

function displayProjectVersions()
{
    project="$1"
    prjSvnroot=$(getProjectSvnBaseUrl $1)
    svnTags=$(${SVN_COMMAND} list ${revisionOption} $prjSvnroot/$(getProjectSvnDir $project)/tags)
    
    ALLTAGS=$(echo $svnTags | sed "s@/@@g")
    TAGPREFIX=$(getProjectTagPrefix $project)
    VERSIONPREFIX=${TAGPREFIX%%_V*}
    if [ -n "$VERSIONPREFIX" ] 
    then
      TAGS=$(for t in $ALLTAGS ; do echo -n $t |grep "${VERSIONPREFIX}_" ; done)
      echo -n $TAGS
    else
    echo $ALLTAGS
    fi
    
}

function displayModules()
{
    project="$1"
    shift 1
    modules=$*
    prjSvnroot="$(getProjectSvnBaseUrl $project)"

    echo "'${project}' project get following modules :"
    for module in $modules ; do
        moduleName=${module##*/}
        echo " - ${moduleName} ( ${prjSvnroot}/${module} )"
    done
    supportedTags=$(displayProjectVersions $project)
    echo "  ( Supported versions: $supportedTags )" | sed "s@/@@g"
    echo
}

function installModules()
{
    project="$1"
    userTag="$2"
    shift 2
    modules="$*"
    prjSvnroot=$(getProjectSvnBaseUrl $project)
    version_path="${project}/${userTag}"
    echo "'${project}' project will be installed in '${version_path}':"
    mkdir -p "${version_path}"
    cd "${version_path}"
    for module in $modules ; do
        moduleName=${module##*/}
        repos_path="${prjSvnroot}/${module}"
        echo -n " - installing '${moduleName}' from '${repos_path}' ... "
        ${SVN_COMMAND} checkout ${revisionOption} "${repos_path}"
        module_src_path="${moduleName}/src"
        (cd "${module_src_path}" ; make clean all ${MAN_CMD_DURING_INSTALL} install)
        echo "DONE."
    done
    echo "Installation finished."
    echo
}

function packageProject()
{
    project="$1"
    userTag="${2/_/.}"
    version=${userTag##*V}
    shift 2
    modules="$*"
    package_path="${project}/PACKAGE/"
    prjSvnroot=$(getProjectSvnBaseUrl $project)
    mkdir -p "${package_path}"
    echo "Packaging '$project' ..."
    MYOLDPWD="$PWD"
    cd "${package_path}"
    for module in $modules ; do
        moduleName=${module##*/}
        repos_path="${prjSvnroot}/${module}"
        echo -n " - export '${moduleName}' from '${repos_path}' ..."
        rm -rf "${moduleName}" &> /dev/null
        ${SVN_COMMAND} export ${revisionOption} "${repos_path}" > /dev/null
        echo "DONE."
    done
    packageProjectHook
    echo "Package finished."
    echo
    cd "$MYOLDPWD" 
}

function packageProjectHook()
{
    # now we are in then $project/PACKAGE dir where modules have been exported
    echo " - apply last packaging steps..."
    if [ $project == "WISARD" ] ; then 
        echo "     Remove optimpack sources"
        rm -rf wisard/optimpack &>/dev/null
        echo "     Update doc and remove test directories"
        rm -rf wisard/doc/* wisard/test &> /dev/null
        cd wisard/doc
        echo "    - TODO wget http://www.jmmc.fr/doc/approved/JMMC-PRE-2500-0001.pdf"
        cd ../..
        DIRNAME=wisard
    else
        cd ..
        DIRNAME=PACKAGE
    fi
       
    VERSIONED_DIR="$project-$version"
    mv "$DIRNAME" "$VERSIONED_DIR"
    tar czf "$VERSIONED_DIR".tgz $VERSIONED_DIR
    rm -rf $VERSIONED_DIR
    echo "Built archive: $PWD/$VERSIONED_DIR.tgz"
}

function tagModules(){
    project="$1"
    userTag="$2"
    shift 2
    modules="$*"
    prjSvnroot=$(getProjectSvnBaseUrl $project)
    echo "Tagging following modules for '${project}' project :"
    for module in $modules
    do
        echo " '${prjSvnroot}/${module}' ..."
        trunkModule="${module/$userTag/trunk}"
        if ${SVN_COMMAND} info "$prjSvnroot/$module" &> /dev/null
        then
          echo "ERROR module $prjSvnroot/$module already present remove first if you want to overwrite"
          return 1
        fi
        ${SVN_COMMAND} copy --parents -m "Automatically tagged ${project} in version ${version/$tagPrefix/} from trunk${revision}  ($SCRIPTNAME)" "$prjSvnroot/$trunkModule${revision}" "$prjSvnroot/$module"
    done
}
# This function contains the description of the svn repository and modules for a given project and version
# it returns on the output the svnroot followed by the list of modules paths
# TODO complete with full project list if they require to be packed or handled automatically by scripts
supportedProjects="AMBER AppLauncher ASPRO2 LITpro MCS OIFitsExplorer SearchCal WISARD YOCO "
function getProjectDesc()
{
    project="${1}"
    version="${2}"

    # Warning use shell {} for expasion but don't include them into quoted string
    case "${project}" in
        AMBER  ) 
            echo "${JMMC_SVNROOT} AMBER/${version}/amdlib" ;;
        ASPRO2 )
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal"
            echo  oiTools/${version}/{oitools,oiexplorer-core}
            echo "ASPRO2/${version}/aspro" ;;
        AppLauncher )
            echo -n "${JMMC_SVNROOT} MCS/${version}/jmcs "
            echo AppLauncher/${version}/{smptest,smprsc,smprun} ;;
        LITpro ) 
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal oiTools/${version}/oitools LITpro/${version}/mfgui" ;;
        MCS )
            echo -n "${JMMC_SVNROOT} "
            echo MCS/${version}/{mkf,mcscfg,tat,ctoo,mcs,log,err,misc,thrd,timlog,mth,fnd,misco,env,cmd,msg,sdb,evh,gwt,jmcs,jmal,modc,modcpp,modsh,modjava,testgui} ;;
        OIFitsExplorer )
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal "
            echo  oiTools/${version}/{oitools,oiexplorer-core,oiexplorer};;
        SearchCal ) 
            echo -n "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal "
            echo SearchCal/${version}/{simcli,alx,vobs,sclsvr,sclws,sclgui} ;;
        WISARD )
            echo "${JMMC_SVNROOT} WISARD/${version}/wisard" ;;
        YOCO )
            echo "https://forge.osug.fr/svn/ipag-sw YOCO/${version}/yoco" ;;
        * )
          return 1 ;;
    esac
}

getProjectSvnBaseUrl(){
    project="${1}";
    version="${2}";
    if [ -z "$version" ] ; then version="trunk" ; fi
    prjElements=( $(getProjectDesc "${project}" "${version}") )
    prjSvnroot=${prjElements[*]:0:1}
    echo $prjSvnroot
}



# This function returns the text description (following the twiki syntax) of
# every supported programs
function listProjects (){
    echo "|*Name*|*Identifier*|*Installation account*|*Tag Prefix*|"
    for p in $supportedProjects
    do
        echo -n "| "
        getProjectName "$p"
        echo -n " | $p | "
        getProjectInstallationAccount "$p"
        echo -n " | "
        echo -n "[[$(getProjectSvnBaseUrl "$p")/$(getProjectSvnDir "$p")/tags][$(getProjectTagPrefix "$p")]]"
        echo " |"
    done
}

# returns the svn top level dir that host the main project material
function getProjectSvnDir(){
    project="${1}";
    case "$project" in
        OIFitsExplorer )  echo -n "oiTools" ;;
        *) echo -n "$project" ;;
    esac 
}


function getProjectName(){
    project="${1}"
    case "$project" in
        AMBER )  echo -n "Amber DRS" ;;
        MCS ) echo -n "Mariotti Common Software" ;;
        *) echo -n "$project" ;;
    esac 
}
function getProjectInstallationAccount(){
    project="${1}"
    case "$project" in
        AppLauncher )  echo -n "~smprun" ;;
        SearchCal )  echo -n "~sclws" ;;
        WISARD | YOCO | AMBER )  echo -n "N.A." ;;
        *) echo -n "~swmgr" ;;
    esac 
}

function getProjectTagPrefix(){
    project="${1}"
    versionSuffix=_Vx_y_z
    case "$project" in
        AppLauncher) echo -n "AL${versionSuffix}" ;;
        SearchCal) echo -n "SC${versionSuffix}" ;;
        MCS) echo -n "mmmyyyy" ;;
        ASPRO2 | LITpro ) echo -n "$(echo $project | tr [a-z] [A-Z] |tr -d [:digit:])"${versionSuffix} ;;
        *) echo -n "${project}${versionSuffix}" ;;
    esac 
}


# Set default value for 
# - development version 
# - svn revision 
# - output directory
# - svn command 
# - installation target 
version="trunk";
revision="";
revisionOption="";
outputDir="$PWD"
SVN_COMMAND="svn"
tagPrefix="tags/"
MAN_CMD_DURING_INSTALL=""

# Parse command-line parameters
while getopts "d:hnmv:r:" option
    # : after option shows it will have an argument passed with it.
do
    case $option in
        d ) 
            outputDir="${OPTARG}";;
        h ) 
            printUsage ;;
        n )
            SVN_COMMAND="echo svn";;
        m )
            MAN_CMD_DURING_INSTALL="man";;
        v ) 
            version="${tagPrefix}${OPTARG}";;
        r ) 
            revisionOption="-r ${OPTARG}"
            revision="@${OPTARG}";;
        * ) # Unknown option
            echo "Invalid option -- $option"
            printUsage ;;
    esac
done

let SHIFTOPTIND=$OPTIND-1
shift $SHIFTOPTIND
let NB_ARGS=$#

userAction=$1
shift 1
userProjects=$*

# handle actions that do not require one project
if [ "$userAction" == "list" ]
then
  listProjects
  exit 0
fi
# handle actions that requires a project
if [ $NB_ARGS -lt 2 ]
then
    echo "ERROR: Missing action or project"
    printUsage 
    exit 1
fi


# move to output dir
mkdir -p "$outputDir" &> /dev/null
if ! cd "$outputDir" &> /dev/null
then
    echo "ERROR: Can't move to '$outputDir' directory"
    exit 1
fi

for project in $userProjects
do
    # Retrieve svnroot and module list for given project 
    # return error if no module is found
    prjElements=( $(getProjectDesc "${project}" "${version}") )
    modules=${prjElements[*]:1}
    if [ -z "$modules" ]
    then 
        echo "ERROR: Project '${project}' is not supported"
        echo "Supported projects are : ${supportedProjects}"
        exit 1
    fi

    case "$userAction" in
        checkout )
		    getModules  checkout "${project}" "${modules}" ;;
		export )
		    getModules  export "${project}" "${modules}" ;;
		update )
		    getModules update "${project}" "${modules}" ;;
		tag )
		    tagModules "${project}" "${version}" "${modules}" ;;
		install )
		    installModules  "${project}" "${version}" "${modules}" ;;
		info )
		    displayModules "${project}" "${modules}" ;;
		info.versions )
		    displayProjectVersions "${project}" ;;
		package )
		    packageProject "${project}" "${version}" "${modules}" ;;
		*)
		    echo "ERROR: Action '$userAction' not supported"
		    printUsage ;;
    esac
done
