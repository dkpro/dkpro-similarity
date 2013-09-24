package dkpro.similarity.algorithms.lsr.gloss;

import java.util.ArrayList;
import java.util.Arrays;
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
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;

/**
 * Implements the distance measure by Lesk (1986).
 * The distance between two terms a and b is defined as the number of shared tokens in the glosses of a and b.
 * We normalize this value to fall into (0,1).
 *
 * @author zesch
 *
 */
public class GlossOverlapComparator 
    extends LexSemResourceComparator
{
    private final Log logger = LogFactory.getLog(getClass());

    private boolean usePseudoGlosses;

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

            return NOT_FOUND;
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
        double similarity;

        try {
            String gloss1 = getGloss(e1);
            String gloss2 = getGloss(e2);

            if (gloss1 == null || gloss2 == null || gloss1.length() == 0 || gloss2.length() == 0) {
                return 0.0;
            }

            similarity = getOverlapCoefficient(gloss1, gloss2);

            if (new Double(similarity).equals(Double.NaN)) {
                return 0.0;
            }

            return similarity;
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
    
    private double getOverlapCoefficient(String s1, String s2) {
        
        Set<String> tokens1 = new HashSet<String>();
        Set<String> tokens2 = new HashSet<String>();
        
        tokens1.addAll(Arrays.asList(s1.split(" ")));
        tokens2.addAll(Arrays.asList(s2.split(" ")));
        
        int minSize = Math.min(tokens1.size(), tokens2.size());
        
        tokens1.retainAll(tokens2);
        
        int overlap = tokens1.size(); 
        
        return (double) overlap / minSize;
    }
}