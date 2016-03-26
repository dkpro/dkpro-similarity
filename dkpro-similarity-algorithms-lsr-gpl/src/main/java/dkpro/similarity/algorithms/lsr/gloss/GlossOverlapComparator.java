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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource.LexicalRelation;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource.SemanticRelation;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.api.SimilarityException;


/**
 * Implements the distance measure by Lesk (1986).
 * The distance between two terms a and b is defined as the number of shared tokens in the glosses of a and b.
 * We normalize this value to fall into (0,1).
 *
 * @author zesch
 *
 */
public class GlossOverlapComparator
	extends GlossBasedComparator
{
	private final Log logger = LogFactory.getLog(getClass());

    private final boolean usePseudoGlosses;

    Set<LexicalRelation> lexicalRelations;
    Map<SemanticRelation,Integer> semanticRelations;

    public GlossOverlapComparator(LexicalSemanticResource lexSemResource, boolean usePseudoGlosses) throws LexicalSemanticResourceException {
        super(lexSemResource);
        this.usePseudoGlosses = usePseudoGlosses;

        this.lexicalRelations = new HashSet<LexicalRelation>();
        this.semanticRelations = new HashMap<SemanticRelation,Integer>();

        lexicalRelations.add(LexicalRelation.antonymy);
        lexicalRelations.add(LexicalRelation.synonymy);

        semanticRelations.put(SemanticRelation.holonymy, 1);
        semanticRelations.put(SemanticRelation.meronymy, 1);
        semanticRelations.put(SemanticRelation.hypernymy, 1);
        semanticRelations.put(SemanticRelation.hyponymy, 1);
        semanticRelations.put(SemanticRelation.other, 1);

    }

	@Override
	public double getSimilarity(Set<Entity> entities1, Set<Entity> entities2)
		throws LexicalSemanticResourceException, SimilarityException
	{
		if (entities1.size() == 0 || entities2.size() == 0) {
            if (entities1.size() == 0) {
                logger.debug("First entity set is empty.");
            }
            else {
                logger.debug("Second entity set is empty.");
            }

            return GlossBasedComparator.NOT_FOUND;
        }


        List<Double> relatednessValues = new ArrayList<Double>();
        for (Entity e1 : entities1) {
            for (Entity e2 : entities2) {
                relatednessValues.add( getSimilarity(e1, e2) );
            }
        }

        return Collections.max(relatednessValues);

    }

	@Override
	public double getSimilarity(Entity e1, Entity e2)
		throws LexicalSemanticResourceException, SimilarityException
	{
		double relatedness;

        try {
            String gloss1 = getGloss(e1);
            String gloss2 = getGloss(e2);

            if (gloss1 == null || gloss2 == null || gloss1.length() == 0 || gloss2.length() == 0) {
                return 0.0;
            }

            relatedness = overlapCoefficientComparator.getSimilarity(gloss1, gloss2);

            if (new Double(relatedness).equals(Double.NaN)) {
                return 0.0;
            }

            return relatedness;
        } catch (UnsupportedOperationException e) {
            throw new LexicalSemanticResourceException(e);
        }
    }

    @Override
	public String getName()
	{
		String gloss_suffix = "";
        if (usePseudoGlosses) {
            gloss_suffix = "pseudo";
        }
        else {
            gloss_suffix = "normal";
        }
        return this.getClass().getSimpleName() + "-" + gloss_suffix;
    }

	private String getGloss(Entity e)
		throws LexicalSemanticResourceException
	{
        String gloss;

        if (usePseudoGlosses) {
            gloss = getLexicalSemanticResource().getPseudoGloss(e, lexicalRelations, semanticRelations);
        }
        else {
            gloss = getLexicalSemanticResource().getGloss(e);
        }

        return gloss;
    }
}
