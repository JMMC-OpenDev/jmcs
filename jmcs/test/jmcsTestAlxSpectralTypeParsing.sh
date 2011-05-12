#!/bin/bash
#*******************************************************************************
# JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
#*******************************************************************************

#/**
# @file
# brief description of the shell script, which ends at this dot.
#
# @synopsis
# \<Command Name\> [\e \<param1\> ... \e \<paramN\>] 
#                     [\e \<option1\> ... \e \<optionN\>] 
#
# @param param1 : description of parameter 1, if it exists
# @param paramN : description of parameter N, if it exists
#
# @opt
# @optname option1 : description of option 1, if it exists
# @optname optionN : description of option N, if it exists
# 
# @details
# OPTIONAL detailed description of the shell script follows here.
# 
# @usedfiles
# OPTIONAL. If files are used, for each one, name, and usage description.
# @filename fileName1 :  usage description of fileName1
# @filename fileName2 :  usage description of fileName2
#
# @env
# OPTIONAL. If needed, environmental variables accessed by the program. For
# each variable, name, and usage description, as below.
# @envvar envVar1 :  usage description of envVar1
# @envvar envVar2 :  usage description of envVar2
# 
# @warning OPTIONAL. Warning if any (software requirements, ...)
#
# @ex
# OPTIONAL. Command example if needed
# \n Brief example description.
# @code
# Insert your command example here
# @endcode
#
# @sa OPTIONAL. See also section, in which you can refer other documented
# entities. Doxygen will create the link automatically. For example, 
# @sa \<entity to refer\>
# 
# @bug OPTIONAL. Known bugs list if it exists.
# @bug Bug 1 : bug 1 description
#
# @todo OPTIONAL. Things to forsee list.
# @todo Action 1 : action 1 description
# 
# 
# */


# signal trap (if any)

for spectral_type in "-" "A0" "A0Ia" "A0Ib" "A0IV" "A0V" "A1III/IV" "A1V" "A2" "A2m" "A3" "A3IV" "A5" "A5V" "A7IV" "A7IV-V" "A8/A9V" "A8Vn" "A9V" "A9V..." "Am..." "Ap..." "B0IV..." "B1.5V" "B2" "B2III" "B2:IIIpshev" "B3IIIe" "B5" "B5III" "B5V" "B6III" "B7/B8V" "B8III" "B8V" "B8Vn" "B9" "B9.5IV:" "B9IIIMNp..." "B9IV" "F0" "F0IV..." "F2II/III" "F3Ia" "F5" "F5V" "F8" "G0" "G0Ib" "G0III..." "G0V" "G3Ib" "G3V" "G4Ibp..." "G5" "G5II..." "G5III" "G5IV" "G6/G8III" "G7III" "G8III" "G8IV/V" "K" "K0" "K0III" "K0IV" "K1Iabv" "K1III" "K1III/IV" "K1IIIvar" "K1/K2III" "K2" "K2III" "K2IIIvar" "K2IV" "K2/K3III" "K3Ib" "K3III" "K3IIvar" "K4III" "K4/K5III" "K5" "K5II" "K5III" "K5/M0III" "K7" "M0" "M0III" "M0I-M4Ia" "M1" "M1III" "M1IIIb" "M2Iabpe" "M3" "M3III" "M3/M4III" "M4.5IIIa" "M4III" "M4III:" "M5III" "M5/M6IV" "M6" "M6e-M7" "M6III" "M7III" "M8III:e" "Ma" "Mb" "Mc" "Md" "O" "O..." "O7" "A comp SB" "A0III SB" "A0V SB" "A3IVv SB" "A5Vv SB" "B2III SB" "B2IIIv SB" "B3V SB" "B5III SB" "B5V SB" "B6pv SB" "B8III SB" "B9p SB" "F2IV SB" "F4III SB" "F8V SB" "G2V SB" "G3Ibv SB" "G5III SB" "G8II SB" "G8III compSB" "G8III SB" "G8III-IV SB" "G8V SB" "K0III SB" "K0IV SB" "K0V SB" "K1III SB" "K3III SB" "K4II SB" "K5Ibv SB" "K5III SB" "M0III SB" "O9.5Ib SB"
do
    echo -n "$spectral_type contains "
    ALX spectralTypes "$spectral_type"
done

#___oOo___
