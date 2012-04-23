/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.resource.image;

import fr.jmmc.jmcs.util.ImageUtils;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author Sylvain LAFRASSE
 */
public enum ResourceImage {

    STATUS_HISTORY("/fr/jmmc/jmcs/resource/script-start.png");
    /** the preferenced value identifying token */
    private final String _path;

    /**
     * Constructor
     * @param path the preferenced value identifying token
     */
    ResourceImage(String path) {
        _path = path;
    }

    /**
     * @return the preferenced value identifying token
     */
    public String path() {
        return _path;
    }

    /**
     * For unit testing purpose only.
     * @param args
     */
    public static void main(String[] args) {
        for (ResourceImage rsc : ResourceImage.values()) {
            System.out.println("Resource '" + rsc.name() + "' = ['" + rsc + "'].");
        }
    }
}
