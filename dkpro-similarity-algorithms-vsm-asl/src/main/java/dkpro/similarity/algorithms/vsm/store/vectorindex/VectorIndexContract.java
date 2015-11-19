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
package dkpro.similarity.algorithms.vsm.store.vectorindex;

public interface VectorIndexContract
{
	public static final String CONCEPT_VECTOR_DB_NAME = "concept_vector_db";
	public static final String CONFIG_FILE_NAME = "index.conf";
	public static final String CONFIG_PROP_N_CONCEPTS = "nConcepts";
	public static final String CONFIG_FILE_ENCODING = "UTF-8";

	public static final int DEFAULT_DB_CACHE = 16;
}
