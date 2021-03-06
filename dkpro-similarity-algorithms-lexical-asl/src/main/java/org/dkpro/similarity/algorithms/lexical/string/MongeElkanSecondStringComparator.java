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
package org.dkpro.similarity.algorithms.lexical.string;

import com.wcohen.ss.Level2MongeElkan;
import com.wcohen.ss.MongeElkan;

public class MongeElkanSecondStringComparator
    extends SecondStringComparator_ImplBase
{
    public MongeElkanSecondStringComparator()
    {
            secondStringMeasureL1 = new MongeElkan();
            secondStringMeasureL2 = new Level2MongeElkan();
    }
}