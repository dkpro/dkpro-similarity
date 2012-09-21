package de.tudarmstadt.ukp.similarity.experiments.wordchoice;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.treetagger.TreeTaggerPosLemmaTT4J;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.JiangConrathRelatednessResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LSRRelatednessResourceBase;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LinRelatednessResource;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.WordChoiceResourceBasedSemRelAnnotator;
import de.tudarmstadt.ukp.similarity.experiments.wordchoice.WordChoiceWordPairAnnotator;
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
                TreeTaggerPosLemmaTT4J.class,
                TreeTaggerPosLemmaTT4J.PARAM_LANGUAGE_CODE, "en"
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
                pairAnnotator,
                semRel1,
                semRel2,
                evaluator
        );
    }
}
