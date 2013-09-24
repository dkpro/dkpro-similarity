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
package dkpro.similarity.algorithms.structure;


/**
 * This measure corresponds to the Composite Feature "Ordering" as described in:
 * 
 * ﻿Hatzivassiloglou, V., Klavans, J., & Eskin, E. (1999)
 * Detecting text similarity over short passages: Exploring linguistic feature
 * combinations via machine learning. Proceedings of the Joint SIGDAT Conference
 * on Empirical Methods in Natural Language Processing and Very Large Corpora. 
 *
 * Two feature vector of distances are computed, one for each text. Then they
 * are compared using Pearson correlation. The correlation score is returned
 * as similarity score. 
 */

public class TokenPairOrderingMeasure
	extends TokenPairDistanceMeasure
{
	@Override
	public int transform(int diff)
	{
		return (diff < 0) ? -1 : 1; 
	}
}
