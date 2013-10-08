/*******************************************************************************
 * Copyright 2013
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
 ******************************************************************************/
package dkpro.similarity.experiments.wordchoice.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import dkpro.similarity.type.WordChoiceProblem;

public class WordChoiceAnnotationPairFactory {

    public static List<WordChoiceAnnotationPair> getWordChoiceAnnotationPairs(JCas jcas, WordChoiceProblem wcp) throws AnalysisEngineProcessException {

        List<WordChoiceAnnotationPair> wpcList = new ArrayList<WordChoiceAnnotationPair>();

        // get the sentences => they represent target and candidates
        List<Sentence> sentenceList = JCasUtil.selectCovered(jcas, Sentence.class, wcp);

        if (sentenceList.size() != 5) {
            throw new AnalysisEngineProcessException(new Throwable("Expected five sentence annotations, but only " + sentenceList.size() + " were found."));
        }

        Sentence target = sentenceList.get(0);

        List<Lemma> targetAnnotationList = JCasUtil.selectCovered(jcas, Lemma.class, target);
        if (targetAnnotationList.size() != 1) {
            System.out.println("'" + target.getCoveredText() + "'");
            throw new AnalysisEngineProcessException(new Throwable(targetAnnotationList.size() + " Lemma annotations for the target found, but expected only one."));
        }
        Lemma targetLemma = targetAnnotationList.get(0);
        POS targetPos = getPosList(jcas, targetAnnotationList).get(0);

        for (int i=1; i<=4; i++) {
            Sentence candidate = sentenceList.get(i);
            List<Lemma> candidateLemmaList = JCasUtil.selectCovered(jcas, Lemma.class, candidate);
            List<POS> candidatePosList = getPosList(jcas, candidateLemmaList);
            wpcList.add( new WordChoiceAnnotationPair(targetLemma, targetPos, candidateLemmaList, candidatePosList, wcp.getCorrectAnswer()));
        }

        return wpcList;

    }

    public static List<POS> getPosList(JCas jcas, List<Lemma> annotations) throws AnalysisEngineProcessException {
        List<POS> posList = new ArrayList<POS>();
        for (Annotation a : annotations) {
            List<POS> posAnnotations = JCasUtil.selectCovered(jcas, POS.class, a);
            if (posAnnotations.size() != 1) {
                throw new AnalysisEngineProcessException(new Throwable(posAnnotations.size() + " POS annotations for one annotation found."));
            }
            posList.add(posAnnotations.get(0));
        }
        return posList;
    }


}