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
package dkpro.similarity.algorithms.lsr.path;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Implements the relatedness measure by Leacock and Chodorow (1998)
 * lch(c1,c2) = - log(minPL(c1,c2) / 2 * depth) = log( 2*depth / minPL(c1,c2) )
 * minPathLength is measured in edges (original definition measures in nodes).
 *
 * If minPl is measured in nodes, then minPl(c,c) = 0.
 * This would cause logarithm error (or a division by zero)).
 * Hence, we change to formula to
 * lch(c1,c2) = - log( (minPL(c1,c2)+1) / 2 * depth)
 *
 * @author zesch
 *
 */
public class LeacockChodorowComparator
	extends PathBasedComparator
{
    private Double depth;

	public LeacockChodorowComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);

		try
		{
			this.depth = getEntityGraph().getDepth();

	        if (this.depth.equals(Double.NaN)) {
	            throw new LexicalSemanticResourceException("Invalid depth of the graph. Cannot compute LC98 relatedness.");
	        }
		}
		catch (UnsupportedOperationException e) {
			throw new LexicalSemanticResourceException(e);
		}
    }

	@Override
	public double getSimilarity(Entity e1, Entity e2)
		throws SimilarityException, LexicalSemanticResourceException
	{

        // shortcut for equal entities
        if (e1.equals(e2)) {
            return - Math.log(1.0 / (2*depth));
        }

        double minPathLength = getShortestPathLength(e1, e2);

        if (minPathLength < 0.0) {
            return minPathLength;
        }

        // lch(c1,c2) = - log( (minPL(c1,c2)+1) / 2 * depth)
        double relatedness = - Math.log( (minPathLength+1) / (2*depth));
        return relatedness;
    }
}