#ifndef cmdCOMMAND_H
#define cmdCOMMAND_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: cmdCOMMAND.h,v 1.5 2004-12-21 16:53:25 mella Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     15-Nov-2004  Created
* gzins     06-Dec-2004  Renamed _hasNotBeenYetParsed to _hasBeenYetParsed
* gzins     09-Dec-2004  Added pure virtual Parse() method
*                        Added cdfFilename argument to previous Parse() method
*
*******************************************************************************/

/**
 * \file
 * cmdCOMMAND class declaration.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

#include <map>
#include "cmdPARAM.h"


#include <libgdome/gdome.h>
#include <libxml/parser.h>

/*
 * Class declaration
 */

/**
 * This class is used to get a model for any MCS command. Each command should
 * have a Command Definition File (SAMPLE.cdf). The file cmdDefinitionFile.xsd
 * describes the xml format that must follow each cdf file.
 */
class cmdCOMMAND
{
public:
    // Brief description of the constructor
    cmdCOMMAND(string name, string params);

    // Brief description of the destructor
    virtual ~cmdCOMMAND();

    /** typedef for map of cmdPARAM */
    typedef map<string, cmdPARAM *> STRING2PARAM;

    virtual mcsCOMPL_STAT Parse()=0;
     
    virtual mcsCOMPL_STAT GetHelp(string &help);
    
    /* methods to handle parameters */
    virtual mcsCOMPL_STAT AddParam(cmdPARAM *param);
    virtual mcsCOMPL_STAT GetParam(string paramName, cmdPARAM **param);
    virtual mcsLOGICAL IsDefined(string paramName);
    virtual mcsLOGICAL HasDefaultValue(string paramName);
    virtual mcsLOGICAL IsOptional(string paramName);
    virtual mcsCOMPL_STAT GetParamValue(string paramName, mcsINT32 *param);
    virtual mcsCOMPL_STAT GetParamValue(string paramName, char **param);
    virtual mcsCOMPL_STAT GetParamValue(string paramName, mcsDOUBLE *param);
    virtual mcsCOMPL_STAT GetParamValue(string paramName, mcsLOGICAL *param);
    virtual mcsCOMPL_STAT GetDefaultParamValue(string paramName, 
                                               mcsINT32 *param);
    virtual mcsCOMPL_STAT GetDefaultParamValue(string paramName, 
                                               char **param);
    virtual mcsCOMPL_STAT GetDefaultParamValue(string paramName,
                                               mcsDOUBLE *param);
    virtual mcsCOMPL_STAT GetDefaultParamValue(string paramName,
                                               mcsLOGICAL *param);

protected:
    virtual mcsCOMPL_STAT Parse(string cdfFilename);
    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
     cmdCOMMAND(const cmdCOMMAND&);
     cmdCOMMAND& operator=(const cmdCOMMAND&);

     virtual mcsCOMPL_STAT ParseCdf(string cdfFilename);
     virtual mcsCOMPL_STAT ParseCdfForDesc(GdomeElement *node);
     virtual mcsCOMPL_STAT ParseCdfForParameters(GdomeElement *node);
     virtual mcsCOMPL_STAT ParseCdfForParam(GdomeElement *param);
     virtual mcsCOMPL_STAT CmdGetNodeContent(GdomeElement *parentNode,
                                             string tagName, string &content);

     virtual mcsCOMPL_STAT ParseParams();
     virtual mcsCOMPL_STAT ParseTupleParam(string tuple);
     
     virtual mcsCOMPL_STAT CheckParams();
     
     virtual mcsCOMPL_STAT SetDescription(string desc);

     /** given string to the constructor */
     string _params;
     /** map of params */
     STRING2PARAM _paramList;
     /** Flag that indicates if the params have been parsed */
     mcsLOGICAL _hasBeenYetParsed;
     /** name of the command */
     string _name;
     /** description of the command */
     string _desc;
};

#endif /*!cmdCOMMAND_H*/

/*___oOo___*/
