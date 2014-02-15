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

import de.tudarmstadt.ukp.wikipedia.api.CategoryGraph;
import de.tudarmstadt.ukp.wikipedia.api.CategoryGraphManager;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiRelatednessException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.wikipedia.measures.JiangConrathBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeacockChodorowBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeskFirstComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LeskFullComparator;
import dkpro.similarity.algorithms.wikipedia.measures.LinBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.PathLengthBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.ResnikBestComparator;
import dkpro.similarity.algorithms.wikipedia.measures.WikipediaSimilarityMeasureBase;
import dkpro.similarity.algorithms.wikipedia.measures.WuPalmerBestComparator;


/**
 * WikipediaBasedComparators do not use the LSR interface, but are only LSR compatible wrappers for the measures implemented in JWPL.
 * Those measures cannot easily be implemented in LSR, because they use a very complex combination of the Wikipedia category and article graph.
 * In LSR, we either have the category graph or the article graph as resources but not both.
 *
 * @author zesch
 *
 */
public class WikipediaBasedComparator
	extends TextSimilarityMeasureBase
{
	public static final double NO_PATH = -2.0;

	public enum WikipediaBasedRelatednessMeasure {
        JiangConrath,
        LeacockChodorow,
        LeskFirst,
        LeskFull,
        Lin,
        PathLength,
        Resnik,
        WuPalmer
    }

    private WikipediaSimilarityMeasureBase wikipediaComparator;
    private WikipediaBasedRelatednessMeasure measure;


    public WikipediaBasedComparator(Wikipedia wikipedia, WikipediaBasedRelatednessMeasure measure)
    	throws SimilarityException
    {
    	this(wikipedia, measure, false);
    }

	public WikipediaBasedComparator(Wikipedia wikipedia, WikipediaBasedRelatednessMeasure measure, boolean useCache)
		throws SimilarityException
	{
		super();

		this.measure = measure;

		try {
            // do not create a new graph, but use the method provided by the CategoryGraphManager
            // this ensures that multiple WikipediaBasedComparators can share a category graph
            CategoryGraph catGraph = CategoryGraphManager.getCategoryGraph(wikipedia);

            if (measure.equals(WikipediaBasedRelatednessMeasure.JiangConrath)) {
                this.wikipediaComparator = new JiangConrathBestComparator(wikipedia, catGraph);
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.LeacockChodorow)) {
                this.wikipediaComparator = new LeacockChodorowBestComparator(wikipedia, catGraph);
                this.measure = WikipediaBasedRelatednessMeasure.LeacockChodorow;
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.LeskFirst)) {
                this.wikipediaComparator = new LeskFirstComparator(wikipedia, catGraph);
                this.measure = WikipediaBasedRelatednessMeasure.LeskFirst;
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.LeskFull)) {
                this.wikipediaComparator = new LeskFullComparator(wikipedia, catGraph);
                this.measure = WikipediaBasedRelatednessMeasure.LeskFull;
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.Lin)) {
                this.wikipediaComparator = new LinBestComparator(wikipedia, catGraph);
                this.measure = WikipediaBasedRelatednessMeasure.Lin;
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.PathLength)) {
                this.wikipediaComparator = new PathLengthBestComparator(wikipedia, catGraph);
                this.measure = WikipediaBasedRelatednessMeasure.PathLength;
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.Resnik)) {
                this.wikipediaComparator = new ResnikBestComparator(wikipedia, catGraph);
            }
            else if (measure.equals(WikipediaBasedRelatednessMeasure.WuPalmer)) {
                this.wikipediaComparator = new WuPalmerBestComparator(wikipedia, catGraph);
            }
            else {
                throw new SimilarityException(new Throwable("Unknown measure type: " + measure));
            }

            this.wikipediaComparator.setUseCache(useCache);
		}
		catch (WikiApiException e) {
			throw new SimilarityException(e);
		}
		catch (WikiRelatednessException e) {
			throw new SimilarityException(e);
		}
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName() + "-" + measure.name();
    }

    @Override
	public double getSimilarity(String term1, String term2)
		throws SimilarityException
	{
        return this.wikipediaComparator.getSimilarity(term1, term2);
    }

    @Override
	public double getSimilarity(Collection<String> terms1, Collection<String> terms2)
		throws SimilarityException
	{
		List<Double> relatednessValues = new ArrayList<Double>();
        for (String t1 : terms1) {
            for (String t2 : terms2) {
                relatednessValues.add( getSimilarity(t1, t2) );
            }
        }

        // return the best relatedness between all term pairs
        // WARNING: do not just use Collections.max(), as some of these measures are distance rather than relatedness measure. Always use the build in method.
        try {
            return this.wikipediaComparator.combineRelatedness(relatednessValues);
        } catch (WikiRelatednessException e) {
            throw new SimilarityException(e);
        }
    }

	protected double getBestRelatedness(List<Double> values)
		throws SimilarityException
	{
		try {
            return this.wikipediaComparator.combineRelatedness(values);
        } catch (WikiRelatednessException e) {
            throw new SimilarityException(e);
        }
    }
}