package de.tudarmstadt.ukp.similarity.experiments.rte.util;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.component.JCasConsumer_ImplBase;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.resources.DKProContext;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.RTECorpusReader;
import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader.CombinationStrategy;
import de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome;
import de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.Dataset;

import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.DATASET_DIR;
import static de.tudarmstadt.ukp.similarity.experiments.rte.Pipeline.FEATURES_DIR;


public class GoldstandardCreator
{
	public static void outputGoldstandard(Dataset dataset)
		throws Exception
	{	        
		CollectionReader reader = createCollectionReader(
                RTECorpusReader.class,
                RTECorpusReader.PARAM_COMBINATION_STRATEGY, CombinationStrategy.SAME_ROW_ONLY,
                RTECorpusReader.PARAM_INPUT_FILE, RteUtil.getInputFilePathForDataset(DATASET_DIR, dataset));
        
        AnalysisEngineDescription tagger = createPrimitiveDescription(
                OpenNlpPosTagger.class,
                OpenNlpPosTagger.PARAM_LANGUAGE, "en");
                
        AnalysisEngineDescription lemmatizer = createPrimitiveDescription(
                GateLemmatizer.class);
        
        AnalysisEngineDescription printer = createPrimitiveDescription(
                GoldstandardWriter.class,
                GoldstandardWriter.PARAM_DATASET_NAME, dataset.toString());

        SimplePipeline.runPipeline(
                reader,
                tagger,
                lemmatizer,
                printer);
	}

	
}
