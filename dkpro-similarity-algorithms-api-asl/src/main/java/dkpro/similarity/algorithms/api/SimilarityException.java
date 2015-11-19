/*******************************************************************************
 * Copyright 2013
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
package dkpro.similarity.algorithms.api;

/**
 * Signals that the similarity computation by a similarity measure has
 * failed. It is thrown by the
 * {@link TermSimilarityMeasure#getSimilarity(String, String) getSimilarity}
 * methods.
 */
public class SimilarityException
	extends Exception
{
	private static final long serialVersionUID = -2855287805931261418L;

	public SimilarityException() {
        super();
    }
    
    public SimilarityException(String message) {
        super(message);
    }
    
    public SimilarityException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public SimilarityException(Throwable cause) {
        super(cause);
    }

}
