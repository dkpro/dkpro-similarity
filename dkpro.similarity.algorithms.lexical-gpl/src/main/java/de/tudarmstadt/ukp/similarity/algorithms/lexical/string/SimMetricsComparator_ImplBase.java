/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 *******************************************************************************/
package de.tudarmstadt.ukp.similarity.algorithms.lexical.string;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;


/**
 * Abstract base class for all similarity measures that are based
 * on the SimMetrics library (Chapman, Norton, and Ciravegna, 2005).
 *
 * Chapman S., Norton B., and Ciravegna F. (2005). Armadillo: Integrating
 * Knowledge for the Semantic Web. In Proceedings of the Dagstuhl Seminar
 * in Machine Learning for the Semantic Web, Wadern, Germany.
 * <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.60.5185&rep=rep1&type=pdf">(pdf)</a>
 */
public abstract class SimMetricsComparator_ImplBase
	extends TextSimilarityMeasureBase
{
    
	protected AbstractStringMetric similarityMeasure;

    @Override
    public double getSimilarity(String s1, String s2)
        throws SimilarityException
    {
        return similarityMeasure.getSimilarity(s1, s2);
    }

    @Override
    public double getSimilarity(Collection<String> stringList1, Collection<String> stringList2)
        throws SimilarityException
    {
		if (stringList1.size() == 0 || stringList2.size() == 0) {
			return 0.0;
        }

        String concatenatedString1 = StringUtils.join(stringList1, " ");
        String concatenatedString2 = StringUtils.join(stringList2, " ");

        if (concatenatedString1.length() == 0 || concatenatedString2.length() == 0) {
            return 0.0;
        }

        double similarity = similarityMeasure.getSimilarity(concatenatedString1, concatenatedString2);

        return similarity;
    }
}