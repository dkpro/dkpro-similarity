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

import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;

public class WordChoiceAnnotationPair {

    private Lemma targetLemma;
    private List<Lemma> candidateLemmas;

    private POS targetPos;
    private List<POS> candidatePos;

    private int correct;

    public WordChoiceAnnotationPair(Lemma targetLemma, POS targetPos, List<Lemma> candidateLemmas, List<POS> candidatePos, int correct) {
        super();
        this.targetLemma = targetLemma;
        this.candidateLemmas = candidateLemmas;
        this.targetPos = targetPos;
        this.candidatePos = candidatePos;
        this.correct = correct;
    }

    public Lemma getTargetLemma() {
        return targetLemma;
    }
    public void setTargetLemma(Lemma targetLemma) {
        this.targetLemma = targetLemma;
    }
    public List<Lemma> getCandidateLemmas() {
        return candidateLemmas;
    }
    public void setCandidateLemmas(List<Lemma> candidateLemmas) {
        this.candidateLemmas = candidateLemmas;
    }

    public POS getTargetPos() {
        return targetPos;
    }

    public void setTargetPos(POS targetPos) {
        this.targetPos = targetPos;
    }

    public List<POS> getCandidatePos() {
        return candidatePos;
    }

    public void setCandidatePos(List<POS> candidatePos) {
        this.candidatePos = candidatePos;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }


    public String getTargetString() {
        return targetLemma.getValue();
    }
    public List<String> getCandidateStrings() {
        List<String> candidates = new ArrayList<String>();
        for (Lemma lemma : candidateLemmas) {
            candidates.add( lemma.getValue() );
        }
        return candidates;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append(getTargetString());
        sb.append(System.getProperty("line.separator"));
        sb.append(StringUtils.join(getCandidateStrings(), " "));
        sb.append(System.getProperty("line.separator"));
        return sb.toString();
    }

}
