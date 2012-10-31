/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fixed size linked hash map, automatically removing eldest entry.
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author Sylvain LAFRASSE
 */
public final class FixedSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    /** default serial UID for Serializable interface */
    private static final long serialVersionUID = 1;
    /** maximum map size */
    private final int _maxSize;

    /**
     * Public constructor 
     * @param maxSize maximum map size
     */
    public FixedSizeLinkedHashMap(final int maxSize) {
        super(maxSize);
        _maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return size() > _maxSize;
    }
}