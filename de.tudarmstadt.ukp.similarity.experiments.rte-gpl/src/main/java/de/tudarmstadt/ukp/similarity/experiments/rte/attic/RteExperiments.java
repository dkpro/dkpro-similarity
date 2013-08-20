/*******************************************************************************
 * Copyright 2013
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 ******************************************************************************/
package de.tudarmstadt.ukp.similarity.experiments.rte.attic;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReader;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.pipeline.SimplePipeline;

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
        
        CollectionReader reader = createReader(
                RTECorpusReader.class,
                RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY,
                RTECorpusReader.PARAM_INPUT_FILE, context + "/RTE1/Test/annotated_test.xml"
        );
        
        AnalysisEngineDescription tagger = createEngineDescription(
                OpenNlpPosTagger.class,
                OpenNlpPosTagger.PARAM_LANGUAGE, "en"
        );
                
        AnalysisEngineDescription lemmatizer = createEngineDescription(
                GateLemmatizer.class
        );
        AnalysisEngineDescription printer = createEngineDescription(
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
