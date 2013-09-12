/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.star;

import fr.jmmc.jmcs.gui.util.SwingUtils;
import java.util.ArrayList;
import java.util.Observable;
import fr.jmmc.jmcs.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Store data relative to multiple stars.
 * @author Sylvain LAFRASSE.
 */
public class StarList extends Observable {

    /** Logger */
    private static final Logger _logger = LoggerFactory.getLogger(StarList.class.getName());
    /** List of stars */
    private final ArrayList<Star> _starList;

    public StarList() {
        _starList = new ArrayList<Star>();
    }

    /**
     * Fires the notification to the registered observers
     * @param notification notification enumeration value
     */
    public final synchronized void fireNotification(final Star.Notification notification) {
        // notify observers (swing components) within EDT :
        if (!SwingUtils.isEDT()) {
            _logger.error("Invalid thread : use EDT", new Throwable());
        }

        _logger.debug("Fire notification: {}", notification);

        notifyObservers(notification);
    }

    public final synchronized void add(final Star star) {
        _starList.add(star);
        setChanged();
    }

    public final Star get(final int index) {
        return _starList.get(index);
    }

    public final int size() {
        return _starList.size();
    }

    public final void clear() {
        _starList.clear();
    }

    @Override
    public String toString() {
        return CollectionUtils.toString(_starList);
    }
}
