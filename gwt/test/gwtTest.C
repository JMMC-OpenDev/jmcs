/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: gwtTest.C,v 1.12 2005-03-08 12:41:25 mella Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.11  2005/03/02 14:54:17  mella
 * Test dimension change for the table
 *
 * Revision 1.10  2005/03/02 13:08:09  mella
 * Test the window update mechanism
 *
 * Revision 1.9  2005/02/28 12:44:57  mella
 * Add height test for Table
 *
 * Revision 1.8  2005/02/24 13:33:04  mella
 * Add gwtLABEL
 *
 * Revision 1.7  2005/02/24 12:40:32  mella
 * Add a status that give a reason at startup
 *
 * Revision 1.6  2005/02/15 12:25:28  gzins
 * Changed SUCCESS/FAILURE to mcsSUCCESS/mcsFAILURE
 *
 * Revision 1.5  2005/02/15 12:17:52  gzins
 * Added CVS log as file modification history
 *
 * mella     14-Sep-2004  Created
 *
 ******************************************************************************/

/**
 * \file simple test program. It connects on the remotegui, build a gui
 * description and send its description to the gwt.
 */

static char *rcsId="@(#) $Id: gwtTest.C,v 1.12 2005-03-08 12:41:25 mella Exp $"; 
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

class gwtTestSERVER : public evhSERVER
{
public:
    gwtTestSERVER();
    virtual ~gwtTestSERVER();
    virtual evhCB_COMPL_STAT SocketCB (const int sd,void* obj);
    mcsCOMPL_STAT Button1CB(void *userData);
    mcsCOMPL_STAT Button3CB(void *userData);
    virtual mcsCOMPL_STAT AppInit();
    void DoIt();
private:
    gwtTEXTFIELD *textfield1;
    gwtWINDOW *window1; 
    gwtGUI *oneGui;
    gwtLABEL *label1;
    gwtBUTTON *button1;
    gwtBUTTON *button2;
    gwtBUTTON *button3;
    gwtSEPARATOR *separator;
    gwtTABLE *table1;

    gwtMENU *menu;
};

gwtTestSERVER::gwtTestSERVER()
{
    logExtDbg("gwtTestSERVER::gwtTestSERVER()");
    
    textfield1 = new gwtTEXTFIELD();
    window1 = new gwtWINDOW(); 
    oneGui = new gwtGUI();
    label1 = new gwtLABEL("blah blah blah        ...","I did not place any help ");
    button1 = new gwtBUTTON();
    button2 = new gwtBUTTON("button2", "help for button2");
    button3 = new gwtBUTTON();
    separator = new gwtSEPARATOR();
    table1 = new gwtTABLE(0,0);
}

gwtTestSERVER::~gwtTestSERVER()
{
    logExtDbg("gwtTestSERVER::~gwtTestSERVER()");
    delete textfield1;
    delete window1; 
    delete oneGui;
    delete button1;
    delete button2;
    delete button3;
    delete separator;
}

mcsCOMPL_STAT gwtTestSERVER::AppInit()
{
    logExtDbg("gwtTestSERVER::AppInit()");
    
    mcsCOMPL_STAT status;
    string gwtHost("localhost");
    int gwtPort = 1234;
    
    // Connect the gwtGUI to the remote system
    status = oneGui->ConnectToRemoteGui(gwtHost,gwtPort, mcsGetProcName());
    if (status == mcsFAILURE)
    {
        cout << "connection on " << gwtHost << ":" << gwtPort << " failed" << endl;
        return mcsFAILURE;
    }
    cout << "connection on " << gwtHost << ":" << gwtPort << " succeed" << endl;
    
    // prepare Event Handling 
    evhIOSTREAM_CALLBACK cb(this, (evhIOSTREAM_CB_METHOD)&gwtTestSERVER::SocketCB);
    evhIOSTREAM_KEY key(oneGui->GetSd());
    AddCallback(key, cb);
       
    // Prepare a menu 
    menu = new gwtMENU("menu de test");
    oneGui->RegisterXmlProducer(menu);
    menu->Show();
    
    // Prepare a window
    window1->SetTitle("First Window");
    window1->SetCloseCommand("window1_closed");
    window1->AttachAGui(oneGui);
    
    window1->Hide();
    
    // Prepare a button
    button1->SetHelp("No real interresting help for the button1");
    button1->SetLabel("A button");
    // Here one update for label should succeed (second value will be used)
    button1->SetLabel("A Simple button");
    // connects the button1 event to a callback
    button1->AttachCB(this, (gwtCOMMAND::CB_METHOD) &gwtTestSERVER::Button1CB);

    
    // Prepare a textfiel
    textfield1->SetHelp("No real interresting help for the textfield1");
    textfield1->SetLabel("A textfield");
    textfield1->SetText("A text for the textfield");
    
    //with a table 
    table1->SetDimension(10,5);
    table1->SetHelp("This table was filled adding rown and column");
    table1->SetLabel("A table");
    table1->SetHeight(200);
    // that takes the full window width
    table1->SetVerticalOrientation(mcsTRUE);

    int r,c;
    char str[32];
    for (c=0;c<5;c++)
    {
        sprintf(str,"Header %d",c);
        table1->SetColumnHeader(c,str);
        for (r=0;r<10;r++)
        {
            sprintf(str,"%d",r+c);
            table1->SetCell(r,c,str);
        }
    }
    // Change color for two cells
    table1->SetCellBackground(0,0,"#0FA8D0");
    table1->SetCellBackground(1,1,"#A8D00F");
    
    // Add elements and and show the window
    window1->Add(label1);
    window1->Add(button1);
    window1->Add(separator);
    window1->Add(textfield1);
    window1->Add(table1);
    
    window1->Show();
   
    /*
     * After window1->show() no added widget are added on the real gwt.
     */
    
    // add one button without callback after the window display
    window1->Add(button2);
 
    
//    window1->Add(&button3);
    // connects the button3 event to a callback
  //  button3.AttachCB(this, (gwtCOMMAND::CB_METHOD) &gwtTestSERVER::Button3CB);
 
   return mcsSUCCESS;
}

/**
 *  User callback associated to the button 1. 
 *  The panel is updated after the textfield
 */
mcsCOMPL_STAT gwtTestSERVER::Button1CB(void *)
{
    cout<< "button1 pressed" << " while the textfield value is:" << textfield1->GetText() <<endl;
    oneGui->SetStatus(true, "");
    
    // Modify textfield1 content
    string newStr;
    newStr.append(textfield1->GetText());
    newStr.append("...---'''");
    textfield1->SetText(newStr);
    
    //update the panel
    window1->Update();
    return mcsSUCCESS;
}

/**
 *  User callback associated to the button 3. 
 */
mcsCOMPL_STAT gwtTestSERVER::Button3CB(void *)
{
    cout<< "button3 pressed" <<endl;
    window1->Hide();
    oneGui->SetStatus(true, "Window closed properly");
    return mcsSUCCESS;
}


void gwtTestSERVER::DoIt()
{
    logExtDbg("gwTestSERVER::DoIt()");
    
    cout << endl;
    cout << endl;
    cout << endl;
    // Set a new status message
    string msg("Hello from ");
    msg.append(mcsGetProcName());
    oneGui->SetStatus(false, msg, "Because");
    
    // close the window 
    window1->Hide();
   
    // and show it again
    window1->Show();
}

/**
 *  Main callback used to get back events from the GUI. Actually there must be
 *  only one evh callbak to inform the gwt. 
 *
 * \param sd  the socket descriptor
 * \param obj nc
 *
 * \return  mcsSUCCESS or mcsFAILURE
 */
evhCB_COMPL_STAT gwtTestSERVER::SocketCB (const int sd,void* obj)
{
    logExtDbg("gwtTestSERVER::SocketCB()");

    int size;
    mcsSTRING80 msg;
    size = read(sd, msg,80);
    msg[size]=0;
    string data(msg);
    oneGui->ReceiveData(data);

    return evhCB_SUCCESS;
}


int main(int argc, char *argv[])
{
    gwtTestSERVER gwtTestServer;
    logInfo("Server initing ..");

    // Init server
    if (gwtTestServer.Init(argc, argv) == mcsFAILURE)
    {
        errDisplayStack();
        exit (EXIT_FAILURE);
    }
   
    logInfo("Server starting ..");
    gwtTestServer.DoIt();

    // Main loop
    if (gwtTestServer.MainLoop() == mcsFAILURE)
    {
        errDisplayStack();
    }
    
    logInfo("Server exiting ..");

    exit (EXIT_SUCCESS);
}

/*___oOo___*/
