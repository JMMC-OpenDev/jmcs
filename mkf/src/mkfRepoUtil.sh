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
    echo -e "Usage: $SCRIPTNAME [-h] [-v version] [-d outputDir] <info<.versions>|checkout|export|update|tag> <PROJECT> [... <PROJECT_N>]"
    echo -e "\t-h\tprint this help.";
    echo -e "\t-n\tdisplay svn command which is performed when this option is not set.";
    echo -e "\t-v <version>\tuse given version (default is development one) when retrieving sources or tagging.";
    echo -e "\t-d <directory>\tset working area for modules (default is current dir).";
    echo -e "\tACTIONS  :";
    echo -e "\t   info          : list modules of given projects.";
    echo -e "\t   info.versions : list versions present handled by the source code management.";
    echo -e "\t   checkout      : retrieve the modules of given projects.";
    echo -e "\t   export        : retrieve the modules of given projects without versionning files (packaging cases).";
    echo -e "\t   update        : update the modules of given projects.";
    echo -e "\t   tag           : tag the modules of given projects with tag given by -v option.";
    echo -e "\t   install       : install the modules of given projects with tag given by -v option.";
    echo -e "\tPROJECTS :";
    echo -e "\t   ${supportedModules}";
    exit 1
}

function getModules ()
{
    prjSvnroot="$1"
    svnOptions="$2"
    project="$3"
    shift 3
    modules=$*

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
                CMD="$CMD ${moduleName}" ;;
            *)
                CMD="$CMD ${prjSvnroot}/${module}" ;;
        esac
        echo " '${prjSvnroot}/${module}' ..."
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
    prjSvnroot="$1"
    project="$2"
    svnTags=$(${SVN_COMMAND} list $prjSvnroot/$project/tags)
    echo $svnTags | sed "s@/@@g"
}

function displayModules()
{
    prjSvnroot="$1"
    project="$2"
    shift 2
    modules=$*
    echo "'${project}' project get following modules ($modules):"
    for module in $modules ; do
        moduleName=${module##*/}
        echo " - ${moduleName} ( ${prjSvnroot}/${module} )"
    done
    supportedTags=$(displayProjectVersions $prjSvnroot $project)
    echo "  ( Supported versions: $supportedTags )" | sed "s@/@@g"
    echo
}

function installModules()
{
    prjSvnroot="$1"
    project="$2"
    userTag="$3"
    shift 3
    modules="$*"
    version_path="${project}/${userTag}"
    echo "'${project}' project will be installed in '${version_path}':"
    mkdir -p "${version_path}"
    cd "${version_path}"
    for module in $modules ; do
        moduleName=${module##*/}
        repos_path="${prjSvnroot}/${module}"
        echo -n " - installing '${moduleName}' from '${repos_path}' ... "
        ${SVN_COMMAND} checkout "${repos_path}"
        module_src_path="${moduleName}/src"
        (cd "${module_src_path}" ; make clean all man install)
        echo "DONE."
    done
    echo "Installation finished."
    echo
}

function tagModules(){
    prjSvnroot="$1"
    project="$2"
    userTag="$3"
    shift 3
    modules="$*"
    echo "Tagging following modules for '${project}' project :"
    for module in $modules
    do
        echo " '${prjSvnroot}/${module}' ..."
        trunkModule="${module/$userTag/trunk}"
        if svn info "$prjSvnroot/$module" &> /dev/null
        then
          echo "ERROR module $prjSvnroot/$module already present remove first if you want to overwrite"
          return 1
        fi
        ${SVN_COMMAND} copy --parents -m "Automatically tagged ${project} in version ${version/$tagPrefix/} ($SCRIPTNAME)" "$prjSvnroot/$trunkModule" "$prjSvnroot/$module"
    done
}
# This function contains the description of the svn repository and modules for a given project and version
# it returns on the output the svnroot followed by the list of modules paths
# TODO complete with full project list if they require to be packed or handled automatically by scripts
supportedModules="AMBER AppLauncher ASPRO2 LITpro MCS SearchCal WISARD YOCO "
function getProjectDesc()
{
    project="${1}"
    version="${2}"

    case "${project}" in
        AMBER  ) 
            echo "${JMMC_SVNROOT} AMBER/${version}/amdlib" ;;
        ASPRO2 )
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal oiTools/${version}/oitools ASPRO2/${version}/aspro" ;;
        AppLauncher )
            echo -n "${JMMC_SVNROOT} MCS/${version}/jmcs "
            echo AppLauncher/${version}/{smptest,smprsc,smprun} ;;
        LITpro ) 
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs MCS/${version}/jmal oiTools/${version}/oitools LITpro/${version}/mfgui" ;;
        MCS )
            echo -n "${JMMC_SVNROOT} "
            echo MCS/${version}/{mkf,mcscfg,tat,ctoo,mcs,log,err,misc,thrd,timlog,mth,fnd,misco,env,cmd,msg,sdb,evh,gwt,jmcs,jmal,modc,modcpp,modsh,modjava,testgui} ;;
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


# Set default value for 
# - development version 
# - output directory
# - svn command 
version="trunk";
outputDir="$PWD"
SVN_COMMAND="svn"
tagPrefix="tags/"

# Parse command-line parameters
while getopts "hnv:d:" option
    # : after option shows it will have an argument passed with it.
do
    case $option in
        h ) 
            printUsage ;;
        v ) 
            version="${tagPrefix}${OPTARG}";;
        d ) 
            outputDir="${OPTARG}";;
        n )
            SVN_COMMAND="echo svn";;
        * ) # Unknown option
            echo "Invalid option -- $option"
            printUsage ;;
    esac
done
let SHIFTOPTIND=$OPTIND-1
shift $SHIFTOPTIND
if [ $# -lt 2 ]
then
    echo "ERROR: Missing action or project"
    printUsage 
    exit 1
fi
userAction=$1
shift 1
userProjects=$*

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
    prjSvnroot=${prjElements[*]:0:1}
    modules=${prjElements[*]:1}
    if [ -z "$modules" ]
    then 
        echo "ERROR: Project '${project}' is not supported"
        echo "Supported projects are : ${supportedModules}"
        exit 1
    fi

    case "$userAction" in
        checkout )
		    getModules "${prjSvnroot}" checkout "${project}" "${modules}" ;;
		export )
		    getModules "${prjSvnroot}" export "${project}" "${modules}" ;;
		update )
		    getModules "${prjSvnroot}" update "${project}" "${modules}" ;;
		tag )
		    tagModules "${prjSvnroot}" "${project}" "${version}" "${modules}" ;;
		install )
		    installModules "${prjSvnroot}" "${project}" "${version}" "${modules}" ;;
		info )
		    displayModules "${prjSvnroot}" "${project}" "${modules}" ;;
		info.versions )
		    displayProjectVersions "${prjSvnroot}" "${project}" ;;
		*)
		    echo "ERROR: Action '$userAction' not supported"
		    printUsage ;;
    esac
done
