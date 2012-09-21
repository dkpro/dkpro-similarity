package de.tudarmstadt.ukp.similarity.experiments.wordpairs;

import static org.uimafit.factory.AnalysisEngineFactory.createPrimitiveDescription;
import static org.uimafit.factory.CollectionReaderFactory.createCollectionReader;
import static org.uimafit.factory.ExternalResourceFactory.createExternalResourceDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.uimafit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.JiangConrathRelatednessResource;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LSRRelatednessResourceBase;
import de.tudarmstadt.ukp.similarity.dkpro.resource.lsr.LinRelatednessResource;
import de.tudarmstadt.ukp.similarity.experiments.wordpairs.io.SemanticRelatednessResultWriter;
import de.tudarmstadt.ukp.similarity.experiments.wordpairs.io.WordPairReader;

public class WordPairExperiment
{

    public static void main(String[] args)
        throws Exception
    {
        CollectionReader reader = createCollectionReader(
                WordPairReader.class,
                WordPairReader.PARAM_PATH, "classpath:/datasets/wordpairs/en/",
                WordPairReader.PARAM_PATTERNS,
                new String[] { ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.gold.pos.txt" }
        );

        AnalysisEngineDescription semRel1 = createPrimitiveDescription(
                ResourceBasedAnnotator.class,
                ResourceBasedAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        JiangConrathRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );

        AnalysisEngineDescription semRel2 = createPrimitiveDescription(
                ResourceBasedAnnotator.class,
                ResourceBasedAnnotator.SR_RESOURCE, createExternalResourceDescription(
                        LinRelatednessResource.class,
                        LSRRelatednessResourceBase.PARAM_RESOURCE_NAME, "wordnet",
                        LSRRelatednessResourceBase.PARAM_RESOURCE_LANGUAGE, "en"
                )
        );

        
        AnalysisEngineDescription writer = createPrimitiveDescription(
                SemanticRelatednessResultWriter.class,
                SemanticRelatednessResultWriter.PARAM_SHOW_DETAILS, true
        );

        SimplePipeline.runPipeline(
                reader,
                semRel1,
                semRel2,
                writer
        );
    }
}