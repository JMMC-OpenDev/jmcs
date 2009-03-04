package fr.jmmc.mcs.util;

import java.util.Observable;

/**
 * This class enable a third class to be considered as Observable.
 * To delegate feature, the class just has to forward addObserver(Observer o) to this class and call
 * notifyObservers methods. The Observer can get the original modified object using getSource method
 * on the casted observable field.
 *
 */
public class ObservableDelegate extends Observable
{
    Object _source;

    /**
     * Build a new delegate object to be observable.
     *
     * @param source the object to be observed;
     */
    public ObservableDelegate(Object source){
        super();
        _source = source;
    }

    public void notifyObservers()
    {
        setChanged();
        super.notifyObservers();
    }

    public void notifyObservers(Object arg)
    {
        setChanged();
        super.notifyObservers(arg);
    }

    /**
     * Return the notified source
     * @return the source
     */
    public Object getSource(){
        return _source;
    }
}
