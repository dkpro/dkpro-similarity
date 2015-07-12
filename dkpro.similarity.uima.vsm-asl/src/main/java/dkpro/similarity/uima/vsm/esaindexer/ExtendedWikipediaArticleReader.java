/*******************************************************************************
 * Copyright 2013 Mateusz Parzonka
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
package dkpro.similarity.uima.vsm.esaindexer;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.WikipediaArticleReader;
import dkpro.similarity.algorithms.vsm.util.ProgressMeter;

/**
 * Extends the standard WikipediaArticleReader with progress tracking.
 * 
 * @author Mateusz Parzonka
 */
public class ExtendedWikipediaArticleReader extends WikipediaArticleReader {
	
	protected ProgressMeter progressMeter;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		progressMeter = new ProgressMeter(getProgress()[0].getTotal());
	}

	@Override
	public void getNext(JCas jcas) throws IOException, CollectionException {
		super.getNext(jcas);
		progressMeter.next();
		getLogger().info("Indexing article " + progressMeter);
	}

}