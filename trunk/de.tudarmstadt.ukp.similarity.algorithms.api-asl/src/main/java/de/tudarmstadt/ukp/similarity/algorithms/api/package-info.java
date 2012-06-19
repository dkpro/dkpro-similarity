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
/**
 * <p>
 * JLRM is a framework for comparing strings, terms or texts. Often, the comparators will involve
 * some external knowledge source such as an ontology, an traditional index (e.g. Lucene) or a
 * semantic index (e.g. an LSA model or ESA model).
 * <p>
 * We define two categories of comparators:
 * <ul>
 * <li>{@link TermSimilarityMeasure} - comparison of two terms, e.g.
 *     [<a href="http://portal.acm.org/citation.cfm?id=657297">Lin, 1998</a>]</li>
 * <li>{@link TextSimilarityMeasure} - comparison of two lists of terms, e.g. LSA
 * 	   [<a href="http://lsa.colorado.edu/papers/dp1.LSAintro.pdf">Landauer et. al, 1998</a>]</li>
 * </ul>
 * <p>
 * All comparators are case-sensitive.
 * <p>
 * Term-based measures can be used for texts by wrapping them in an {@link AggregatorBase aggregator}.
 * There are several aggregator implementations available. Score aggregation is a slow process (O(n^2))
 * because it requires to calculate the pair-wise similarity between each term in the two term lists.
 */
package de.tudarmstadt.ukp.similarity.algorithms.api;
