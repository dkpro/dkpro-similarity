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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.langtech.substituter.MLSenseSubstituter;
import de.tudarmstadt.langtech.substituter.SenseSubstituter;
import de.tudarmstadt.langtech.substituter.Substitution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import dkpro.similarity.algorithms.api.JCasTextSimilarityMeasureBase;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasure;


/**
 * Similarity measure based on the lexical substitution system
 * based on supervised word sense disambiguation
 * <a href="http://link.springer.com/article/10.1007%2Fs10579-012-9180-5">(Biemann, 2012)</a>.
 */
public class TWSISubstituteWrapper
	extends JCasTextSimilarityMeasureBase
{
	SenseSubstituter sensub;
	TextSimilarityMeasure measure;
	
	public TWSISubstituteWrapper(TextSimilarityMeasure measure)
		throws IOException
	{		
		this.sensub = new MLSenseSubstituter(DkproContext.getContext().getWorkspace() + "/TWSI2/conf/TWSI2_config.conf");
		this.measure = measure;
	}
	
	@Override
	public double getSimilarity(JCas jcas1, JCas jcas2)
		throws SimilarityException
	{
		List<String> subst1 = getSubstitutions(jcas1);
		List<String> subst2 = getSubstitutions(jcas2);
		
		return measure.getSimilarity(subst1, subst2);
	}
	
    @Override
    public double getSimilarity(JCas jcas1, JCas jcas2, Annotation coveringAnnotation1,
            Annotation coveringAnnotation2)
        throws SimilarityException
    {
        List<String> subst1 = getSubstitutions(jcas1, coveringAnnotation1);
        List<String> subst2 = getSubstitutions(jcas2, coveringAnnotation2);
        
        return measure.getSimilarity(subst1, subst2);
    }
	 
	public List<String> getSubstitutions(JCas jcas)
	{
		List<String> tokens = new ArrayList<String>();
		List<String> postags = new ArrayList<String>();;
		
		for (Token t : JCasUtil.select(jcas, Token.class))
		{
			try
			{
				tokens.add(t.getLemma().getValue().toLowerCase());
				postags.add(t.getPos().getPosValue());
			}
			catch (NullPointerException e) {
				System.err.println("Couldn't read lemma value for token \"" + t.getCoveredText() + "\"");
			}
		}
		
		return getSubstitutions(tokens, postags);
	}
	
    public List<String> getSubstitutions(JCas jcas, Annotation coveringAnnotation)
    {
        List<String> tokens = new ArrayList<String>();
        List<String> postags = new ArrayList<String>();;
        
        for (Token t : JCasUtil.selectCovered(jcas, Token.class, coveringAnnotation))
        {
            try
            {
                tokens.add(t.getLemma().getValue().toLowerCase());
                postags.add(t.getPos().getPosValue());
            }
            catch (NullPointerException e) {
                System.err.println("Couldn't read lemma value for token \"" + t.getCoveredText() + "\"");
            }
        }
        
        return getSubstitutions(tokens, postags);
    }

    public List<String> getSubstitutions(List<String> tokens, List<String> postags)
	{	
		// Append BOS + EOS tags
		tokens.add(0, "%^%");
		postags.add(0, "BOS");
		tokens.add("%$%");
		postags.add("EOS");		
				
		// Sense substitutor operates on arrays
		String[] tokenArray = tokens.toArray(new String[tokens.size()]);
		String[] postagsArray = postags.toArray(new String[postags.size()]);
		
		List<String> resultList = new ArrayList<String>();
		
		for (int i = 0; i < tokens.size(); i++)
		{
			// System.out.println(postags.get(i) + " / " + tokens.get(i));
			
			// TWSI only operates on nouns
			if (postags.get(i).startsWith("NN"))
			{
				try
				{
					Substitution subst = sensub.getSubstitution(i, tokenArray, postagsArray);
					
					if (subst != null)
					{
						for (String[] substitution : subst.getSubstitutions())
						{
							//resultList.add(subst.getSense().replaceAll("@@", ""));
							resultList.add(substitution[0]);
						}
					}
					else
					{
						resultList.add(tokens.get(i)); 
					}
				}
				catch (InstantiationError e)
				{
					resultList.add(tokens.get(i)); 
				}
			} else {
				resultList.add(tokens.get(i));
			}
		} 
		
		// Remove BOF + EOF words
		resultList.remove(0);
		resultList.remove(resultList.size() - 1);
		
		System.out.println(resultList);
		
		return resultList;
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
