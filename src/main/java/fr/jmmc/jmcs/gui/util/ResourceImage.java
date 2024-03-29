/*******************************************************************************
 *                 jMCS project ( http://www.jmmc.fr/dev/jmcs )
 *******************************************************************************
 * Copyright (c) 2013, CNRS. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     - Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     - Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     - Neither the name of the CNRS nor the names of its contributors may be
 *       used to endorse or promote products derived from this software without
 *       specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL CNRS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package fr.jmmc.jmcs.gui.util;

import fr.jmmc.jmcs.util.ImageUtils;
import javax.swing.ImageIcon;

/**
 * jMCS internal resource images.
 * @author Sylvain LAFRASSE
 */
public enum ResourceImage {

    JMMC_FAVICON("jmmc_favicon.png"),
    STATUS_HISTORY("script_start.png"), // http://www.iconseeker.com/search-icon/aspnet/script-start.html### by http://www.aspneticons.com/ (Creative Commons Attribution 3.0 License)
    HELP_ICON("help_icon.png"),
    DISABLED_INFO_ICON("disabled_info_icon.png"),
    INFO_ICON("info_icon.png"),
    WARNING_ICON("warning_icon.png"),
    ERROR_ICON("error-icon.png"),
    UP_ARROW("up_arrow.png"),
    DOWN_ARROW("down_arrow.png"),
    OK_MARK("ok-mark.png"),
    KO_MARK("x-mark.png"),
    LIST_ADD("list-add.png"),
    LIST_DEL("list-remove.png"),
    REFRESH_ICON("refresh.png");
    /** Common resource directory containing icon files */
    private final static String IMAGE_RESOURCE_COMMON_PATH = "fr/jmmc/jmcs/resource/image/";

    /* members */
    private final String fileName;
    /** Loaded icon resource */
    private ImageIcon icon = null;

    /** 
     * Constructor
     * @param fileName
     */
    private ResourceImage(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the resource image icon
     */
    public ImageIcon icon() {
        if (icon == null) {
            loadImage();
        }
        return icon;
    }

    private synchronized void loadImage() {
        if (icon == null) {
            icon = ImageUtils.loadResourceIcon(IMAGE_RESOURCE_COMMON_PATH + fileName);
        }
    }

    /**
     * For unit testing purpose only.
     * @param args CLI options and parameters
     */
    public static void main(String[] args) {
        for (ResourceImage rsc : ResourceImage.values()) {
            System.out.println("Resource '" + rsc.name() + "' -> '" + rsc.icon()
                    + "': (" + rsc.icon().getIconWidth() + " x " + rsc.icon().getIconHeight() + ").");
        }
    }
}
