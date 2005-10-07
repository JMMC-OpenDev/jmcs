#ifndef gwtGUI_H
#define gwtGUI_H
/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtGUI.h,v 1.6 2005-10-07 06:56:11 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2005/02/24 11:08:01  mella
 * Add reason to SetStatus
 *
 * Revision 1.4  2005/02/15 12:33:49  gzins
 * Updated file description
 *
 * Revision 1.3  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtGUI class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif

//class gwtWIDGET;
//class gwtWINDOW;

#include "gwtWIDGET.h"
#include "gwtWINDOW.h"
#include "evh.h"

/*
 * Class declaration
 */

/** 
 * Main Graphical User Interface class.
 * The gwtGUI is the central point of any GUI that uses the gui module.
 * This class handles local widgets and communication with the remote displayer.
 * It is linked to the remote displayer using a socket connection. This
 * connection is used by the event handler.
 */
class gwtGUI: public evhSERVER
{
public:
    gwtGUI();
    virtual ~gwtGUI();

    virtual mcsCOMPL_STAT AddionalInit();
    virtual void SetStatus(bool valid, string status, string explanation="");
    virtual void Send(string xmlStr);
    virtual void RegisterXmlProducer(gwtXML_PRODUCER *producer);
    /** 
     * typedef for map of windows
     */
    typedef map<string, gwtXML_PRODUCER *> gwtMAP_STRING2PRODUCER;
protected:
    virtual evhCB_COMPL_STAT ReceiveDataCB(const int sd, void *userData);

    gwtMAP_STRING2PRODUCER _children; 
private:
    msgSOCKET_CLIENT *_clientSocket;
    virtual void DispatchGuiReturn(string widgetid, string data);
    virtual mcsCOMPL_STAT ConnectToRemoteGui(const string hostname, const int port, const string procname);
};

#endif /*!gwtGUI_H*/

/*___oOo___*/
