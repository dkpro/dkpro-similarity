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
package dkpro.similarity.algorithms.lexical.string;

import java.util.Collection;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

public abstract class SimpleStringMatchComparator_ImplBase
	extends TextSimilarityMeasureBase
{

    protected enum SimpleStringMatchStrategy
    {
        ExactStringMatch,
        SubstringMatch,
        BoundedSubstringMatch
    }
    
	public final static double NO_MATCH = 0.0;
    public final static double MATCH    = 1.0;

    protected SimpleStringMatchStrategy strategy;
    
    @Override
    public double getSimilarity(Collection<String> s1, Collection<String> s2)
        throws SimilarityException
    {
		if (matchList(s1, s2)) {
			return MATCH;
		}
		else {
			return NO_MATCH;
		}
    }

    @Override
	public double getSimilarity(String s1, String s2)
		throws SimilarityException
	{
		if (match(s1,s2)) {
            return MATCH;
        }
        else {
            return NO_MATCH;
        }
    }

    private boolean match(String s1, String s2)
    {
    	if (s1.equals(s2)) {
    		return true;
    	}
    	else {
	    	switch (strategy)
	    	{
	    		case SubstringMatch:
	    			if (s1.contains(s2) || s2.contains(s1)) {
		                return true;
		            }
		            break;
	    		case BoundedSubstringMatch:
		            if (s1.startsWith(s2) || s2.startsWith(s1)) {
		                return true;
		            }
		            else if (s1.endsWith(s2.toLowerCase()) || s2.endsWith(s1.toLowerCase())) {
		                return true;
		            }
		            break;
	        }
    	}
        
        return false;
    }

	private boolean matchList(Collection<String> s1, Collection<String> s2)
	{
		if (strategy.equals(SimpleStringMatchStrategy.ExactStringMatch)) {
            if (s1.size() != s2.size()) {
                return false;
            }
            
            for (int i=0; i<s1.size(); i++) {
                if (!s1.toArray()[i].equals(s2.toArray()[i])) {
                    return false;
                }
            }
            
            return true;
        }
        else if (strategy.equals(SimpleStringMatchStrategy.SubstringMatch) || strategy.equals(SimpleStringMatchStrategy.BoundedSubstringMatch)) {
            for (String item1 : s1) {
                for (String item2 : s2) {
                    if (match(item1,item2)) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        return false;
    }
}