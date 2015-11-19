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

import org.apache.commons.codec.language.DoubleMetaphone;

/**
 * For English words.
 * Under most conditions it should be the than {@link MetaphoneComparator} or {@link SoundexComparator}.
 *  
 * @author zesch
 *
 */
public class DoubleMetaphoneComparator
    extends SoundComparatorBase
{

    public DoubleMetaphoneComparator()
    {
        encoder = new DoubleMetaphone();
    }
}
