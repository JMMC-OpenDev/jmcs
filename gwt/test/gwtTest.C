/*******************************************************************************
* JMMC project
*
* "@(#) $Id: gwtTest.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* mella     14-Sep-2004  Created
*
*
*******************************************************************************/

/**
 * \file simple test program. It connects on the remotegui, build a gui
 * description and send its description to the gwt.
 */

static char *rcsId="@(#) $Id: gwtTest.C,v 1.1 2004-11-25 14:27:52 gzins Exp $"; 
static void *use_rcsId = ((void)&use_rcsId,(void *) &rcsId);

/* 
 * System Headers 
 */
#include <stdlib.h>
#include <iostream>

/**
 * \namespace std
 * Export standard iostream objects (cin, cout,...).
 */
using namespace std;

/*
 * MCS Headers 
 */
#include "mcs.h"
#include "log.h"
#include "err.h"
#include "evh.h"
#include "fnd.h"

#include "evhCALLBACK.h"
#include "evhCMD_CALLBACK.h"
#include "evhIOSTREAM_CALLBACK.h"
#include "evhHANDLER.h"
#include "evhKEY.h"
#include "evhCMD_KEY.h"
#include "evhIOSTREAM_KEY.h"

/*
 * Local Headers 
 */
#include "gwt.h"
#include "gwtPrivate.h"

/*
 * Local Variables
 */

/* 
 * Signal catching functions  
 */

class gwtTestSERVER : public evhTASK, public fndOBJECT
{
public:
    gwtTestSERVER() {};
    virtual ~gwtTestSERVER() {};
    virtual mcsCOMPL_STAT PrintAppUsage();
    virtual mcsCOMPL_STAT ParseAppOptions(mcsINT32 argc, mcsINT8 *argv[],
                                          mcsINT32 *optind);
    virtual evhCB_COMPL_STAT SocketCB (const int sd,void* obj);
    void go();
    mcsCOMPL_STAT Button1CB(void *userData);
    mcsCOMPL_STAT Button3CB(void *userData);
private:
    mcsLOGICAL  noTimeout;
    mcsBYTES256 configFileName;
    
    gwtTEXTFIELD textfield1;
    gwtWINDOW window1; 
    gwtGUI oneGui;
};




/**
 *  User callback associated to the button 1. 
 */
mcsCOMPL_STAT gwtTestSERVER::Button1CB(void *)
{
    cout<< "button1 pressed" << " while the textfield value is:" << textfield1.GetText() <<endl;
    oneGui.SetStatus(true, "");
    return SUCCESS;
}

/**
 *  User callback associated to the button 3. 
 */
mcsCOMPL_STAT gwtTestSERVER::Button3CB(void *)
{
    cout<< "button3 pressed" <<endl;
    window1.Hide();
    oneGui.SetStatus(true, "Window closed properly");
    return SUCCESS;
}

/**
 * Build a simple gwt.
 *
 */
void gwtTestSERVER::go(){
    mcsCOMPL_STAT status;
    string gwtHost("localhost");
    int gwtPort = 1234;
    
    // Connect the gwtGUI to the remote system
    status = oneGui.ConnectToRemoteGui(gwtHost,gwtPort, mcsGetProcName());
    if (status == FAILURE)
    {
        cout << "connection on " << gwtHost << ":" << gwtPort << " failed" << endl;
        return ;
    }
    cout << "connection on " << gwtHost << ":" << gwtPort << " succeed" << endl;

    // prepare Event Handler 
    evhHANDLER evhHandler;
    evhIOSTREAM_CALLBACK cb(this, (evhIOSTREAM_CB_METHOD)&gwtTestSERVER::SocketCB);
    evhIOSTREAM_KEY key(oneGui.GetSd());
    evhHandler.AddCallback(key, cb);
    
    // Prepare a window
    window1.SetTitle("First Window");
    window1.AttachAGui(&oneGui);
    
    // Prepare a button
    gwtBUTTON button1;
    button1.SetHelp("No real interresting help for the button1");
    button1.SetLabel("A button");
    // Here one update for label should succeed (second value will be used)
    button1.SetLabel("A Simple button");
    // connects the button1 event to a callback
    button1.AttachCB(this, (gwtCOMMAND::CB_METHOD) &gwtTestSERVER::Button1CB);

    // Prepare a separator
    gwtSEPARATOR separator;
    
    // Prepare a textfiel
    textfield1.SetHelp("No real interresting help for the textfield1");
    textfield1.SetLabel("A textfield");
    textfield1.SetText("A text for the textfield");
    
    //with a table
    gwtTABLE table1(10,5);
    table1.SetHelp("This table was filled adding rown and column");
    table1.SetLabel("A table");
    int r,c;
    char str[32];
    for (c=0;c<5;c++)
    {
        sprintf(str,"Header %d",c);
        table1.SetColumnHeader(c,str);
        for (r=0;r<10;r++)
        {
            sprintf(str,"%d",r+c);
            table1.SetCell(r,c,str);
        }
    }
    
    // Add elements and and show the window
    window1.Add(&button1);
    window1.Add(&separator);
    window1.Add(&textfield1);
    window1.Add(&table1);
    window1.Show();
    
    /*
     * After window1.show() no added widget are added on the real gwt.
     */
    
    // add one button without callback
    gwtBUTTON *button2 = new gwtBUTTON("button2", "help for button2");
    window1.Add(button2);
 
    
    gwtBUTTON button3("close", "Click on this button close the window");
    window1.Add(&button3);
    // connects the button3 event to a callback
    button3.AttachCB(this, (gwtCOMMAND::CB_METHOD) &gwtTestSERVER::Button3CB);

    // Set a new status message
    string msg("Hello from ");
    msg.append(mcsGetProcName());
    oneGui.SetStatus(true, msg);
    
    // close the window 
    window1.Hide();
   
    // and show it again
    window1.Show();
  
    // and wait events
    if (evhHandler.MainLoop() == FAILURE)
    {
        errDisplayStack();
    }
    
    logInfo("Server exiting ..");
    
    return ;
}

/**
 *  Main callback used to get back events from the GUI. Actually there must be
 *  only one evh callbak to inform the gwt. 
 *
 * \param sd  the socket descriptor
 * \param obj nc
 *
 * \return  SUCCESS or FAILURE
 */
evhCB_COMPL_STAT gwtTestSERVER::SocketCB (const int sd,void* obj)
{
    logExtDbg("gwtTestSERVER::SocketCB()");

    int size;
    mcsSTRING80 msg;
    size = read(sd, msg,80);
    msg[size]=0;
    string data(msg);
    oneGui.ReceiveData(data);

    return evhCB_SUCCESS;
};


int main(int argc, char *argv[])
{
    gwtTestSERVER gwtTestServer;

    // Parse input parameter
    if (gwtTestServer.ParseOptions(argc, argv) == FAILURE)
    {
        exit (EXIT_FAILURE);
    }
    logInfo("Server starting ..");

    // Start real process
    gwtTestServer.go();
    exit (EXIT_SUCCESS);
}

mcsCOMPL_STAT gwtTestSERVER::PrintAppUsage()
{
    logExtDbg("gwtTestSERVER::PrintAppUsage()"); 

    cout <<" Other options:    -noTimeout   disable waiting for a reply on "
        "a CCS message" << endl;
    cout <<"                   -c <file>    specify application "
        "configuration file" << endl;
    return SUCCESS;
}

mcsCOMPL_STAT gwtTestSERVER::ParseAppOptions(mcsINT32 argc, mcsINT8 *argv[],
                                           mcsINT32 *optind)
{
    logExtDbg("gwtTestSERVER::ParseAppOptions()"); 

    // No timeout option
    if(strcmp(argv[*optind], "-noTimeout") == 0)
    {
        noTimeout = mcsTRUE;
        return SUCCESS;
    }
    // Application configuration file option
    else if (strcmp(argv[*optind], "-c") == 0)
    {
        if ((*optind + 1) < argc)
        {
            *optind += 1;
            optarg = argv[*optind];
            if ( sscanf (optarg, "%s", configFileName) != 1)
            {
                logWarning ("%s: Argument to option %s is invalid: '%s'",
                            Name(), argv[*optind-1], optarg);
                return FAILURE;
            }
            return SUCCESS;
        }
        else
        {
            logWarning ("%s: Option %s requires an argument",
                        Name(), argv[*optind]);
            return FAILURE;
        }
    }

    // Invalid argument
    logWarning ("%s: Invalid argument %s", Name(), argv[*optind] );
    return FAILURE;
}

/*___oOo___*/
