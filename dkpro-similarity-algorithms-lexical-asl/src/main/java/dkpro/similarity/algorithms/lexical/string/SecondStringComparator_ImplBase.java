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

import org.apache.commons.lang.StringUtils;

import com.wcohen.ss.AbstractStringDistance;
import com.wcohen.ss.api.StringWrapper;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;


public abstract class SecondStringComparator_ImplBase
    extends TextSimilarityMeasureBase
{
    
    protected AbstractStringDistance secondStringMeasureL1;
    protected AbstractStringDistance secondStringMeasureL2;

    public enum SecondStringTextSimilarityMeasure {
        Jaro,
        JaroWinkler,
        Levenshtein,
        MongeElkan
    }
    
    @Override
    public double getSimilarity(String s1, String s2)
        throws SimilarityException
    {
        // preparing the string saves some cycles through caching, if many comparisons with the same string are done.
        StringWrapper wrappedString1 = secondStringMeasureL1.prepare(s1);
        StringWrapper wrappedString2 = secondStringMeasureL1.prepare(s2);

        return secondStringMeasureL1.score(wrappedString1, wrappedString2);
    }
    
    @Override
    public double getSimilarity(Collection<String> s1, Collection<String> s2)
        throws SimilarityException
    {
        if (s1.size() == 0 || s2.size() == 0) {
            return 0.0;
        }

        String concatenatedString1 = StringUtils.join(s1, " ");
        String concatenatedString2 = StringUtils.join(s2, " ");

        if (concatenatedString1.length() == 0 || concatenatedString2.length() == 0) {
            return 0.0;
        }

        // find tokens (I know that we already know what the tokens are, but the SecondString implementation needs it that way)
        StringWrapper wrappedString1 = secondStringMeasureL2.prepare(concatenatedString1);
        StringWrapper wrappedString2 = secondStringMeasureL2.prepare(concatenatedString2);

        double distance = secondStringMeasureL2.score(wrappedString1, wrappedString2);

        return distance;
    }
}