package de.tudarmstadt.ukp.similarity.experiments.wordchoice.util;

import static org.uimafit.util.JCasUtil.selectCovered;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.uimafit.util.JCasUtil;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.similarity.type.WordChoiceProblem;

public class WordChoiceProblemFactory {

    public static List<WCProblem> getWordChoiceProblems(JCas jcas) throws AnalysisEngineProcessException {

        List<WCProblem> wcpList = new ArrayList<WCProblem>();

        for (WordChoiceProblem wcp : JCasUtil.select(jcas, WordChoiceProblem.class)) {
            // get the sentences => they represent target and candidates
            List<Sentence> candidateList = selectCovered(jcas, Sentence.class, wcp);

            if (candidateList.size() != 5) {
                throw new AnalysisEngineProcessException(new Throwable("Expected five sentence annotations, but only " + candidateList.size() + " were found."));
            }

            Sentence target = candidateList.get(0);

            List<Lemma> targetAnnotationList = selectCovered(jcas, Lemma.class, target);
            if (targetAnnotationList.size() != 1) {
                System.out.println("'" + target.getCoveredText() + "'");
                throw new AnalysisEngineProcessException(new Throwable(targetAnnotationList.size() + " Lemma annotations for the target found, but expected only one."));
            }
            String targetString = targetAnnotationList.get(0).getValue();

            String cand1 = getCandidate(jcas, candidateList.get(1));
            String cand2 = getCandidate(jcas, candidateList.get(2));
            String cand3 = getCandidate(jcas, candidateList.get(3));
            String cand4 = getCandidate(jcas, candidateList.get(4));

            List<String> candList1 = getCandidateList(jcas, candidateList.get(1));
            List<String> candList2 = getCandidateList(jcas, candidateList.get(2));
            List<String> candList3 = getCandidateList(jcas, candidateList.get(3));
            List<String> candList4 = getCandidateList(jcas, candidateList.get(4));

            wcpList.add(
                    new WCProblem(targetString, cand1, cand2, cand3, cand4, candList1, candList2, candList3, candList4, wcp.getCorrectAnswer())
            );

        }

        return wcpList;
    }

    private static List<String> getCandidateList(JCas jcas, Sentence candidateAnnotation) {
        List<Lemma> candidateAnnotationList = selectCovered(jcas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma.class, candidateAnnotation);
        return getLemmaList(candidateAnnotationList);

    }

    private static String getCandidate(JCas jcas, Sentence candidateAnnotation) {
        List<String> lemmas = getCandidateList(jcas, candidateAnnotation);
        return StringUtils.join(lemmas, " ");
    }

    public static List<String> getLemmaList(List<Lemma> lemmas) {
        List<String> lemmaList = new ArrayList<String>();
        for (Lemma l : lemmas) {
            lemmaList.add( l.getValue() );
        }
        return lemmaList;
    }

}
