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
package dkpro.similarity.experiments.wordpairs.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.type.SemRelWordPair;
import dkpro.similarity.type.SemanticRelatedness;

public class SemanticRelatednessResultWriter extends JCasAnnotator_ImplBase {

    private static final String SEP = ":";
    private static final String LF = System.getProperty("line.separator");

    public static final String PARAM_SHOW_DETAILS = "ShowDetails";
    @ConfigurationParameter(name = PARAM_SHOW_DETAILS, mandatory=true, defaultValue="false")
    private boolean showDetails;

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {

        Result result = new Result();
        for (SemanticRelatedness sr : JCasUtil.select(jcas, SemanticRelatedness.class)) {
            String measureName = sr.getMeasureName();
            String measureType = sr.getMeasureType();
            String term1 = sr.getTerm1();
            String term2 = sr.getTerm2();
            double relatednessValue = sr.getRelatednessValue();

            String measureKey = measureType + "-" + measureName;

            SemRelWordPair wp = (SemRelWordPair) sr.getWordPair();

            result.addScore(term1, term2, measureKey, relatednessValue, wp.getGoldValue());
        }

        if (showDetails) {
            System.out.println("UNFILTERED");
            System.out.println(result.getWordpairs().size() + " word pairs");
            System.out.println( getFormattedResultMap(result) );
        }

        // filter invalid keys in the result object (i.e. wordpairs where at least one measure returned NOT_FOUND)
        Result filteredResult = getFilteredResult(result);

        if (showDetails) {
            System.out.println("FILTERED");
            System.out.println(filteredResult.getWordpairs().size() + " word pairs");
            System.out.println( getFormattedResultMap(filteredResult) );
        }

        if (showDetails) {
            System.out.println(DocumentMetaData.get(jcas).getDocumentTitle());
            System.out.println( getMeasures(filteredResult) );
            System.out.println( getCorrelations(filteredResult) );
            System.out.println();
        }
        else {
            for (String measure : filteredResult.measures) {
				System.out.println(getShortResults(filteredResult, measure,
						DocumentMetaData.get(jcas).getDocumentTitle()));
            }
        }
    }

    private String getFormattedResultMap(Result result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Term1");
        sb.append(SEP);
        sb.append("Term2");
        sb.append(SEP);
        sb.append("Gold");
        sb.append(SEP);
        sb.append(StringUtils.join(result.getMeasures(), SEP));
        sb.append(LF);
        for (String wordpair : result.getWordpairs()) {
            sb.append(wordpair);
            sb.append(SEP);
            sb.append(result.getGoldValue(wordpair));
            for (String measure : result.getMeasures()) {
                sb.append(SEP);
                sb.append(result.getScore(wordpair, measure));
            }
            sb.append(LF);
        }
        sb.append(LF);

        return sb.toString();
    }

    private class Result {
        private final Map<String,Scores> wordpairScoresMap;
        private final Map<String,Double> wordpairGoldMap;
        private final Set<String> measures;

        public Result() {
            wordpairScoresMap = new TreeMap<String,Scores>();
            wordpairGoldMap = new TreeMap<String,Double>();
            measures = new TreeSet<String>();
        }

        public void addScore(String term1, String term2, String measureKey, double relatednessValue, double goldValue) throws AnalysisEngineProcessException {
            String key = getKeyTerm(term1, term2);
            addScore(key, measureKey, relatednessValue, goldValue);
        }

        public void addScore(String key, String measureKey, double relatednessValue, double goldValue) throws AnalysisEngineProcessException {
            measures.add(measureKey);
            if (
            		//word pair is already stored in map
            		wordpairGoldMap.containsKey(key)
            		&&
            		//it has been evaluated with the same measure "measureKey"
            		wordpairScoresMap.get( key ).containsMeasure(measureKey)
            		) {
                System.out.println("wordpairGoldMap already contains key: " + key);
                System.out.println("Ignoring this duplicate word pair. Duplicates might occurr because of stemming.");
                return;
            }
            wordpairGoldMap.put(key, goldValue);

            if (wordpairScoresMap.containsKey(key)) {
                Scores scores = wordpairScoresMap.get( key );
                scores.update(measureKey, relatednessValue);
                wordpairScoresMap.put(key, scores);
            }
            else {
                Scores scores = new Scores(measureKey, relatednessValue);
                wordpairScoresMap.put(key, scores);
            }
        }

        public Set<String> getMeasures() {
            return measures;
        }

        public Set<String> getWordpairs() {
            return wordpairScoresMap.keySet();
        }

        public Double getScore(String wordpair, String measure) {
            return wordpairScoresMap.get(wordpair).getValue(measure);
        }

        public Double getGoldValue(String wordpair) {
            return wordpairGoldMap.get(wordpair);
        }

        /**
         * @return The list of gold standard values in key sorted order.
         */
        public List<Double> getGoldList() {
            List<Double> goldList = new ArrayList<Double>();
            for (String wordpair : wordpairGoldMap.keySet()) {
                goldList.add(wordpairGoldMap.get(wordpair));
            }
            return goldList;
        }

        /**
         * @param measure
         * @return The list of scores for the given measure in key sorted order.
         */
        public List<Double> getScoreList(String measure) {
            List<Double> scoreList = new ArrayList<Double>();
            for (String wordpair : wordpairGoldMap.keySet()) {
                scoreList.add(wordpairScoresMap.get(wordpair).getValue(measure));
            }
            return scoreList;
        }

        private String getKeyTerm(String term1, String term2) {
            String key;
            if (term1.compareTo(term2) < 0) {
                key = term1 + SEP + term2;
            }
            else {
                key = term2 + SEP + term1;
            }
            return key;
        }
    }

    private static class Scores {
        private final Map<String,Double> measureScoreMap;

        public Scores(String measureKey, double relatednessValue) {
            measureScoreMap = new TreeMap<String,Double>();
            measureScoreMap.put(measureKey, relatednessValue);
        }

        public void update(String measureKey, double relatednessValue) throws AnalysisEngineProcessException {
            if (measureScoreMap.containsKey(measureKey)) {
                throw new AnalysisEngineProcessException(new Throwable("Score for measure " + measureKey + " already exists. Do you have run the measure twice? Might also be caused by a duplicate word pair (e.g. money-cash and bank-money/money-bank are included twice in the original Fikelstein-353 dataset)."));
            }
            measureScoreMap.put(measureKey, relatednessValue);
        }

        public boolean containsMeasure(String measure) {
            return measureScoreMap.containsKey(measure);
        }

        public Double getValue(String measure) {
            return measureScoreMap.get(measure);
        }
    }

    private Result getFilteredResult(Result result) throws AnalysisEngineProcessException {
        Set<String> wordpairsToFilter = new HashSet<String>();
        for (String wordpair : result.getWordpairs()) {
            for (String measure : result.getMeasures()) {
                if (result.getScore(wordpair, measure) == null) {
                    wordpairsToFilter.add(wordpair);
                }
                else if (result.getScore(wordpair, measure) < 0.0) {
                    wordpairsToFilter.add(wordpair);
                }
            }
        }

        Result filteredResult = new Result();
        for (String wordpair : result.getWordpairs()) {
            if (!wordpairsToFilter.contains(wordpair)) {
                for (String measure : result.getMeasures()) {
                    filteredResult.addScore(wordpair, measure, result.getScore(wordpair, measure), result.getGoldValue(wordpair));
                }
            }
        }

        return filteredResult;
    }

    private String getShortResults(Result result, String measure, String document) {

        StringBuilder sb = new StringBuilder();
        sb.append("RESULT");
        sb.append(SEP);
        sb.append(document);
        sb.append(SEP);
        sb.append(measure);
        sb.append(SEP);

        List<Double> scoreList = result.getScoreList(measure);
        List<Double> goldList = result.getGoldList();
        double pearsonCorrelation = new PearsonsCorrelation().correlation(
                ArrayUtils.toPrimitive(goldList.toArray(new Double[goldList.size()])),
                ArrayUtils.toPrimitive(scoreList.toArray(new Double[goldList.size()]))
        );

        double spearmanCorrelation = new SpearmansCorrelation().correlation(
                ArrayUtils.toPrimitive(goldList.toArray(new Double[goldList.size()])),
                ArrayUtils.toPrimitive(scoreList.toArray(new Double[goldList.size()]))
        );

        sb.append(spearmanCorrelation);
        sb.append(SEP);
        sb.append(pearsonCorrelation);

        return sb.toString();
    }

    private String getCorrelations(Result result) {
        Map<String,Double> pearsonCorrelationMap = new HashMap<String,Double>();
        Map<String,Double> spearmanCorrelationMap = new HashMap<String,Double>();
        List<Double> goldList = result.getGoldList();
        for (String measure : result.getMeasures()) {
            List<Double> scoreList = result.getScoreList(measure);
            double pearsonCorrelation = new PearsonsCorrelation().correlation(
                    ArrayUtils.toPrimitive(goldList.toArray(new Double[goldList.size()])),
                    ArrayUtils.toPrimitive(scoreList.toArray(new Double[goldList.size()]))
            );
            pearsonCorrelationMap.put(measure, pearsonCorrelation);

            double spearmanCorrelation = new SpearmansCorrelation().correlation(
                    ArrayUtils.toPrimitive(goldList.toArray(new Double[goldList.size()])),
                    ArrayUtils.toPrimitive(scoreList.toArray(new Double[goldList.size()]))
            );
            spearmanCorrelationMap.put(measure, spearmanCorrelation);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Pearson: ");
        for (String measure : result.getMeasures()) {
            sb.append(pearsonCorrelationMap.get(measure));
            sb.append(SEP);
        }
        sb.append(LF);
        sb.append("Spearman: ");
        for (String measure : result.getMeasures()) {
            sb.append(spearmanCorrelationMap.get(measure));
            sb.append(SEP);
        }

        return sb.toString();
    }

    private String getMeasures(Result result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Measure: ");
        for (String measure : result.getMeasures()) {
            sb.append(measure);
            sb.append(SEP);
        }

        return sb.toString();
    }
}