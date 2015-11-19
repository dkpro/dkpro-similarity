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
package dkpro.similarity.algorithms.wikipedia.measures;

import java.util.HashMap;
import java.util.Map;

public interface Measures {

    /** Return value, if no relatedness could be computed, because one or both terms are missing in Wikipedia. */
    public static final double NOT_IN_WIKIPEDIA = -1.0;
    
    /** Return value, if no relatedness could be computed, because one or both terms are not categorized. */
    public static final double NOT_CATEGORIZED = -2.0;

    /** Return value, if a disambiguation page was hit, but the disambiguation module could not find a suitable sense. */
    public static final double NO_SENSE = -3.0;
    
    /**
     * The available relatedness measures. 
     */
    public enum Measure { PathLengthAverage, 
                            PathLengthBest,
                            PathLengthSelectivity,
                            LeacockChodorowAverage,
                            LeacockChodorowBest,
                            LeacockChodorowSelectivity,
                            ResnikAverage,
                            ResnikBest,
                            ResnikSelectivityLinear, // linear selectivity
                            ResnikSelectivityLog,    // logarithmic selectivity
                            LinAverage,
                            LinBest,
                            JiangConrathAverage,
                            JiangConrathBest,
                            WuPalmerAverage,
                            WuPalmerBest,
                            LeskFirst,
                            LeskFull,
                            WikiLinkMeasure};

    
    public enum CombinationStrategy { Average, Best, SelectivityLinear, SelectivityLog, None };
    
    @SuppressWarnings("serial")
    public Map<Measure, Boolean> isSymmetricMap = new HashMap<Measure, Boolean>() {
        {
            put(Measure.PathLengthAverage, true);
            put(Measure.PathLengthBest, true);
            put(Measure.PathLengthSelectivity, true);
            put(Measure.LeacockChodorowAverage, true);
            put(Measure.LeacockChodorowBest, true);
            put(Measure.LeacockChodorowSelectivity, true);
            put(Measure.ResnikAverage, true);
            put(Measure.ResnikBest, true);
            put(Measure.ResnikSelectivityLinear, true);
            put(Measure.ResnikSelectivityLog, true);
            put(Measure.LinAverage, true);
            put(Measure.LinBest, true);
            put(Measure.JiangConrathAverage, true);
            put(Measure.JiangConrathBest, true);
            put(Measure.WuPalmerAverage, true);
            put(Measure.WuPalmerBest, true);
            put(Measure.LeskFirst, true);
            put(Measure.LeskFull, true);
            put(Measure.WikiLinkMeasure, true);
        }
    };
    
}