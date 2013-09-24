package de.tudarmstadt.ukp.similarity.dkpro.resource.style;

import java.util.Map;

import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.similarity.dkpro.resource.JCasTextSimilarityResourceBase;
import dkpro.similarity.algorithms.style.AvgCharactersPerTokenMeasure;


public class AvgCharactersPerTokenResource
	extends JCasTextSimilarityResourceBase
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
        throws ResourceInitializationException
    {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }

        this.mode = TextSimilarityResourceMode.jcas;
        
        measure = new AvgCharactersPerTokenMeasure();
        
        return true;
    }
}
