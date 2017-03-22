/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.util;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Weak cache for <K> items
 * @author bourgesl
 * @param <K> class type
 */
public abstract class GenericWeakCache<K> {

    /** Class logger */
    private static final Logger logger = LoggerFactory.getLogger(GenericWeakCache.class.getName());
    /** check that the item queue does not contain duplicates */
    private static final boolean DO_CHECKS = false;

    /* members */
    /** cache name */
    private final String name;
    /* flag to recycle elements having incorrect size */
    private final boolean recycleBadElementSize;
    /** weak reference on K item queue */
    private WeakReference<ArrayDeque<K>> recycled_items_queue = new WeakReference<ArrayDeque<K>>(null);

    protected GenericWeakCache(final String name) {
        this(name, false);
    }

    protected GenericWeakCache(final String name, final boolean recycleBadElementSize) {
        this.name = name;
        this.recycleBadElementSize = recycleBadElementSize;
    }

    /**
     * Get (dirty) item given the minimal lengths [N][M]
     * @param length N length
     * @param length2 M length
     * @return dirty item or null if none available
     */
    public synchronized K getItem(final int length, final int length2) {
        ArrayDeque<K> queue = recycled_items_queue.get();
        if (queue != null) {
            final int size = queue.size();
            if (logger.isDebugEnabled()) {
                logger.debug("getItem[{}]: traversing queue: {} elements", name, size);
            }
            for (int i = 0; i < size; i++) {
                K item = queue.poll();
                if (item != null) {
                    if (checkSizes(item, length, length2)) {
                        // return dirty item (not empty or zero-filled):
                        if (logger.isDebugEnabled()) {
                            logger.debug("getItem[{}]: reuse element [{}] @ {}", name, getSizes(item), item.hashCode());
                        }
                        return item;
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("getItem[{}]: bad element size [{}] vs [{} x {}] @ {}", name,
                                    getSizes(item), length, length2, item.hashCode());
                        }
                        if (recycleBadElementSize) {
                            // return bad item into the queue:
                            queue.offer(item);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Put the given item in the weak cache
     * @param item item to be recycled
     */
    public synchronized void putItem(final K item) {
        if (item != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("putItem[{}]: sizes[{}] @ {}", name, getSizes(item), item.hashCode());
            }
            ArrayDeque<K> queue = recycled_items_queue.get();
            if (queue == null) {
                queue = new ArrayDeque<K>(4);
                recycled_items_queue = new WeakReference<ArrayDeque<K>>(queue);
            }
            if (DO_CHECKS) {
                final Throwable stack = new Throwable();
                if (queue.contains(item)) {
                    logger.warn("duplicate items in weak cache[{}]", name, stack);
                    return;
                }
            }
            // dirty item (not empty or zero-filled):
            queue.offer(item);
            if (logger.isDebugEnabled()) {
                logger.debug("putItem[{}]: queue: {} elements", name, queue.size());
            }
        }
    }

    protected abstract boolean checkSizes(final K array, final int length, final int length2);

    public abstract String getSizes(final K array);
}
