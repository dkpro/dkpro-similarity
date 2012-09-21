package de.tudarmstadt.ukp.similarity.experiments.wordpairs;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.descriptor.ExternalResource;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.similarity.algorithms.api.SimilarityException;
import de.tudarmstadt.ukp.similarity.algorithms.api.TextSimilarityMeasure;
import de.tudarmstadt.ukp.similarity.type.SemanticRelatedness;
import de.tudarmstadt.ukp.similarity.type.WordPair;

public class ResourceBasedAnnotator 
    extends JCasAnnotator_ImplBase
{

    public final static String SR_RESOURCE = "SemanticRelatednessResource";
    @ExternalResource(key = SR_RESOURCE)
    protected TextSimilarityMeasure measure;
    
    @Override
	public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {

        double semRelValue = -1.0;

        String term1 = null;
        String term2 = null;

        int i = 0;
        for (WordPair wp : JCasUtil.select(jcas, WordPair.class)) {
            i++;
            if (i % 10 == 0) {
                getContext().getLogger().log(Level.INFO, measure.getName() + " processing word pair " + i);
            }

            term1 = wp.getWord1();
            term2 = wp.getWord2();

            // are the terms initialized?
            if (term1 == null || term2 == null) {
                throw new AnalysisEngineProcessException(new Throwable("Could not initialize terms."));
            }

            // compute relatedness
            try {
                semRelValue = measure.getSimilarity(term1, term2);
            } catch (SimilarityException e) {
                throw new AnalysisEngineProcessException(e);
            }

            // write a SR annotation (features: measure type & value)
            SemanticRelatedness semRelAnnotation = new SemanticRelatedness(jcas);
            semRelAnnotation.setMeasureType(measure.getName());
            semRelAnnotation.setMeasureName(measure.getName());
            semRelAnnotation.setRelatednessValue(semRelValue);
            semRelAnnotation.setTerm1(term1);
            semRelAnnotation.setTerm2(term2);
            semRelAnnotation.setWordPair(wp);
            semRelAnnotation.addToIndexes();
        }
    }
}