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
package dkpro.similarity.algorithms.wikipedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.wikipedia.measures.WikiLinkComparator;

/**
 * Wraps the Wikipedia Link Measure from David Milne. Implemented in JWPL, as it is a Wikipedia
 * specific measure.
 *
 * Explained in detail in: Computing Semantic Relatedness using Wikipedia Link Structure. Proc. of
 * the New Zealand Computer Science Research Student Conference (NZCSRSC'2007), Hamilton, New
 * Zealand.
 *
 * @author zesch
 */
public class WLMComparator
	extends TextSimilarityMeasureBase
{
	private final WikiLinkComparator wikiLinkComparator;

	public WLMComparator(Wikipedia wiki)
		throws SimilarityException
	{
		this(wiki, true, false);
	}

	public WLMComparator(Wikipedia wiki, boolean useCache, boolean useOutboundLinks)
	{
		super();

		wikiLinkComparator = new WikiLinkComparator(wiki, useCache, useOutboundLinks);
		// do not use the JWPL database cache
		wikiLinkComparator.setUseCache(false);
	}

	@Override
	public String getName()
	{
		return this.getClass().getName();
	}

	@Override
	public double getSimilarity(String term1, String term2)
		throws SimilarityException
	{
		return this.wikiLinkComparator.getSimilarity(term1, term2);
	}

	@Override
	public double getSimilarity(Collection<String> terms1, Collection<String> terms2)
		throws SimilarityException
	{
		List<Double> relatednessValues = new ArrayList<Double>();
		for (String t1 : terms1) {
			for (String t2 : terms2) {
				relatednessValues.add(getSimilarity(t1, t2));
			}
		}

		// return the best relatedness between all term pairs
		// WARNING: do not just use Collections.max(), as some of these measures are distance rather
		// than relatedness measure. Always use the build in method.
		try {
			return wikiLinkComparator.combineRelatedness(relatednessValues);
		}
		catch (WikiRelatednessException e) {
			throw new SimilarityException(e);
		}
	}
}