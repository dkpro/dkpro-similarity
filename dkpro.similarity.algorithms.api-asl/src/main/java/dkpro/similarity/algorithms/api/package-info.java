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
/**
 * This package includes the interfaces for similarity measures. We thereby allow
 * to compare terms, texts, or JCAS text representations.
 *
 * We define three categories of comparators:
 * <ul>
 * <li>{@code TermSimilarityMeasure} - comparison of two terms, e.g.
 *     <a href="http://portal.acm.org/citation.cfm?id=657297">Lin (1998)</a></li>
 * <li>{@code TextSimilarityMeasure} - comparison of two texts, e.g. LSA
 * 	   <a href="http://lsa.colorado.edu/papers/dp1.LSAintro.pdf">Landauer et. al (1998)</a></li>
 * <li>{@code JCasTextSimilarityMeasure} - comparison of two texts in JCas format</li>
 * </ul>
 * 
 * All comparators are case-sensitive.
 */
package dkpro.similarity.algorithms.api;
