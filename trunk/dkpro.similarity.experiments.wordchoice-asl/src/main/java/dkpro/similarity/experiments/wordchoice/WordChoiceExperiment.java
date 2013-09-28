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
 ******************************************************************************/
package dkpro.similarity.experiments.wordchoice;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import dkpro.similarity.algorithms.lsr.uima.path.JiangConrathRelatednessResource;
import dkpro.similarity.algorithms.lsr.uima.path.LSRRelatednessResourceBase;
import dkpro.similarity.algorithms.lsr.uima.path.LinRelatednessResource;
import dkpro.similarity.experiments.wordchoice.io.WordChoiceProblemReader;
import dkpro.similarity.experiments.wordchoice.io.WordChoiceProblemsEvaluator;

public class WordChoiceExperiment
{
    public static void main(String[] args) throws Exception
    {
        CollectionReader reader = createReader(
                WordChoiceProblemReader.class,
                WordChoiceProblemReader.PARAM_SOURCE_LOCATION, "classpath:/datasets/en/",
                WordChoiceProblemReader.PARAM_PATTERNS, new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.txt"
                }
        );
        
        AnalysisEngineDescription tagger = createEngineDescription(
                OpenNlpPosTagger.class,
                OpenNlpPosTagger.PARAM_LANGUAGE, "en"
        );
                
        AnalysisEngineDescription lemmatizer = createEngineDescription(
                GateLemmatizer.class
        );

        AnalysisEngineDescription pairAnnotator = createEngineDescription(
                WordChoiceWordPairAnnotator.class
        );
        
        AnalysisEngineDescription semRel1 = createEngineDescription(
                WordChoiceResourceBasedSemRelAnnotator.class,
                WordChoiceResourceBasedSemRelAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        JiangConrathRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );
        
        AnalysisEngineDescription semRel2 = createEngineDescription(
                WordChoiceResourceBasedSemRelAnnotator.class,
                WordChoiceResourceBasedSemRelAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        LinRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );

        AnalysisEngineDescription evaluator = createEngineDescription(
                WordChoiceProblemsEvaluator.class
        );

        SimplePipeline.runPipeline(
                reader,
                tagger,
                lemmatizer,
                pairAnnotator,
                semRel1,
                semRel2,
                evaluator
        );
    }
}
