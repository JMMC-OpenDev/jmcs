/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmcs.collection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fixed size linked hash map, automatically removing eldest entry.
 *
 * @author Sylvain LAFRASSE
 */
public class FixedSizeLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    private int _size = 0;

    public FixedSizeLinkedHashMap(int size) {
        super(size);
        _size = size;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > _size;
    }
}