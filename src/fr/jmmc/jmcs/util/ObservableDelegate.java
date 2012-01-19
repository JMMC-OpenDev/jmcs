/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.util;

import java.util.Observable;

/**
 * This class enable a third class to be considered as Observable.
 * To delegate feature, the class just has to forward addObserver(Observer o) to this class and call
 * notifyObservers methods. The Observer can get the original modified object using getSource method
 * on the casted observable field.
 * 
 * @author Guillaume MELLA.
 */
public class ObservableDelegate extends Observable {

    /** object to be observed */
    private final Object _source;

    /**
     * Build a new delegate object to be observable.
     *
     * @param source the object to be observed;
     */
    public ObservableDelegate(final Object source) {
        super();
        _source = source;
    }

    /**
     * Notify observers without argument
     */
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }

    /**
     * Notify observers with the given argument
     *
     * @param arg optional argument
     */
    @Override
    public void notifyObservers(final Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }

    /**
     * Return the notified source
     * @return the source
     */
    public Object getSource() {
        return _source;
    }
}
