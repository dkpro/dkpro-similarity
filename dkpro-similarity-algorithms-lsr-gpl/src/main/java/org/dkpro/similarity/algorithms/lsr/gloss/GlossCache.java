/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package org.dkpro.similarity.algorithms.lsr.gloss;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * A cache for LSR glosses.
 *
 * @author zesch
 *
 */
public class GlossCache extends LinkedHashMap<String,String> {

    private static final long serialVersionUID = 1L;

    private final int capacity;

    /**
     * Creates a cache with a certain capacity.
     * A capacity of 50 means that the most recent 50 glosses are cached.
     * The smaller the vectors, the more can be cached in a certain amount of RAM obviously :)
     * 
     * @param capacity
     */
    public GlossCache(int capacity) {
        super(capacity + 1, 1.1f, true);
        this.capacity = capacity;
    }
    
    /**
     * @param entityId The id of an entity.
     * 
     * @return The cached gloss for that entityId.
     */
    public Object getCachedGloss(String entityId) {
        return this.get(entityId);
    }

    /**
     * Puts the gloss in the cache.
     * Overwrites the old value, if it was already there.
     * 
     * @param entityId
     * @param gloss
     */
    public void putCachedVetor(String entityId, String gloss) {
        this.put(entityId, gloss);
    }
    

    @Override
    protected boolean removeEldestEntry(Entry<String,String> eldest) {
        return size() > capacity;
    }
}