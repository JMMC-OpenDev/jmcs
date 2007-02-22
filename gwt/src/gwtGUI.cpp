/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtGUI.cpp,v 1.11 2007-02-22 12:49:10 gzins Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.10  2006/05/11 13:04:55  mella
 * Changed rcsId declaration to perform good gcc4 and gcc3 compilation
 *
 * Revision 1.9  2005/10/07 06:56:11  gzins
 * Inherited from evhSERVER class; handled connection to XML GUI server, and reception and dispatching of messages from this server.
 *
 * Revision 1.8  2005/09/28 14:02:10  mella
 * Improve memory deallocation into destructor
 *
 * Revision 1.7  2005/03/08 11:22:27  mella
 * Change %80 into %.80 for logInfo
 *
 * Revision 1.6  2005/03/08 11:08:33  mella
 * Place logInfo for message exchanges with Xml Java Side
 *
 * Revision 1.5  2005/02/24 13:38:15  mella
 * Correct doxygen missing param
 *
 * Revision 1.4  2005/02/24 12:34:58  mella
 * Correct xml syntax error for status
 *
 * Revision 1.3  2005/02/24 11:08:02  mella
 * Add reason to SetStatus
 *
 * Revision 1.2  2005/02/15 12:25:28  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.1  2005/01/27 18:09:35  gzins
 * Renamed .C to .cpp
 * Added CVS loh as modification history.
 *
 * mella     14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file
 * Definition of gwtGUI class.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtGUI.cpp,v 1.11 2007-02-22 12:49:10 gzins Exp $";

/* 
 * System Headers 
 */
#include <iostream>
#include <sstream>
using namespace std;
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include "stdarg.h"
#include "msg.h"

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"


/*
 * Local Headers 
 */
#include "gwtGUI.h"
#include "gwtPrivate.h"

/**
 * Socket descriptor for the remote gui system.
 */

/**
 * Class constructor
 */
gwtGUI::gwtGUI()
{
    logExtDbg("gwtGUI::gwtGUI()");   
    _clientSocket = new msgSOCKET_CLIENT();
}


/**
 * Class destructor
 */
gwtGUI::~gwtGUI()
{
    logExtDbg("gwtGUI::~gwtGUI()");    
    delete _clientSocket;
}

/**
 * Initialization of servers.
 * 
 * It established connection with the remote GUI server.
 *
 * \return mcsSUCCESS on successful completion or mcsFAILURE otherwise. 
 */
mcsCOMPL_STAT gwtGUI::AddionalInit()
{
    logTrace("gwtGUI::AddionalInit()");

    // Registers application to MCS services and parses the command-line
    // parameters
    if (evhSERVER::AddionalInit() == mcsFAILURE)
    {
        return (mcsFAILURE);
    }

    // Retrieve port number used to communicate with GUI server
    mcsINT32    port;
    if (miscGetEnvVarIntValue("GILDASGUIPORT", &port) == mcsFAILURE)
    {
        return (mcsFAILURE);
    }

    if (ConnectToRemoteGui("localhost", port, mcsGetProcName()) == mcsFAILURE)
    {
        return mcsFAILURE;
    }

    return mcsSUCCESS;
}
/**
 * Set the location of the remote GUI system and connect. After this call, the
 * evh Handler can trig event on the socket descriptor.
 *
 * \param hostname Host name for socket connection 
 * \param port  Port for socket connection
 * \param procname  Application name
 *
 */
mcsCOMPL_STAT gwtGUI::ConnectToRemoteGui(const string hostname, const int port, const string procname)
{
    logExtDbg("gwtGUI::ConnectToRemoteGui()");

    logDebug("Connection will be done on %s:%d",hostname.data(), port);

    if(_clientSocket->Open(hostname, port)==mcsFAILURE)
    {
        return mcsFAILURE;
    }
    

    string configStr("<config entityName=\"");
    configStr.append(procname);
    configStr.append("\" to=\"JavaGui\"></config>\n");
    Send(configStr);

    // Prepare Event Handling for reception of messages from the gui
    evhIOSTREAM_CALLBACK ioStreamCB
        (this, (evhIOSTREAM_CB_METHOD)&gwtGUI::ReceiveDataCB);
    evhIOSTREAM_KEY ioStreamKey(_clientSocket->GetDescriptor());
    AddCallback(ioStreamKey, ioStreamCB);

    return mcsSUCCESS;
    /* Now the evhHandler should handle socket event */
}

/*
 * Public methods
 */


/**
 * Send a XML string to the remote gui over the socket.
 * \param xmlStr XML string to send.
 * \todo test error cases
 */
void gwtGUI::Send(string xmlStr)
{
    logTrace("gwtGUI::Send()");    
    logInfo("Send '%.80s' to Xml Gui server",xmlStr.data());    
   
    _clientSocket->Send(xmlStr);
}


/**
 * Set the status on the remote gwt. This make appear a valid (green) or
 * invalid (red) icon in the status bar of the client. After each command
 * returned by the client, it's status is in waiting status (orange icon),
 * then it is recommended to return the status of the application after each
 * callback code.
 * \param valid says if everithing is right or not
 * \param status is the status message
 * \param explanation is the detailled information about the returned status 
 * \todo modify xml api for the command attribute
 */
void gwtGUI::SetStatus(bool valid, string status, string explanation )
{
    logExtDbg("gwtGUI::SetStatus()");
    // build the xml string
    string s;
    s.append("<gui_status valid=\"");
    if (valid)
    {
        s.append("true");
    }
    else
    {
        s.append("false");
    }
    s.append("\" text=\"");
    s.append(status);
    if (! explanation.empty())
    {
        s.append("\" ");
        s.append(" reason=\"");
        s.append(explanation);
    }
    s.append("\">\n");
    s.append("</gui_status>\n");

    // and send it
    Send(s);
}

/**
 * Used by each program that get data which are gui related from the event handler.
 * This part parses the text returned by the XMLBASEDGUI and would be much
 * better handled with xml(this point is in the wish list of the XMLBASEDGUI
 * module).
 *
 * \param sd descriptor of socket where the data come from
 * \param userData pointer to user data (not used)
 */
evhCB_COMPL_STAT  gwtGUI::ReceiveDataCB (const int sd, void* userData)
{
    logTrace("gwtGUI::ReceiveDataCB()");    
   
    // Read message coming from XML server
    int size;
    char msg[1024];
    size = read(sd, msg,1024);
    msg[size]=0;
    string data(msg);

    // do bad job correction : skip the trailing CR at the end of the data
    size = data.size();
    char c =  data.at(size-1);
    if(c=='\n')
    {
        data = data.substr(0,size-1);
    }

    /* check if data is the return of a variable associated widget */
    string letStr = data.substr(3);
    if (data.compare(0, 3, "let") == 0)
    {
        /* it is a value associated widget */
        logDebug("gwtGUI has received for value: %s" , data.data());
        int firstSpaceIdx = data.find_first_of(" ");
        string afterSpaceStr = data.substr(firstSpaceIdx+1);
        int secondSpaceIdx = afterSpaceStr.find_first_of(" ");
        string widgetStr = afterSpaceStr.substr(0,secondSpaceIdx);
        string afterWidgetStr = afterSpaceStr.substr(secondSpaceIdx+1);
        DispatchGuiReturn(widgetStr, afterWidgetStr);
    }
    else
    {
        /* it is a command widget (buttons) */
        logDebug("gwtGUI has received for command: %s" , data.data());

        DispatchGuiReturn(data.data(),"");
    }
    
    return evhCB_SUCCESS;
}


/**
 * Register a component that can generate xml data for the client. 
 * This registration makes possible to link an event to a widget.
 *
 * \param producer the xml producer to register
 */
void gwtGUI::RegisterXmlProducer(gwtXML_PRODUCER *producer)
{
    logExtDbg("gwtGUI::RegisterXmlProducer()");
    /* give an id to the producer */
    string id;
    ostringstream osstring;
    osstring << "p_" <<_children.size();
    id = osstring.str();
    producer->SetProducerId(id);

    logDebug ("register new xml producer referenced by: %s", id.data());

    _children.insert (make_pair(id, producer));
}
/*
 * Protected methods
 */

/*
 * Private methods
 */

/**
 * Dispach the Gui return to the window. Actually it is returned to every
 * windows because we have no solution to know if it is for one or another
 * window...
 *
 * \param widgetid  Widget id to transmit.
 * \param data  Associated data to transmit.
 *
 */
void gwtGUI::DispatchGuiReturn(string widgetid, string data)
{
    logExtDbg("gwtGUI::DispatchGuiReturn()");
    // inform all children
    gwtMAP_STRING2PRODUCER::iterator i = _children.begin();
    while(i != _children.end())
    {
        gwtXML_PRODUCER * tmpProducer = i->second;
        tmpProducer->ReceiveFromGui(widgetid, data); 
        i++;
    } 
    
}
/*___oOo___*/
