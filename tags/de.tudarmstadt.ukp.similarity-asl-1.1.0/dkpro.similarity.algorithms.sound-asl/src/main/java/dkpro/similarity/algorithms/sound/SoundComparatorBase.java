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
package dkpro.similarity.algorithms.sound;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

import de.tudarmstadt.ukp.dkpro.core.api.phonetics.util.SoundUtils;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TermSimilarityMeasureBase;

/**
 * Base class wrapper for sound based comparators implemented in commons-codec.
 * 
 * The comparators encode the strings in a phonetic form depending on the rules built into the different algorithms.
 * The encoded forms are then compared.
 * 
 * @author zesch
 *
 */
public abstract class SoundComparatorBase
    extends TermSimilarityMeasureBase
{

    protected StringEncoder encoder;
    
    
    @Override
    public double getSimilarity(String string1, String string2)
        throws SimilarityException
    {
        
        String encodedString1 = null;
        String encodedString2 = null; 
        try {
            encodedString1 = encoder.encode(string1);
            encodedString2 = encoder.encode(string2); 
        }
        catch (EncoderException e) {
            throw new SimilarityException();
        } 
        
        int value = SoundUtils.differenceEncoded(
            encodedString1,
            encodedString2
        );
        
        int minLength = Math.min(encodedString1.length(), encodedString2.length());
        
        return (double) value / minLength;
    }
}
