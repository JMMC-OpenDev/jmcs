/*******************************************************************************
* JMMC project - mth Yorick plugin 
*
* Yorick plugin for MCS mathematical library
*
* "@(#) $Id: mth.i,v 1.1 2007-07-09 12:52:42 gzins Exp $"
*
* History
* -------
* $Log: not supported by cvs2svn $
*/

// Plugins
#include "mthPlugin.i"

func mth(void)
/* DOCUMENT mth(void)

  DESCRIPTION
    Main Yorick functions related to mth plugin

  AUTHORS
    - Laurence Gluck (Laurence.Gluck@obs.ujf-grenoble.fr)
    - Gerard Zins (Gerard.Zins@obs.ujf-grenoble.fr)

  CONTRIBUTIONS
    If you want to contribute with any new file related function, just append it
    to the end of file, and add it to the following function list.

  USEFUL FUNCTIONS
    - mthInterp : Linear interpolation 
*/
{
    if (am_subroutine())
    {
        write, "MCS mathematical library\n";
        help, mth;
    }   
}

