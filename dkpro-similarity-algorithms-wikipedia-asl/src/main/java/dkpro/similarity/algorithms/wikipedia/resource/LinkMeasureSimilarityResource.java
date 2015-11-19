/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package dkpro.similarity.algorithms.wikipedia.resource;

import java.util.Map;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceSpecifier;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;
import dkpro.similarity.algorithms.wikipedia.WLMComparator;
import dkpro.similarity.uima.resource.TextSimilarityResourceBase;

/**
 * This class is a resource for the link measure.
 * Users can set parameters for accessing a jwpl dump of Wikipedia.
 * @author nico.erbs@gmail.com
 *
 */
public class LinkMeasureSimilarityResource
extends TextSimilarityResourceBase
{

    public static final String PARAM_HOST = "Host";
    @ConfigurationParameter(name=PARAM_HOST, mandatory=true)
    private String host;

    public static final String PARAM_DATABASE = "Database";
    @ConfigurationParameter(name=PARAM_DATABASE, mandatory=true)
    private String database;

    public static final String PARAM_USER = "User";
    @ConfigurationParameter(name=PARAM_USER, mandatory=true, defaultValue="student")
    private String user;

    public static final String PARAM_PASSWORD = "Password";
    @ConfigurationParameter(name=PARAM_PASSWORD, mandatory=true, defaultValue="student")
    private String password;

    public static final String PARAM_USE_OUTBOUND_LINKS = "UseOutboundLinks";
    @ConfigurationParameter(name=PARAM_USE_OUTBOUND_LINKS, mandatory=true, defaultValue="false")
    private boolean useOutboundLinks;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean initialize(ResourceSpecifier aSpecifier, Map aAdditionalParams)
            throws ResourceInitializationException
            {
        if (!super.initialize(aSpecifier, aAdditionalParams)) {
            return false;
        }


        DatabaseConfiguration dbconfig = 
                new DatabaseConfiguration(host,database,user, password,
                Language.english);
        Wikipedia wiki;
        try {
            wiki = new Wikipedia(dbconfig);
        }
        catch (WikiInitializationException e) {
            throw new ResourceInitializationException();
        }
  
        TextSimilarityMeasureBase measure = new WLMComparator(wiki, false, useOutboundLinks);
        this.measure = measure;
        
        mode = TextSimilarityResourceMode.text;
        
        return true;
            }

    @Override
    public double getSimilarity(String string1, String string2)
        throws SimilarityException
    {
//        System.out.println("Computing similarity between " + string1 + " and " + string2);
        double similarity = super.getSimilarity(string1, string2);
//        if(similarity<0){
//            similarity=0;
//        }
//        if(similarity>0){
//            System.out.println(string1 +"\t" + string2 +"\t" + similarity);
//        }
//        System.out.printf("%-12s %-12s %.2f %n",
//                string1,
//                string2,
//        similarity);

        return similarity;
    }

}
