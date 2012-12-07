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
package de.tudarmstadt.ukp.similarity.experiments.rte;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.dkpro.io.RTECorpusReader;

public class RteExperiments
{

    public static void main(String[] args) throws Exception
    {
        String context = DKProContext.getContext().getWorkspace("RTE").getAbsolutePath();
        
        CollectionReader reader = createCollectionReader(
                RTECorpusReader.class,
                RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY,
                RTECorpusReader.PARAM_INPUT_FILE, context + "/RTE1/Test/annotated_test.xml"
        );
        
        AnalysisEngineDescription tagger = createPrimitiveDescription(
                OpenNlpPosTagger.class,
                OpenNlpPosTagger.PARAM_LANGUAGE, "en"
        );
                
        AnalysisEngineDescription lemmatizer = createPrimitiveDescription(
                GateLemmatizer.class
        );
        AnalysisEngineDescription printer = createPrimitiveDescription(
                RteResultsPrinter.class
        );

        SimplePipeline.runPipeline(
                reader,
                tagger,
                lemmatizer,
                printer
        );
    }
}
