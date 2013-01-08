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
package de.tudarmstadt.ukp.similarity.experiments.rte.attic;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.jcas.JCas;
import org.uimafit.component.JCasAnnotator_ImplBase;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.similarity.dkpro.io.CombinationReader;
import de.tudarmstadt.ukp.similarity.entailment.type.EntailmentClassificationOutcome;

public class RteResultsPrinter
    extends JCasAnnotator_ImplBase
{

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {
        try {
            JCas view1 = jcas.getView(CombinationReader.VIEW_1);
            JCas view2 = jcas.getView(CombinationReader.VIEW_2);
            
            System.out.println(view1.getDocumentText());
            System.out.println(view2.getDocumentText());
            
            EntailmentClassificationOutcome outcome = JCasUtil.selectSingle(jcas, EntailmentClassificationOutcome.class);
            System.out.println(outcome);
        }
        catch (CASException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}