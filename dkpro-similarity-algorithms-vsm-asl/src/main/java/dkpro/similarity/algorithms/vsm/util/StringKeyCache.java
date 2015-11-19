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
package dkpro.similarity.algorithms.vsm.util;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * Custom EsaVectorCache, since the general EsaVectorCache distorts results. See Bug 148.
 *
 * @author Richard Eckart de Castilho
 */
public class StringKeyCache<T>
	extends LinkedHashMap<String, T>
{
	private static final long serialVersionUID = -4879961662785431162L;
	private int capacity;

	private long hits = 0;
	private long misses = 0;
	private long swaps = 0;

	public StringKeyCache(int aCapacity)
	{
		super(aCapacity + 1, 1.1f, true);
		capacity = aCapacity;
	}

	@Override
	public T put(String aKey, T aValue)
	{
		// System.out.println("Add: ["+aKey+"] "+size());
		return super.put(aKey.intern(), aValue);
	}

	@Override
	public T get(Object aKey)
	{
		T value = super.get(((String) aKey).intern());
		// T value = super.get(aKey);
		if (value != null) {
			// System.out.println("Hit: ["+aKey+"]");
			hits++;
		}
		else {
			// System.out.println("Miss: ["+aKey+"]");
			misses++;
		}
		return value;
	}

	@Override
	protected boolean removeEldestEntry(Entry<String, T> eldest)
	{
		if (size() > capacity) {
			swaps++;
			return true;
		}
		else {
			return false;
		}
	}

	public long getHits()
	{
		return hits;
	}

	public long getMisses()
	{
		return misses;
	}

	public long getSwaps()
	{
		return swaps;
	}

	public int getCapacity()
	{
		return capacity;
	}

	@Override
	public String toString()
	{
		return "Hits: " + hits + " Misses: " + misses + " Swaps: " + swaps + " Size: " + size()
				+ " Capacity: " + capacity;
	}
}
