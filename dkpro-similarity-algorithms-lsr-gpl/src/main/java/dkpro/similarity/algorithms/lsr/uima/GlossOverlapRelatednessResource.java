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
package dkpro.similarity.algorithms.lsr.uima;
 
import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.dkpro.lexsemresource.exception.LexicalSemanticResourceException;
import dkpro.similarity.algorithms.lsr.gloss.GlossOverlapComparator;
import dkpro.similarity.algorithms.lsr.uima.path.LSRRelatednessResourceBase;

public final class GlossOverlapRelatednessResource
	extends LSRRelatednessResourceBase
{

    public static final String PARAM_USE_PSEUDO_GLOSSES = "UsePseudoGlosses";
    @ConfigurationParameter(name = PARAM_USE_PSEUDO_GLOSSES, mandatory = true, defaultValue="false")
    protected String usePseudoGlossesString;
    
    @SuppressWarnings({ "rawtypes" })
    @Override
	public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
		throws ResourceInitializationException
	{
		if (!super.initialize(aSpecifier, aAdditionalParams)) {
			return false;
		}

        try {
            boolean usePseudoGlosses = Boolean.parseBoolean(usePseudoGlossesString);
            measure = new GlossOverlapComparator(lsr, usePseudoGlosses);
        }
        catch (LexicalSemanticResourceException e) {
            throw new ResourceInitializationException(e);
        }
		
		return true;
	}
}