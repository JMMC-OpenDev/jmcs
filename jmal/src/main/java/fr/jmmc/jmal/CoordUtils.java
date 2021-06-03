/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal;

import net.jafama.FastMath;

/**
 * Astronomical Coordinate utilities (distance ...)
 * @author bourgesl
 */
public final class CoordUtils {

    /**
     * Forbidden constructor : utility class
     */
    private CoordUtils() {
        super();
    }

    /**
     * Compute the distance between to ra/dec coordinates.
     *
     * @param raDeg1 first right acsension in degrees
     * @param decDeg1 first declinaison in degrees
     * @param raDeg2 second right acsension in degrees
     * @param decDeg2 second declinaison in degrees
     * @return distance in degrees
     */
    public static double computeDistanceInDegrees(final double raDeg1, final double decDeg1,
                                                  final double raDeg2, final double decDeg2) {
        
        if (Double.isNaN(raDeg1 + raDeg2 + decDeg1 + decDeg2)) {
            return Double.NaN;
        }

        /* Convert all the given angle from degrees to rad */
        final double ra1 = FastMath.toRadians(raDeg1);
        final double dec1 = FastMath.toRadians(decDeg1);

        final double ra2 = FastMath.toRadians(raDeg2);
        final double dec2 = FastMath.toRadians(decDeg2);

        /*
         * This implementation derives from Bob Chamberlain's contribution
         * to the comp.infosystems.gis FAQ; he cites
         * R.W.Sinnott, "Virtues of the Haversine", Sky and Telescope vol.68,
         * no.2, 1984, p159.
         */

        /* haversine formula: better precision than cosinus law */
        final double sd2 = FastMath.sin(0.5d * (dec2 - dec1));
        final double sr2 = FastMath.sin(0.5d * (ra2 - ra1));

        final double angle = sd2 * sd2 + sr2 * sr2 * FastMath.cos(dec1) * FastMath.cos(dec2);

        /* check angle ranges [0;1] */
        if (angle <= 0d) {
            return 0d;
        }
        if (angle < 1d) {
            return 2d * FastMath.toDegrees(FastMath.asin(Math.sqrt(angle)));
        }
        return 180d;
    }

}
