/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

/**
 * Enumeration of standard ranges arround well-known absorption lines
 */
public enum AbsorptionLineRange {
    HeI_2_058("HeI 2.058", 2.038, 2.078),
    FeII_2_089("FeII 2.089", 2.074, 2.104), // Warm and dense CSE https://arxiv.org/pdf/1408.6658.pdf sec 3.2.1
    HeI_2_112("HeI 2.112", 2.0926, 2.1326),
    NIII_2_115("NIII 2.115", 2.1155 - 0.02, 2.1155 + 0.02), // http://adsabs.harvard.edu/abs/1996ApJS..107..281H
    MgII_2_140("MgII 2.140", 2.130, 2.150), // doublet
    Brg_2_166("Brg 2.166", 2.136, 2.196),
    HeII_2_188("HeII 2.188", 2.1885 - 0.02, 2.1885 + 0.02), // http://adsabs.harvard.edu/abs/1996ApJS..107..281H
    NaI_2_206("NaI 2.206", 2.198, 2.218),
    NIII_2_249("NIII 2.249", 2.237, 2.261),
    CO_bands("CO bands", 2.28, Double.NaN);
    /*    
    # -- spectral line database
    # -- see also https://arxiv.org/pdf/astro-ph/0008213.pdf, table 1
    lines = {#'HeI-II':[(2.058, 2.112, 2.1623, 2.166, 2.189), 'c'],
             #'H2':((2.1218, 2.2235), 'm'),
             #'MgII':((2.13831, 2.14359), '0.5'),
             #r'Br$\gamma$':[2.1661, 'b'],
             #'NIII':[(2.247, 2.251), (0,1,0.5)],
             #'FeI-II':((2.0635, 2.0846, 2.2263, 2.2389, 2.2479, 2.2626), 'g'),
             #r'$^{12}$C$^{16}$O HB':([2.2935, 2.3227, 2.3535, 2.3829,2.4142], 'r'),
             #r'$^{13}$C$^{16}$O HB':([2.3448, 2.3739, 2.4037, 2.4341,2.4971], 'orange'),
             #'AlI':((2.109884,2.116958,2.270729),'b'),
             #'MgI':((2.121393,2.146472,2.386573),'r'),
             #'NaI':[(2.206242, 2.208969), 'y'],
             #'ScI':((2.20581,2.20714),'m'),
             #'SiI':((2.206873),'g')
             #'CaI':((1.89282, 1.94508,  1.98702, 1.99318, 2.261410,2.263110,2.265741),'g')
             }
     */
    // members:
    private final String name;
    private final double min;
    private final double max;

    AbsorptionLineRange(final String name, final double min, final double max) {
        this.name = name;
        this.min = min;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public String toString() {
        return "AbsorptionLineRange{" + "name=" + name + ", min=" + min + ", max=" + max + '}';
    }

}
