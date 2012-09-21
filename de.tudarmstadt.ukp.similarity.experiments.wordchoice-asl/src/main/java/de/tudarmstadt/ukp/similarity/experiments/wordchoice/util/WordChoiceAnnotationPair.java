package de.tudarmstadt.ukp.similarity.experiments.wordchoice.util;

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
