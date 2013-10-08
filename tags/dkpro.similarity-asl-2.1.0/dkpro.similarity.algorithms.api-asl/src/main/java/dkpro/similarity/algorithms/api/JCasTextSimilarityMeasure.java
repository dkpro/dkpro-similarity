/*******************************************************************************
 * Copyright 2013
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
package dkpro.similarity.algorithms.api;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;


/**
 * Similarity measure on two JCas text representations.
 */
public interface JCasTextSimilarityMeasure
	extends TextSimilarityMeasure
{
	/**
	 * Computes the similarity between two JCas text representations.
	 */
	double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException;

	/**
	 * Computes the similarity between two JCas text representations.
	 * Processing is limited to the two covering {@link Annotation}s within
	 * each JCas. 
	 */
    double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1, Annotation coveringAnnotation2)
    	throws SimilarityException;

}
