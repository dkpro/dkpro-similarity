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
package dkpro.similarity.algorithms.lsr.gloss;

import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexical.string.OverlapCoefficientSimMetricComparator;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;


public abstract class GlossBasedComparator
	extends LexSemResourceComparator
{
    public static final double NOT_FOUND = -1.0;
    
    protected TextSimilarityMeasure overlapCoefficientComparator;
    
	public GlossBasedComparator(LexicalSemanticResource lsr)
		throws LexicalSemanticResourceException
	{
		super(lsr);

        overlapCoefficientComparator = new OverlapCoefficientSimMetricComparator();
    }
}