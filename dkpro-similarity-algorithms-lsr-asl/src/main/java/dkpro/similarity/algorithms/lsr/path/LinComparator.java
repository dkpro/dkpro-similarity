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
 * Implements the similarity measure by Lin (1998)
 * lin(c1,c2) = 2 * ic ( lcs(c1,c2) ) / IC(c1) + IC(c2)
 *
 * @author zesch
 *
 */
public class LinComparator
	extends PathBasedComparator
{
	public LinComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
    }

	public LinComparator(LexicalSemanticResource lexSemResource, Entity root)
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
            double iicE1 = getEntityGraph().getIntrinsicInformationContent(e1);
            double iicE2 = getEntityGraph().getIntrinsicInformationContent(e2);

            if (iicE1 == 0 && iicE2 == 0) {
                return 0.0;
            }

            // 2 * ic ( lcs(c1,c2) ) / IC(c1) + IC(c2)
            relatedness = 2*iicLcs / (iicE1 + iicE2);
        } catch (UnsupportedOperationException e) {
            throw new LexicalSemanticResourceException(e);
        }

        return relatedness;
    }
}