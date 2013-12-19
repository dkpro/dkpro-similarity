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
 * Implements the distance measure by Rada et al. (1989).
 * The distance between two terms a and b is defined as the minimum number of edges separating
 * the nodes that represent a and b in the semantic net.
 * 
 * @author zesch
 *
 */
public class PathLengthComparator
	extends PathBasedComparator
{
	protected final static double NOT_RELATED = Double.POSITIVE_INFINITY;
	
    private double diameter = Double.MAX_VALUE;
    private boolean convertToRelatedness = false;
    
	public PathLengthComparator(LexicalSemanticResource lexSemResource)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
    }

	public PathLengthComparator(LexicalSemanticResource lexSemResource,	boolean convertToRelatedness)
		throws LexicalSemanticResourceException
	{
		super(lexSemResource);
		this.convertToRelatedness = convertToRelatedness;
        
        if (convertToRelatedness) {
            this.diameter = getEntityGraph().getDiameter();
        }
    }

	@Override
    public double getSimilarity(Entity e1, Entity e2)
		throws SimilarityException, LexicalSemanticResourceException
	{
        // entity overrides the equals method with a usable variant
        if (e1.equals(e2)) {
            return 0.0;
        }
        double distance = getShortestPathLength(e1, e2);
        
        if (convertToRelatedness) {
            return (diameter - distance) / diameter;
        }
        else {
            return distance;
        }
    }
	
    @Override
    public boolean isDistanceMeasure()
    {
        return true;
    }
}