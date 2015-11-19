/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.algorithms.lsr.gloss;

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
    

    protected boolean removeEldestEntry(Entry<String,String> eldest) {
        return size() > capacity;
    }
}