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
import de.tudarmstadt.ukp.dkpro.lexsemresource.graph.EntityGraph.DirectionMode;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Implements the relatedness measure by Wu Palmer et al. (1994).
 * wp(c1,c2) = 2 * depth(lcs(c1,c2)) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
 * pl() is measured in nodes.
 *
 * @author zesch
 *
 */
public class WuPalmerComparator
	extends PathBasedComparator
{
	public WuPalmerComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
	}

	public WuPalmerComparator(LexicalSemanticResource lexSemResource, Entity root)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource, root);
	}

    @Override
	public double getSimilarity(Entity e1, Entity e2)
		throws SimilarityException, LexicalSemanticResourceException
	{
        // entity overrides the equals method with a usable variant
        if (e1.equals(e2)) {
            return 1.0;
        }

        // get the lowest common subsumer (lcs) of the two entities
        Entity lcs = null;
        try {
            lcs = getEntityGraph().getLCS(root, e1, e2);
        } catch (UnsupportedOperationException e) {
            throw new LexicalSemanticResourceException(e);
        }

        if (lcs == null) {
            return NO_PATH;
        }

        double depthLCS = getEntityGraph().getShortestPathLength(root, lcs, DirectionMode.undirected);
        double pl1      = getEntityGraph().getShortestPathLength(e1, lcs, DirectionMode.undirected) + 1;
        double pl2      = getEntityGraph().getShortestPathLength(e2, lcs, DirectionMode.undirected) + 1;

        // wp(c1,c2) = 2 * depth(lcs(c1,c2)) / ( pl(c1,lcs(c1,c2)) + pl(c2,lcs(c1,c2)) + 2 * depth(lcs(c1,c2)) )
        double nominator   = 2 * depthLCS;
        double denominator = pl1 + pl2 + 2 * depthLCS;
        double relatedness = nominator / denominator;

        return relatedness;
    }
}