/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: ModjavaExample.java,v 1.2 2006-03-07 14:20:58 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2006/03/07 14:13:37  lafrasse
 * initial release
 *
 ******************************************************************************/

package jmmc.mcs.modjava;

/**
 * @file
 * brief description of the program, which ends at this dot.
 *
 * @synopsis
 * \e \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
 *                     [\e \<option1\> ... \e \<optionN\>] 
 *
 * @param param1 : description of parameter 1
 * @param paramN : description of parameter N
 *
 * \n
 * @opt
 * @optname option1 : description of option 1
 * @optname optionN : description of option N
 * 
 * \n
 * @details
 * OPTIONAL detailed description of the c main file follows here.
 * 
 * @usedfiles
 * OPTIONAL. If files are used, for each one, name, and usage description.
 * @filename fileName1 :  usage description of fileName1
 * @filename fileName2 :  usage description of fileName2
 *
 * \n
 * @env
 * OPTIONAL. If needed, environmental variables accessed by the program. For
 * each variable, name, and usage description, as below.
 * @envvar envVar1 :  usage description of envVar1
 * @envvar envVar2 :  usage description of envVar2
 * 
 * \n
 * @warning OPTIONAL. Warning if any (software requirements, ...)
 *
 * \n
 * @ex
 * OPTIONAL. Command example if needed
 * \n Brief example description.
 * @code
 * Insert your command example here
 * @endcode
 *
 * @sa OPTIONAL. See also section, in which you can refer other documented
 * entities. Doxygen will create the link automatically.
 * @sa modcppOPERATION.C
 * 
 * @bug OPTIONAL. Known bugs list if it exists.
 * @bug Bug 1 : bug 1 description
 *
 * @todo OPTIONAL. Things to forsee list.
 * @todo Action 1 : action 1 description
 * 
 */

public class ModjavaExample {
    /* A Class implementation comment can go here. */

    /**
     * Main.
     */
    public static void main(String[] args) {

        System.out.println("Complex addition:");

        Complex z1 = new Complex(1.0, 1.0);
        Complex z2 = new Complex(2.0, 2.0);
        Complex z3 = z1.add(z2);

        System.out.println(z1 + " + " + z2 + " = " + z3);
    }
}

/*___oOo___*/
