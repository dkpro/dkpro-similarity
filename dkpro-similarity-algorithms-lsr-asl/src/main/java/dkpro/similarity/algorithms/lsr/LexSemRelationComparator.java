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
package dkpro.similarity.algorithms.lsr;

import java.util.Set;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource.LexicalRelation;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource.SemanticRelation;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.api.SimilarityException;

/**
 * Comparator that matches entities that are in direct lexical or semantic relation.
 * Returns 1.0 if they are in such a relation and 0.0 otherwise.
 *
 * @author zesch
 *
 */
public class LexSemRelationComparator extends LexSemResourceComparator {

//    private static final Logger logger = Logger.getLogger(LexSemRelationComparator.class);

//    private final String relationsString;
    private final Set<LexicalRelation> lexicalRelations;
    private final Set<SemanticRelation> semanticRelations;

    public final static double NO_MATCH = 0.0;
    public final static double MATCH    = 1.0;

    public LexSemRelationComparator(LexicalSemanticResource lexSemResource, Set<LexicalRelation> lexicalRelations, Set<SemanticRelation> semanticRelations) throws LexicalSemanticResourceException  {
        super(lexSemResource);
//        this.relationsString = StringUtils.join(new TreeSet<LexicalRelation>(lexicalRelations), "-") + "-" + StringUtils.join(new TreeSet<SemanticRelation>(semanticRelations), "-");
        this.lexicalRelations = lexicalRelations;
        this.semanticRelations = semanticRelations;
    }

    @Override
	public double getSimilarity(Set<Entity> entities1, Set<Entity> entities2) throws LexicalSemanticResourceException {
        return getDetailedRelatedness(entities1, entities2).relatedness;
    }

    @Override
    public double getSimilarity(Entity entity1, Entity entity2) throws SimilarityException, LexicalSemanticResourceException {
        LexSemRelValue lsrValue = getDetailedRelatedness(entity1, entity2);
        return lsrValue.getRelatedness();
    }

    /**
     * Only needed for very special purposes, when the type of the matching relation needs to be known.
     * @param entities1
     * @param entities2
     * @return
     * @throws LexicalSemanticResourceException
     */
    public LexSemRelValue getDetailedRelatedness(Set<Entity> entities1, Set<Entity> entities2) throws LexicalSemanticResourceException {
        if (entities1.size() == 0 || entities2.size() == 0) {
            return new LexSemRelValue(NO_MATCH, "");
        }

        for (Entity e1 : entities1) {
            for (Entity e2 : entities2) {
                LexSemRelValue lsrValue = getDetailedRelatedness(e1, e2);
                if (Math.abs(MATCH - lsrValue.relatedness) < 0.00001) {
                    return new LexSemRelValue(MATCH, lsrValue.relation);
                }
            }
        }

        return new LexSemRelValue(NO_MATCH, "");
    }

    public LexSemRelValue getDetailedRelatedness(Entity e1, Entity e2) throws LexicalSemanticResourceException {
        Set<String> lexemes1 = e1.getLexemes();
        Set<String> lexemes2 = e2.getLexemes();

        // return a match if e1 and e2 are connected by any of the relation types
        for (LexicalRelation lexicalRelation : lexicalRelations) {
            for (String lexeme : lexemes1) {

                Set<String> relatedLexemes;
                try {
                    relatedLexemes = getLexicalSemanticResource().getRelatedLexemes(lexeme, e1.getPos(), e1.getSense(lexeme), lexicalRelation);
                } catch (UnsupportedOperationException e) {
                    throw new LexicalSemanticResourceException(e);
                }
                for (String relatedLexeme : relatedLexemes) {
                    if (lexemes2.contains(relatedLexeme)) {
                        return new LexSemRelValue(MATCH, lexicalRelation.name());
                    }
                }
            }
            for (String lexeme : lexemes2) {
                Set<String> relatedLexemes;
                try {
                    relatedLexemes = getLexicalSemanticResource().getRelatedLexemes(lexeme, e2.getPos(), e2.getSense(lexeme), lexicalRelation);
                } catch (UnsupportedOperationException e) {
                    throw new LexicalSemanticResourceException(e);
                }
                for (String relatedLexeme : relatedLexemes) {
                    if (lexemes1.contains(relatedLexeme)) {
                        return new LexSemRelValue(MATCH, lexicalRelation.name());
                    }
                }
            }
        }

        for (SemanticRelation semanticRelation : semanticRelations) {
            try {
                Set<Entity> relatedEntites1 = getLexicalSemanticResource().getRelatedEntities(e1, semanticRelation);
                Set<Entity> relatedEntites2 = getLexicalSemanticResource().getRelatedEntities(e2, semanticRelation);

                if (getLexicalSemanticResource().getIsCaseSensitive()) {
                    for (Entity relatedEntity : relatedEntites1) {
                        if (relatedEntity.compareTo(e2) == 0) {
                            return new LexSemRelValue(MATCH, semanticRelation.name());
                        }
                    }

                    for (Entity relatedEntity : relatedEntites2) {
                        if (relatedEntity.compareTo(e1) == 0) {
                            return new LexSemRelValue(MATCH, semanticRelation.name());
                        }
                    }
                }
                else {
                    for (Entity relatedEntity : relatedEntites1) {
                        if (relatedEntity.compareToCaseInsensitive(e2) == 0) {
                            return new LexSemRelValue(MATCH, semanticRelation.name());
                        }
                    }

                    for (Entity relatedEntity : relatedEntites2) {
                        if (relatedEntity.compareToCaseInsensitive(e1) == 0) {
                            return new LexSemRelValue(MATCH, semanticRelation.name());
                        }
                    }
                }

            } catch (UnsupportedOperationException e) {
                throw new LexicalSemanticResourceException(e);
            }
        }

        return new LexSemRelValue(NO_MATCH, "");
    }

    @Override
    public String getName() {
        return getLexicalSemanticResource().getResourceName();
    }

    public class LexSemRelValue {
        private double relatedness;
        private String relation;

        public LexSemRelValue(double relatedness, String relation) {
            super();
            this.relatedness = relatedness;
            this.relation = relation;
        }

        public double getRelatedness() {
            return relatedness;
        }

        public void setRelatedness(double relatedness) {
            this.relatedness = relatedness;
        }

        public String getRelation() {
            return relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }
    }

}