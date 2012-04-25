/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.resource.image;

import fr.jmmc.jmcs.util.ImageUtils;
import javax.swing.ImageIcon;

/**
 *
 * @author Sylvain LAFRASSE
 */
public enum ResourceImage {

    STATUS_HISTORY("/fr/jmmc/jmcs/resource/script-start.png"), // http://www.iconseeker.com/search-icon/aspnet/script-start.html### by http://www.aspneticons.com/ (Creative Commons Attribution 3.0 License)
    HELP("/fr/jmmc/jmcs/resource/help.png"),
    JMMC_FAVICON("/fr/jmmc/jmcs/resource/favicon.png"),
    WARNING_ICON("/fr/jmmc/aspro/gui/icons/dialog-warning.png");
    /** the preferenced value identifying token */
    private final ImageIcon _icon;

    /**
     * Constructor
     * @param path the preferenced value identifying token
     */
    private ResourceImage(String path) {
        _icon = ImageUtils.loadResourceIcon(path);
    }

    /**
     * @return the resource image icon
     */
    public ImageIcon icon() {
        return _icon;
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
