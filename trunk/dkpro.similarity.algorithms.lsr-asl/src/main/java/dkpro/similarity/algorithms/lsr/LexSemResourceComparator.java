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
package dkpro.similarity.algorithms.lsr;

import static java.util.Collections.singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity.PoS;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public abstract class LexSemResourceComparator
	extends TextSimilarityMeasureBase
{
	private final Log log = LogFactory.getLog(getClass());

	protected final static double NOT_RELATED = -1.0;

	private LexicalSemanticResource lexSemResource;
	private Map<String, Set<Entity>> cache;
	private PoS defaultPos;

	public LexSemResourceComparator(LexicalSemanticResource aLexSemResource)
		throws LexicalSemanticResourceException
	{
		super();
		setLexSemResource(aLexSemResource);
	}

	public void setLexSemResource(LexicalSemanticResource aLexSemResource)
	{
		lexSemResource = aLexSemResource;
	}

	public LexicalSemanticResource getLexicalSemanticResource()
	{
		return lexSemResource;
	}

	public Map<String, Set<Entity>> getCache()
	{
		return cache;
	}

	public void setCache(Map<String, Set<Entity>> aCache)
	{
		cache = aCache;
	}

	public void setDefaultPos(PoS aDefaultPos)
	{
		defaultPos = aDefaultPos;
	}

	public PoS getDefaultPos()
	{
		return defaultPos;
	}

	public abstract double getSimilarity(Entity entity1, Entity entity2)
		throws SimilarityException, LexicalSemanticResourceException;

	public abstract double getSimilarity(Set<Entity> entities1, Set<Entity> entities2)
		throws SimilarityException, LexicalSemanticResourceException;

	@Override
	public final double getSimilarity(String aTerm1, String aTerm2)
		throws SimilarityException
	{
		return getSimilarity(singleton(aTerm1), singleton(aTerm2));
	}

	@Override
	public final double getSimilarity(Collection<String> aTerms1, Collection<String> aTerms2)
		throws SimilarityException
	{
		return getSimilarity(aTerms1, aTerms2, defaultPos);
	}

	public double getSimilarity(Collection<String> aTerms1, Collection<String> aTerms2, PoS aPos)
		throws SimilarityException
	{
		try {
			Collection<String> set1 = aTerms1.size() == 1 ? aTerms1 : new TreeSet<String>(aTerms1);
			Collection<String> set2 = aTerms2.size() == 1 ? aTerms2 : new TreeSet<String>(aTerms2);

			List<Double> scores = new ArrayList<Double>();
			for (String t1 : set1) {
				Set<Entity> entities1 = getEntities(t1, aPos);
				if (entities1.size() == 0) {
					continue;
				}

				for (String t2 : set2) {
					Set<Entity> entities2 = getEntities(t2, aPos);
					if (entities2.size() == 0) {
						continue;
					}

					scores.add(getSimilarity(entities1, entities2));
				}
			}

			if (log.isDebugEnabled()) {
				log.debug(aTerms1 + " : " + aTerms2 + " : " + getBestRelatedness(scores) + " : "
						+ scores);
			}

			return getBestRelatedness(scores);
		}
		catch (LexicalSemanticResourceException e) {
			throw new SimilarityException(e);
		}
	}

	protected Set<Entity> getEntities(String aTerm, PoS aPos)
		throws LexicalSemanticResourceException
	{
		if (cache == null) {
			if (aPos != null) {
				return lexSemResource.getEntity(aTerm, aPos);
			}
			else {
				return lexSemResource.getEntity(aTerm);
			}
		}
		else {
			String key = (aPos != null ? aPos.toString() : "*") + aTerm;
			Set<Entity> entities = cache.get(key);
			if (entities == null) {
				if (aPos != null) {
					entities = lexSemResource.getEntity(aTerm, aPos);
				}
				else {
					entities = lexSemResource.getEntity(aTerm);
				}
				cache.put(key, entities);
			}
			return entities;
		}
	}

	@Override
	public void beginMassOperation()
	{
		cache = new HashMap<String, Set<Entity>>();
	}

	@Override
	public void endMassOperation()
	{
		cache = null;
	}

	public LexicalSemanticResource getLexSemResource()
	{
		return lexSemResource;
	}

	protected double getBestRelatedness(List<Double> relatednessValues)
		throws SimilarityException
	{
		if (relatednessValues.size() == 0) {
			return NOT_FOUND;
		}

		List<Double> scores = new ArrayList<Double>();
		for (double d : relatednessValues) {
			if (d >= 0.0) {
				scores.add(d);
			}
		}

		if (scores.size() == 0) {
			scores.add(NOT_FOUND);
		}

		return isDistanceMeasure() ? Collections.min(scores) : Collections.max(scores);
	}

}
