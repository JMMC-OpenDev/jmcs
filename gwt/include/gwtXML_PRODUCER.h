#ifndef gwtXML_PRODUCER_H
#define gwtXML_PRODUCER_H
/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

/**
 * \file
 * Declaration of gwtXML_PRODUCER class.
 */


#ifndef __cplusplus
#error This is a C++ include file and cannot be used from plain C
#endif


class gwtGUI;

/*
 * Class declaration
 */

/**
 * This class should be used by object that can send xml strings and get back
 * change events.
 */
class gwtXML_PRODUCER
{

public:
    // Brief description of the constructor
    gwtXML_PRODUCER();

    // Brief description of the destructor
    virtual ~gwtXML_PRODUCER();

    virtual void AttachAGui(gwtGUI * g);
    virtual void SendXml(string data);
    virtual void SetProducerId(string id);
    virtual void ReceiveFromGui(string widgetid, string data);

protected:

    
private:
    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
     gwtXML_PRODUCER(const gwtXML_PRODUCER&);
     gwtXML_PRODUCER& operator=(const gwtXML_PRODUCER&);

    gwtGUI * _attachedGui;

};

#endif /*!gwtXML_PRODUCER_H*/

/*___oOo___*/