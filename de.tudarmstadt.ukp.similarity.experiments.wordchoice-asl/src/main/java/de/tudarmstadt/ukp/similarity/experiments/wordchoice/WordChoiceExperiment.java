package de.tudarmstadt.ukp.similarity.experiments.wordchoice;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.JiangConrathRelatednessResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LSRRelatednessResourceBase;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LinRelatednessResource;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.io.WordChoiceProblemReader;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.io.WordChoiceProblemsEvaluator;

public class WordChoiceExperiment
{
    public static void main(String[] args) throws Exception
    {
        CollectionReader reader = createCollectionReader(
                WordChoiceProblemReader.class,
                WordChoiceProblemReader.PARAM_PATH, "classpath:/datasets/en/",
                WordChoiceProblemReader.PARAM_PATTERNS, new String[] {
                        ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.txt"
                }
        );
        
        AnalysisEngineDescription tagger = createPrimitiveDescription(
                OpenNlpPosTagger.class,
                OpenNlpPosTagger.PARAM_LANGUAGE, "en"
        );
                
        AnalysisEngineDescription lemmatizer = createPrimitiveDescription(
                GateLemmatizer.class
        );

        AnalysisEngineDescription pairAnnotator = createPrimitiveDescription(
                WordChoiceWordPairAnnotator.class
        );
        
        AnalysisEngineDescription semRel1 = createPrimitiveDescription(
                WordChoiceResourceBasedSemRelAnnotator.class,
                WordChoiceResourceBasedSemRelAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        JiangConrathRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );
        
        AnalysisEngineDescription semRel2 = createPrimitiveDescription(
                WordChoiceResourceBasedSemRelAnnotator.class,
                WordChoiceResourceBasedSemRelAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        LinRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );

        AnalysisEngineDescription evaluator = createPrimitiveDescription(
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
