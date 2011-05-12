/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file simple test program. It connects on the remotegui, build a gui
 * description and send its description to the gwt.
 */

static char *rcsId __attribute__ ((unused)) ="@(#) $Id: gwtTest.C,v 1.16 2006-05-11 13:04:55 mella Exp $";
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

class gwtTestSERVER : public gwtGUI
{
public:
    gwtTestSERVER();
    virtual ~gwtTestSERVER();
    mcsCOMPL_STAT WindowCloseCB(void *userData);
    mcsCOMPL_STAT Button1CB(void *userData);
    mcsCOMPL_STAT Button3CB(void *userData);
    virtual mcsCOMPL_STAT AppInit();
    void DoIt();
private:
    gwtTEXTFIELD *textfield1;
    gwtWINDOW *window1; 
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
    delete button1;
    delete button2;
    delete button3;
    delete separator;
    delete label1;
    delete table1;
    delete menu;
}

mcsCOMPL_STAT gwtTestSERVER::AppInit()
{
    logExtDbg("gwtTestSERVER::AppInit()");
    
    // Prepare a menu 
    menu = new gwtMENU("menu de test");
    RegisterXmlProducer(menu);
    menu->Show();
    
    // Prepare a window
    window1->SetTitle("First Window");
    window1->SetCloseCommand("window_1_closed");
    window1->AttachAGui(this);
    
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
    
    // Attach close CB
    window1->AttachCB(this, (gwtCOMMAND::CB_METHOD) & gwtTestSERVER::WindowCloseCB);
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
 *  User callback associated to the window closing event. 
 *  The application exits.
 */
mcsCOMPL_STAT gwtTestSERVER::WindowCloseCB(void *)
{
    cout<< "Window's close button pressed" << " while the textfield value is:" << textfield1->GetText() <<endl;
    SetStatus(true, "Application exists");
    
    window1->Hide();
    exit(0);
    return mcsSUCCESS;
}

/**
 *  User callback associated to the button 1. 
 *  The panel is updated after the textfield
 */
mcsCOMPL_STAT gwtTestSERVER::Button1CB(void *)
{
    cout<< "button1 pressed" << " while the textfield value is:" << textfield1->GetText() <<endl;
    SetStatus(true, "");
    
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
    SetStatus(true, "Window closed properly");
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
    SetStatus(false, msg, "Because");
    
    // close the window 
    window1->Hide();
   
    // and show it again
    window1->Show();
}


int main(int argc, char *argv[])
{
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
    }
    exit (EXIT_SUCCESS);
}

/*___oOo___*/
