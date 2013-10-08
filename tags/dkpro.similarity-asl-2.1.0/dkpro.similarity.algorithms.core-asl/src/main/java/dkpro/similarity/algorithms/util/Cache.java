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
package dkpro.similarity.algorithms.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cache<K,V>
	extends LinkedHashMap<K,V>
{
	private static final long serialVersionUID = 6896122881488063028L;

	private final int capacity;

	public Cache(int capacity)
	{
		super(capacity + 1, 1.1f, true);
		this.capacity = capacity;
	}
	
	protected boolean removeEldestEntry(Map.Entry<K,V> eldest)
	{
		return size() > capacity;
	}
}