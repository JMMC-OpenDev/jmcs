/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtGUI.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file
 * gui class declaration file.
 */

static char *rcsId="@(#) $Id: gwtGUI.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);


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
int gwtGUI::_remoteGuiSd=-1;

/**
 * Class constructor
 */
gwtGUI::gwtGUI()
{
    logExtDbg("gwtGUI::gwtGUI()");    
}


/**
 * Class destructor
 */
gwtGUI::~gwtGUI()
{
    logExtDbg("gwtGUI::~gwtGUI()");    
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

    struct hostent    *remoteHostEnt;

    int                sd;
    struct sockaddr_in addr;
    mcsINT32           nbRetry;
    mcsINT32           status;


    /* If a connection is already open... */
    if (_remoteGuiSd != -1)
    {
        /* Raise an error */
        //GM         errAdd(msgERR_PROC_ALREADY_CONNECTED);

        /* Return an error code */
        //GM return FAILURE;
        cout << "Erreur1"<<endl;
        return FAILURE;
    }

    remoteHostEnt = gethostbyname(hostname.data());
    if (remoteHostEnt == (struct hostent *) NULL)
    {
        //GM         errAdd(msgERR_GETHOSTBYNAME, strerror(errno));
        //GM  return FAILURE;
        cout << "Erreur2"<<endl;
        return FAILURE;
     }
 
     /* Try to establish the connection, retry otherwise... */
    nbRetry = 2;
    do 
    {
        /* Create the socket */
        sd = socket(AF_INET, SOCK_STREAM, 0);
        if(sd == -1)
        { 
            //GM errAdd(msgERR_SOCKET, strerror(errno));
            //GM return FAILURE; 
            cout << "Erreur3"<<endl;
            return FAILURE;
        }

        /* Initialize sockaddr_in */
        memset((char *) &addr, 0, sizeof(addr));
        addr.sin_port = htons(port);
        memcpy(&(addr.sin_addr), remoteHostEnt->h_addr,remoteHostEnt->h_length);
        addr.sin_family = AF_INET;

        /* Try to connect to msgManager */
        status = connect(sd , (struct sockaddr *)&addr, sizeof(addr));
        if (status == -1)
        {
            if (--nbRetry <= 0 )
            { 
                //GM errAdd(msgERR_CONNECT, strerror(errno));
                //GM return FAILURE; 
                cout << "Erreur4"<<endl;
                return FAILURE;
            }
            else
            {
                logWarning("Cannot connect to remote GUI system. Trying again...");
                sleep(1);
                close(sd);
            }
        }
    } while (status == -1);

    /* Store the established connection socket */
    _remoteGuiSd = sd;

    string configStr("<config entityName=\"");
    configStr.append(procname);
    configStr.append("\" to=\"JavaGui\"></config>\n");
    Send(configStr);

    return SUCCESS;
    /* Now the evhHandler should handle socket event */
}

/*
 * Public methods
 */


/**
 * Return the socket descriptor
 *
 * \return 
 * \b Errors codes: 
 * The possible errors are:
 */
int gwtGUI::GetSd()
{
   return _remoteGuiSd; 
}

/**
 * Send a XML string to the remote gui over the socket.
 * \param xmlStr XML string to send.
 */
void gwtGUI::Send(string xmlStr)
{
    logExtDbg("gwtGUI::Send()");
    
    write(_remoteGuiSd, xmlStr.data(), xmlStr.length());
    fflush(NULL); 
}


/**
 * Set the status on the remote gwt. This make appear a valid (green) or
 * invalid (red) icon in the status bar of the client. After each command
 * returned by the client, it's status is in waiting status (orange icon),
 * then it is recommended to return the status of the application after each
 * callback code.
 * \param valid says if everithing is right or not
 * \param message is the status message
 * \todo modify xml api for the command attribute
 */
void gwtGUI::SetStatus(bool valid, string message)
{
    // build the xml string
    string s;
    s.append("<gwt_status valid=\"");
    if (valid){
        s.append("true");
    }else{
        s.append("false");
    }
    s.append("\" text=\"");
    s.append(message);
    s.append("\">\n");
    s.append("</gwt_status>\n");

    // and send it
    Send(s);
}

/**
 * Used by each program that get data which are gui related from the event handler.
 * This part parses the text returned by the XMLBASEDGUI and would be much
 * better handled with xml(this point is in the wish list of the XMLBASEDGUI
 * module).
 *
 * \param data the received buffer 
 */
void gwtGUI::ReceiveData(string data)
{
    logExtDbg("gwtGUI::receiveData()");    
   

    // do bad job correction : skip the trailing CR at the end of the data
    int size = data.size();
    char c =  data.at(size-1);
    if(c=='\n'){
        data = data.substr(0,size-1);
    }

    
    /* check if data is the return of a variable associated widget */
    string letStr = data.substr(3);
    if(data.compare(0, 3, "let") == 0)
    {
        /* it is a value associated widget */
        logDebug("gwtGUI has received for value: %s" , data.data());
        int firstSpaceIdx = data.find_first_of(" ");
        string afterSpaceStr = data.substr(firstSpaceIdx+1);
        int secondSpaceIdx = afterSpaceStr.find_first_of(" ");
        string widgetStr = afterSpaceStr.substr(0,secondSpaceIdx);
        string afterWidgetStr = afterSpaceStr.substr(secondSpaceIdx+1);
        DispatchGuiReturn(widgetStr, afterWidgetStr);
    }else{
        /* it is a command widget (buttons) */
        logDebug("gwtGUI has received for command: %s" , data.data());

        DispatchGuiReturn(data.data(),"");
    }
    
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

    logDebug ("register new xml producer referenced by: %s",id.data());

    _children.insert ( make_pair(id,producer));
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
        tmpProducer->DispatchGuiReturn(widgetid, data); 
        i++;
    } 
    
}
/*___oOo___*/
