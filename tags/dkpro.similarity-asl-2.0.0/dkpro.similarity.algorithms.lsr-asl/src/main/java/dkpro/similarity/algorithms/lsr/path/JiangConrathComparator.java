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
 * Implements the distance measure by JiangConrath (1997)
 * jc(c1,c2) = IC(c1) + IC(c2) - 2* IC( lcs(c1,c2) )
 *
 * Attention: we convert it into a relatedness measure by using the formula
 * (2 - ( IC(c1) + IC(c2) - 2* IC(lcs(c1,c2)) )) / 2
 *
 * We then scale it to [0,1]
 *
 * We use intrinisc IC instead of corpus-based IC and transform it into a similarity measure:
 *
 * @author zesch
 *
 */
public class JiangConrathComparator
	extends PathBasedComparator
{
	public JiangConrathComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
    }

    /**
     * Initialize comparator with LSR and explicitely set root node.
     *
     * @param lexSemResource The lexical semantic resouce.
     * @param root The root node that should be used.
     * @throws LexicalSemanticResourceException
     */
	public JiangConrathComparator(LexicalSemanticResource lexSemResource, Entity root)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource, root);
    }


	@Override
	public double getSimilarity(Entity e1, Entity e2)
		throws SimilarityException, LexicalSemanticResourceException
	{
        // shortcut for equal entities
        if (e1.equals(e2)) {
            return 1.0;
        }

        double relatedness = NOT_FOUND;
        try {
            Entity lcs = getEntityGraph().getLCS(this.root, e1, e2);

            if (lcs == null) {
                return NO_LCS;
            }

            double iicLcs = getEntityGraph().getIntrinsicInformationContent(lcs);
            double iicE1  = getEntityGraph().getIntrinsicInformationContent(e1);
            double iicE2  = getEntityGraph().getIntrinsicInformationContent(e2);

            // (2 - ( IC(c1) + IC(c2) - 2* IC(lcs(c1,c2)) )) / 2
            relatedness = (2 - iicE1 - iicE2 + 2*iicLcs) / 2;
        } catch (UnsupportedOperationException e) {
            throw new SimilarityException(e);
        }

        return relatedness;
    }
}