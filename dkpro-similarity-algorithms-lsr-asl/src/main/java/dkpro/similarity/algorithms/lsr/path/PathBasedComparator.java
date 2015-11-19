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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.dkpro.lexsemresource.Entity;
import de.tudarmstadt.ukp.dkpro.lexsemresource.LexicalSemanticResource;
import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import de.tudarmstadt.ukp.dkpro.lexsemresource.graph.EntityGraph;
import de.tudarmstadt.ukp.dkpro.lexsemresource.graph.EntityGraphManager;
import de.tudarmstadt.ukp.dkpro.lexsemresource.graph.EntityGraph.DirectionMode;
import de.tudarmstadt.ukp.dkpro.lexsemresource.graph.EntityGraphManager.EntityGraphType;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.lsr.LexSemResourceComparator;


public abstract class PathBasedComparator
	extends LexSemResourceComparator
{
	private final Log log = LogFactory.getLog(getClass());

    public static final double NO_PATH   = -2.0;
    public static final double NO_LCS    = -3.0;

    protected final double NOT_RELATED = Double.NEGATIVE_INFINITY;

    protected EntityGraph entityGraph;
    protected Entity root;


	public PathBasedComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);

		try
		{
			initialize(lexSemResource);

	        // only call getGuessedRoot if root cannot be returned by the LSR itself
	        Entity root = lexSemResource.getRoot();
	        if (root != null) {
	            this.root = root;
	        }
	        else {
	            this.root = getGuessedRoot();
	        }
		}
		catch (UnsupportedOperationException e) {
			throw new LexicalSemanticResourceException();
		}
    }

	public PathBasedComparator(LexicalSemanticResource lexSemResource, Entity root)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);

		try
		{
			initialize(lexSemResource);
		}
		catch (UnsupportedOperationException e) {
			throw new LexicalSemanticResourceException(e);
		}

        this.root = root;
    }

	private void initialize(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		this.entityGraph = EntityGraphManager.getEntityGraph(lexSemResource, EntityGraphType.JGraphT);
		this.entityGraph.removeCycles();
    }

    public EntityGraph getEntityGraph() {
        return entityGraph;
    }

    @Override
	public double getSimilarity(Set<Entity> entities1, Set<Entity> entities2)
		throws SimilarityException, LexicalSemanticResourceException
	{
		if (entities1.size() == 0 || entities2.size() == 0) {
			if (entities1.size() == 0) {
                log.debug("First entity set is empty.");
            }
            else {
                log.debug("Second entity set is empty.");
            }

            return PathBasedComparator.NOT_FOUND;
        }

        List<Double> relatednessValues = new ArrayList<Double>();
        for (Entity e1 : entities1) {
            for (Entity e2 : entities2) {
            	relatednessValues.add( getSimilarity(e1, e2) );
            }
        }

		return getBestRelatedness(relatednessValues);
    }

//    @Override
//	protected double getBestRelatedness(List<Double> relatednessValues)
//		throws RelatednessException
//	{
//		List<Double> scores = new ArrayList<Double>();
//		for (double d : relatednessValues)
//		{
//			if (d >= 0.0) {
//				scores.add(d);
//			}
//		}
//
//		if (scores.size() == 0) {
//			scores.add(NOT_RELATED);
//		}
//
//		return super.getBestRelatedness(scores);
//	}

    /**
     * @param e1
     * @param e2
     * @return The path length between the two entites or NO_PATH if no path was found.
     * @throws LexicalSemanticResourceException
     */
	protected double getShortestPathLength(Entity e1, Entity e2)
		throws LexicalSemanticResourceException
	{
		double minPathLength;

        // try to use the shortest path length method from the LSR first
        // catch the exception if the method is not implemented and use the (usually slower) entity graph implementation
        try {
            minPathLength = getLexicalSemanticResource().getShortestPathLength(e1, e2);
        }
        catch (UnsupportedOperationException e) {
            log.info("Falling back to the entity graph implementation of shortest path (usually slower).");

            minPathLength = entityGraph.getShortestPathLength(e1, e2, DirectionMode.undirected);
        }

        if (minPathLength == Double.POSITIVE_INFINITY) {
            return NO_PATH;
        }

        return minPathLength;
    }

    public Entity getRoot() {
        return this.root;
    }

    public void setRoot(Entity root) {
        this.root = root;
    }

	protected Entity getGuessedRoot()
		throws LexicalSemanticResourceException
	{
		Entity guessedRoot = null;

        Set<Entity> roots = getEntityGraph().getRoots();
        if (roots.size() > 1) {
            EntityGraph lcc = getEntityGraph().getLargestConnectedComponent();
            Set<Entity> lccRoots = lcc.getRoots();
            if (lccRoots.size() == 1) {
                guessedRoot = lccRoots.iterator().next();
            }
//            else {
//                logger.info("Roots: " + lccRoots);
//                throw new LexicalSemanticResourceException("No root or multiple roots found in LCC. Cannot compute relatedness.");
//            }
        }
        else if (roots.size() == 1) {
            guessedRoot = roots.iterator().next();
        }
//        else {
//            throw new LexicalSemanticResourceException("No root found. Cannot compute relatedness.");
//        }

        return guessedRoot;
    }
}
