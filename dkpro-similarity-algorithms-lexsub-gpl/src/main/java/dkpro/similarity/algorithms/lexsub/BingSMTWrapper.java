/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.algorithms.lexsub;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.gate.GateLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.opennlp.OpenNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;
import dkpro.similarity.algorithms.lexsub.util.BingTranslator;

/**
 * Similarity measure which uses the Microsoft Bing translator to translate
 * the original texts into a given bridge language and back to the original
 * language. The idea is that in the translation process, lexical gaps are
 * closed. 
 */
public class BingSMTWrapper
	extends JCasTextSimilarityMeasureBase
{
	public enum Language
	{
		EN,
		DE,
		ES,
		FR,
		NL
	};
	
	BingTranslator translator;
	Language originalLanguage;
	Language bridgeLanguage;
	TextSimilarityMeasure measure;
	
	public BingSMTWrapper(TextSimilarityMeasure measure, Language originalLanguage, Language bridgeLanguage)
	{		
		this.translator = new BingTranslator();
		this.originalLanguage = originalLanguage;
		this.bridgeLanguage = bridgeLanguage;
		this.measure = measure;
	}
	
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation a1, Annotation a2)
        throws SimilarityException
    {
    	// Unfortunately, we have to disregard all annotations such as POS tags, lemmas,
    	// etc. as the translation API only allows to pass a string for translation. 
    	String text1 = a1.getCoveredText();
    	String text2 = a2.getCoveredText();
    	
    	System.out.println(text1);
    	
    	try
    	{
			// Translate the texts
			String bridgeText1 = translator.translate(text1, originalLanguage.toString(), bridgeLanguage.toString());
			String bridgeText2 = translator.translate(text2, originalLanguage.toString(), bridgeLanguage.toString());
			
			System.out.println(bridgeText1);
			
			// Translate them back into the original language
			text1 = translator.translate(bridgeText1, bridgeLanguage.toString(), originalLanguage.toString());
			text2 = translator.translate(bridgeText2, bridgeLanguage.toString(), originalLanguage.toString());
			
			System.out.println(text1);
			
    	} catch (IOException e) {
    		throw new SimilarityException(e);
    	}
    	
        // We now have to lemmatize again and pass the round-trip translated texts to
    	// the final similarity measure for comparison
    	Collection<String> lemmas1;
    	Collection<String> lemmas2;
    	
		try {
			lemmas1 = getLemmas(text1);
			lemmas2 = getLemmas(text2);
		}
		catch (AnalysisEngineProcessException e) {
			throw new SimilarityException(e);
		}
		catch (ResourceInitializationException e) {
			throw new SimilarityException(e);
		}
        
        return measure.getSimilarity(lemmas1, lemmas2);
    }
    
    private Collection<String> getLemmas(String text)
    	throws ResourceInitializationException, AnalysisEngineProcessException
    {
    	AnalysisEngine ae = AnalysisEngineFactory.createEngine(
            createEngineDescription(
                    createEngineDescription(BreakIteratorSegmenter.class),
                    createEngineDescription(OpenNlpPosTagger.class,
            		OpenNlpPosTagger.PARAM_LANGUAGE, originalLanguage.toString().toLowerCase()),
            		createEngineDescription(GateLemmatizer.class)));

        JCasBuilder cb = new JCasBuilder(ae.newJCas());
        cb.add(text);
        cb.close();
            
        JCas jcas = cb.getJCas();
        
        ae.process(jcas);
        
        // Get the lemmas
        Collection<Lemma> lemmas = JCasUtil.select(jcas, Lemma.class);
        
        // Convert to strings
        Collection<String> strings = new ArrayList<String>();
        for (Lemma lemma : lemmas) {
            strings.add(lemma.getValue().toLowerCase());
        }
        
        return strings;
    }
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
		throw new SimilarityException(new NotImplementedException());
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + "_" + measure.getName();
	}
}