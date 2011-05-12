/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/

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

