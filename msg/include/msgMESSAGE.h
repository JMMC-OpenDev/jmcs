#ifndef msgMESSAGE_H
#define msgMESSAGE_H
/*******************************************************************************
* JMMC project
*
* "@(#) $Id: msgMESSAGE.h,v 1.3 2004-11-22 14:31:38 gzins Exp $"
*
* who       when         what
* --------  -----------  -------------------------------------------------------
* scetre    17-Nov-2004  Created
* lafrasse  19-Nov-2004  Changed all method name first letter to upper case, and
*                        re-commented
*
*
*******************************************************************************/

/**
 * \file
 * msgMESSAGE class declaration.
 */

/*
 * MCS Headers 
 */
#include "mcs.h"

/* 
 * Constants definition
 */

/**
 * Message waiting constants
 */
#define msgNO_WAIT               0   
#define msgWAIT_FOREVER         -1

/**
 * Maximum message size
 */
#define msgMAXLEN                8192

/**
 * Standard command names
 */
#define msgDEBUG_CMD            "DEBUG"
#define msgPING_CMD             "PING"
#define msgEXIT_CMD             "EXIT"
#define msgVERSION_CMD          "VERSION"

/* 
 * Macro definition
 */

/**
 * Maximum message body size
 */
#define msgBODYMAXLEN            (msgMAXLEN - sizeof(msgHEADER) - 1)

/*
 * Enumeration type definition
 */

/**
 * Message types enumeration
 */
typedef enum
{
    msgTYPE_COMMAND = 1,         /**< Describe command messages. */
    msgTYPE_REPLY,               /**< Describe reply messages. */
    msgTYPE_ERROR_REPLY          /**< Describe error reply messages. */
} msgTYPE;

/* 
 * Structure type definition
 */

/**
 * Message header structure
 */
typedef struct
{
    mcsPROCNAME sender;          /**< Sender processus name */
    mcsENVNAME  senderEnv;       /**< Sender environnement */
    mcsPROCNAME recipient;       /**< Receiver processus name  */
    mcsENVNAME  recipientEnv;    /**< Receiver environnement */
    mcsSTRING8  identifier;      /**< Identificator */
    mcsLOGICAL  isInternal;      /**< FALSE if it is external >*/
    msgTYPE     type;            /**< Message type */
    mcsCMD      command;         /**< Command name */
    mcsLOGICAL  lastReply;       /**< TRUE if it is the last answer */
    mcsBYTES32  timeStamp;       /**< Message date */
    mcsSTRING8  msgBodySize;     /**< Message body size */
} msgHEADER;

/**
 * Complete message structure
 */
typedef struct
{
    msgHEADER  header;
    char       body[msgMAXLEN - sizeof(msgHEADER)];

} msgMESSAGE_RAW;

#ifdef __cplusplus
/*
 * Class declaration
 */

/**
 * msgMESSAGE is a class that wraps the msgMESSAGE_RAW C structure, with all the
 * needed accessors.
 *
 * It is used to encapsulate all the data to be sent and received with the help
 * of the msgMANAGER_IF object.
 * 
 * \sa msgMESSAGE_RAW and msgHEADER C structures, and msg.h in general
 * \sa msgMANAGER_IF
 */

class msgMESSAGE
{

public:
    // Constructor
    msgMESSAGE                               (const mcsLOGICAL isInternal
                                              = mcsFALSE);

    // Destructor
    virtual ~msgMESSAGE                      ();
    
    // Accessors
    virtual char*            GetSender       ();
    virtual mcsCOMPL_STAT    SetSender       (const char     *buffer);

    virtual char*            GetSenderEnv    ();
    virtual mcsCOMPL_STAT    SetSenderEnv    (const char     *senderEnv);

    virtual char*            GetRecipient    ();
    virtual mcsCOMPL_STAT    SetRecipient    (const char     *recipient);

    virtual char*            GetRecipientEnv ();
    virtual mcsCOMPL_STAT    SetRecipientEnv (const char     *recipientEnv);

    virtual msgTYPE          GetType         ();
    virtual mcsCOMPL_STAT    SetType         (const msgTYPE   type);

    virtual char*            GetIdentifier   ();
    virtual mcsCOMPL_STAT    SetIdentifier   (const char     *identificator);

    virtual char*            GetCommand      ();
    virtual mcsCOMPL_STAT    SetCommand      (const char     *command);

    virtual mcsLOGICAL       IsLastReply     ();
    virtual mcsLOGICAL       IsInternal      ();

    virtual msgHEADER*       GetHeaderPtr    ();

    virtual char*            GetBodyPtr      ();
    virtual mcsINT32         GetBodySize     ();
    virtual mcsCOMPL_STAT    SetBody         (const char     *buffer,
                                              const mcsINT32  bufLen=0);

    virtual msgMESSAGE_RAW*  GetMessageRaw   ();


protected:

    
private:
     // The only member
     msgMESSAGE_RAW _message;

    // Declaration of copy constructor and assignment operator as private
    // methods, in order to hide them from the users.
     msgMESSAGE(const msgMESSAGE&);
     msgMESSAGE& operator=(const msgMESSAGE&);
};
#endif

#endif /*!msgMESSAGE_H*/

/*___oOo___*/
