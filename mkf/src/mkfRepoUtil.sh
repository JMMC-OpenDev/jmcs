#! /bin/bash
#
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
    echo -e "Usage: $SCRIPTNAME [-h] [-v version] [-d outputDir] <info|checkout|export> <PROJECT> [... <PROJECT_N>]"
    echo -e "\t-h\tprint this help.";
    echo -e "\t-v <version>\tuse given version (default is development one) when retrieving sources.";
    echo -e "\t-d <directory>\tset working area for modules (default is current dir).";
    echo -e "\tACTIONS:";
    echo -e "\t   info : list modules of given projects.";
    echo -e "\t   checkout : retrieve the modules of given projects.";
    echo -e "\t   export : retrieve the modules of given projects without versionning files (packaging cases).";
    echo -e "\t   update : update the modules of given projects.";
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
        CMD="svn ${svnOptions} ${moduleName}" ;;
        *)
        CMD="svn ${svnOptions} ${prjSvnroot}/${module}" ;;
    esac

    echo "Retrieving following modules for '${project}' project :"
    # Checkout modules ( if they do not already exists)
    for module in $modules ; do
        moduleName=${module##*/}
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
        echo "'${prjSvnroot}/${module}' ..."
    done

    # export/checkout in current dir
    case "${svnOptions}" in 
            update) ;;
            *)
            CMD="$CMD ." ;;
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

function displayModules()
{
    prjSvnroot="$1"
    project="$2"
    shift 2
    modules=$*
    echo "'${project}' project get following modules:"
    for module in $modules ; do
        moduleName=${module##*/}
        echo " - ${moduleName} ( ${prjSvnroot}/${module} )"
    done
    echo
}

# This function contains the description of the svn repository and modules for a given project and version
# it returns on the output the svnroot followed by the list of modules paths
# TODO complete with full project list if they require to be packed or handled automatically by scripts
supportedModules="AMBER ASPRO2 LITpro MCS SearchCal WISARD YOCO"
function getProjectDesc()
{
    project="${1}"
    version="${2}"

    case "${project}" in
        AMBER  ) 
            echo "${JMMC_SVNROOT} AMBER/${version}/amdlib" ;;
        ASPRO2 )
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs oiTools/${version}/oitools ASPRO2/${version}/aspro" ;;
        LITpro ) 
            echo "${JMMC_SVNROOT} MCS/${version}/jmcs oiTools/${version}/oitools LITpro/${version}/mfgui";;
        MCS )
            echo -n "${JMMC_SVNROOT} "
            echo -n MCS/${version}/{mkf,mcscfg,tat,ctoo,mcs,log,err,misc,thrd,timlog,mth,fnd,misco,env,cmd,msg,sdb,evh,gwt,jmcs,modc,modcpp,modsh,modjava,testgui}
            echo ;;
        SearchCal ) 
            echo -n "${JMMC_SVNROOT} "
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
version="trunk";
outputDir="$PWD"


# Parse command-line parameters
while getopts "hv:d:" option
    # : after option shows it will have an argument passed with it.
do
    case $option in
        h ) 
            printUsage ;;
        v ) 
            version="tags/$OPTARG";;
        d ) 
            outputDir="$OPTARG";;
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
info )
    displayModules "${prjSvnroot}" "${project}" "${modules}" ;;
*)
    echo "ERROR: Action '$userAction' not supported"
    printUsage ;;
    esac
done
