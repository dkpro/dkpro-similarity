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
 * Implements the distance measure by Resnik (1995)
 * res(c1,c2) = informationContent ( lowestCommonSubsumer(c1,c2) )
 * res(c1,c2) = ic ( lcs(c1,c2) )
 *
 * We use intrinisc IC instead of corpus-based IC.
 *
 * @author zesch
 *
 */
public class ResnikComparator
	extends PathBasedComparator
{
	public ResnikComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
	}

	public ResnikComparator(LexicalSemanticResource lexSemResource, Entity root)
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

            relatedness = getEntityGraph().getIntrinsicInformationContent(lcs);
            return relatedness;
        } catch (UnsupportedOperationException e) {
            throw new LexicalSemanticResourceException(e);
        }
    }
}