/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.sspace;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.vsm.VectorComparator;
import dkpro.similarity.algorithms.vsm.store.CachingVectorReader;
import edu.ucla.sspace.common.SemanticSpace;
import edu.ucla.sspace.common.SemanticSpaceIO;

public class LsaSimilarityMeasure 
	extends TextSimilarityMeasureBase
{
	
	private TextSimilarityMeasure measure;
	
	private int cacheSize;
	
	public LsaSimilarityMeasure(File modelFile) throws IOException {
		cacheSize = 100;
		
		SemanticSpace sspace = SemanticSpaceIO.load(modelFile); 

		measure = new VectorComparator(new CachingVectorReader(
                new SSpaceVectorReader(sspace), cacheSize));
	}

	@Override
	public double getSimilarity(Collection<String> strings1,
			Collection<String> strings2) throws SimilarityException {
		return measure.getSimilarity(strings1, strings2);
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}
}