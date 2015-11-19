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
package dkpro.similarity.experiments.wordchoice.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;

import dkpro.similarity.experiments.wordchoice.util.WCProblem;
import dkpro.similarity.experiments.wordchoice.util.WordChoiceProblemFactory;
import dkpro.similarity.type.SemanticRelatedness;

/**
 * Outputs evaluation data for word choice problem experiments.
 *
 * @author zesch
 *
 */
public class WordChoiceProblemsEvaluator extends JCasAnnotator_ImplBase {
    public static final double NOT_FOUND = -1.0;

    private static final String LF  = System.getProperty("line.separator");
    private static final String SEP = "---";
    public static final double EPSILON = 0.00001;

    private List<WCProblem> wordChoiceProblems;

    private Map<String,Map<String,Double>> measureToRelatednessValuesMap;

    private Map<String,Boolean> isDistanceMeasureMap;

    
    @Override
    public void initialize(UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);
        
        measureToRelatednessValuesMap = new HashMap<String,Map<String,Double>>();
        wordChoiceProblems = new ArrayList<WCProblem>();
        isDistanceMeasureMap = new HashMap<String,Boolean>();
    }

    @Override
    public void process(JCas jcas)
        throws AnalysisEngineProcessException
    {

        // reading compute the relatedness measures for each available measure
        getContext().getLogger().log(Level.INFO, "Get the precomputed relatedness values ...");
        fillMeasureToRelatednessValueMap(jcas);

        // store whether the measures are distance or relatedness measures
        getContext().getLogger().log(Level.INFO, "Auto-detecting whether a measure is a relatedness or a distance measure ...");
        fillIsDistanceMeasureMap();

        for (String measure : isDistanceMeasureMap.keySet()) {
            getContext().getLogger().log(Level.INFO, measure + " - " + isDistanceMeasureMap.get(measure));
        }

        wordChoiceProblems = WordChoiceProblemFactory.getWordChoiceProblems(jcas);

        // compute the relatedness measures for each available measure
        getContext().getLogger().log(Level.INFO, "Get the target - candidate relatedness values ...");
        for (String measure : measureToRelatednessValuesMap.keySet()) {
            computeCandidateRelatedness(measure);
        }

        getContext().getLogger().log(Level.INFO, "Computing answers ...");
        for (String measure : measureToRelatednessValuesMap.keySet()) {
            computeAnswers(measure);
        }

        for (String measure : measureToRelatednessValuesMap.keySet()) {
            System.out.println(getEvaluationResults(measure, false));
        }


    }

    private void fillMeasureToRelatednessValueMap(JCas jcas) {
        for (SemanticRelatedness sr : JCasUtil.select(jcas, SemanticRelatedness.class)) {
            String measureName = sr.getMeasureName();
            String measureType = sr.getMeasureType();
            String term1 = sr.getTerm1();
            String term2 = sr.getTerm2();
            double relatednessValue = sr.getRelatednessValue();

            String measureKey = measureType + "-" + measureName;

            updateMap(term1, term2, measureKey, relatednessValue);
        }

        // now we have all the relatedness values for all the measures in a map

//      StringBuilder sb = new StringBuilder();
//      for (String measure : measureToRelatednessValuesMap.keySet()) {
//          sb.append(measure); sb.append(LF);
//          for (String term : measureToRelatednessValuesMap.get(measure).keySet()) {
//              sb.append(term);
//              sb.append(" - ");
//              sb.append(measureToRelatednessValuesMap.get(measure).get(term));
//              sb.append(LF);
//          }
//      }
//      sb.append(LF);
//      System.out.println(sb.toString());

    }

    private void fillIsDistanceMeasureMap() {

        // in the word pair annotator, we have introduced at least one word pair where term1 == term2
        // this word pair can be used to test whether the measure is a distance or a relatedness measure
        for (String measure : measureToRelatednessValuesMap.keySet()) {
            double maxValue = Double.MIN_VALUE;
            double indicatorValue = Double.MIN_VALUE;
            for (String concatenatedTerm : measureToRelatednessValuesMap.get(measure).keySet()) {
                double currentValue = measureToRelatednessValuesMap.get(measure).get(concatenatedTerm);

                if (currentValue < 0.0) {
                    continue;
                }

                if (currentValue > maxValue) {
                    maxValue = currentValue;
                }
                String[] terms = concatenatedTerm.split(SEP);
                String term1 = terms[0];
                String term2 = terms[1];

                if (term1.equals(term2) && !isDistanceMeasureMap.containsKey(measure)) {
                    indicatorValue = currentValue;
                }
            }

            if (indicatorValue == Double.MIN_VALUE) {
                getContext().getLogger().log(Level.WARNING, "Cannot determine measure type (relatedness/distance) as no indicator word pair (t1,t1) was found. Defaulting to relatedness value.");
                indicatorValue = maxValue;
            }

            if (Math.abs(indicatorValue - maxValue) < EPSILON) {
                isDistanceMeasureMap.put(measure, false);
            }
            else if (indicatorValue < maxValue) {
                isDistanceMeasureMap.put(measure, true);
            }
            else {
                isDistanceMeasureMap.put(measure, false);
            }
        }
    }

    private void updateMap(String term1, String term2, String measureKey, double relatednessValue) {
        String concatenatedTerm = getConcatenatedString(term1, term2);

        Map<String,Double> stringToRelatednessMap;
        if (!measureToRelatednessValuesMap.containsKey(measureKey)) {
            stringToRelatednessMap = new HashMap<String,Double>();
            measureToRelatednessValuesMap.put(measureKey, stringToRelatednessMap);
        }

        if (measureToRelatednessValuesMap.get(measureKey).containsKey(concatenatedTerm)) {
            getContext().getLogger().log(Level.FINE, "Map already contains value for measure '" + measureKey + "' and string '" + concatenatedTerm);
        }
        else {
            stringToRelatednessMap = measureToRelatednessValuesMap.get(measureKey);
            stringToRelatednessMap.put(concatenatedTerm, relatednessValue);
            measureToRelatednessValuesMap.put(measureKey, stringToRelatednessMap);
        }
    }

    private String getConcatenatedString(String term1, String term2) {
        if (term1.compareTo(term2) < 0) {
            return term1 + SEP + term2;
        }
        else {
            return term2 + SEP + term1;
        }
    }

    /**
     * Computes the semantic relatedness between target and candidates and stores the values in the WCProblem objects.
     * If the semantic relatedness for a multi-word candidate and the target is unknown, we compute the semantic relatedness of its parts.
     */
    private void computeCandidateRelatedness(String measure) {
        for (WCProblem wcp : wordChoiceProblems) {
            wcp.setRelatedness1( measure, getCandidateRelatedness( measure, wcp.getTarget(), wcp.getCand1(), wcp.getCandList1() ) );
            wcp.setRelatedness2( measure, getCandidateRelatedness( measure, wcp.getTarget(), wcp.getCand2(), wcp.getCandList2() ) );
            wcp.setRelatedness3( measure, getCandidateRelatedness( measure, wcp.getTarget(), wcp.getCand3(), wcp.getCandList3() ) );
            wcp.setRelatedness4( measure, getCandidateRelatedness( measure, wcp.getTarget(), wcp.getCand4(), wcp.getCandList4() ) );
        }

    }

    /**
     * Get the relatendess value between the target and the candidate as indicated by the given offsets.
     * Try first to match the full phrase alternative.
     * If no relatedness can be found => fall back to the components of the phrase.
     *
     * @param measure The type of the used measure (similarity || distance).
     *
     * @return The relatendess value between the query and the alternative as indicated by the given offsets.
     */
    private double getCandidateRelatedness(String measure, String target, String phrase, List<String> components) {

        Map<String,Double> relatednessMap = measureToRelatednessValuesMap.get(measure);

        String queryPlusPhrase = getConcatenatedString(target, phrase);
        if (relatednessMap.containsKey(queryPlusPhrase) && relatednessMap.get(queryPlusPhrase) >= 0.0) {
            return relatednessMap.get(queryPlusPhrase);
        }
        else {
            List<Double> values = new ArrayList<Double>();
            for (String component : components) {
                String targetPlusComponent = getConcatenatedString(target, component);
                if (relatednessMap.containsKey(targetPlusComponent)) {
                    values.add(relatednessMap.get(targetPlusComponent));
                }
            }

            // empty lists are handeled in getMaximum and getMinimum
            if (isDistanceMeasure(measure)) {
                // choose minimum distance
                return getMinimum(values);
            }
            else {
                // choose maximum value
                return getMaximum(values);
            }
        }
    }

    private void computeAnswers(String measure) {
        for (WCProblem wcp : wordChoiceProblems) {
            if (isDistanceMeasure(measure)) {
                wcp.setAnswers(measure, getMinimumValues(measure, wcp));
            }
            else {
                wcp.setAnswers(measure, getMaximumValues(measure, wcp));
            }
        }
    }

    private List<Integer> getMinimumValues(String measure, WCProblem wcp) {
        List<Integer> minimumOffsets = new ArrayList<Integer>();

        List<Double> values = new ArrayList<Double>();
        values.add(wcp.getRelatedness1(measure));
        values.add(wcp.getRelatedness2(measure));
        values.add(wcp.getRelatedness3(measure));
        values.add(wcp.getRelatedness4(measure));

        double minimum = getMinimum(values);

        // negative values indicate error conditions
        if (minimum < 0.0) {
            return null;
        }


        if (Math.abs(wcp.getRelatedness1(measure) - minimum) < EPSILON) {
            minimumOffsets.add(1);
        }
        if (Math.abs(wcp.getRelatedness2(measure) - minimum) < EPSILON) {
            minimumOffsets.add(2);
        }
        if (Math.abs(wcp.getRelatedness3(measure) - minimum) < EPSILON) {
            minimumOffsets.add(3);
        }
        if (Math.abs(wcp.getRelatedness4(measure) - minimum) < EPSILON) {
            minimumOffsets.add(4);
        }

        return minimumOffsets;
    }

    private List<Integer> getMaximumValues(String measure, WCProblem wcp) {
        List<Integer> maximumOffsets = new ArrayList<Integer>();

        List<Double> values = new ArrayList<Double>();
        values.add(wcp.getRelatedness1(measure));
        values.add(wcp.getRelatedness2(measure));
        values.add(wcp.getRelatedness3(measure));
        values.add(wcp.getRelatedness4(measure));

        double maximum = getMaximum(values);

        // 0.0 should not be counted for semantic relatedness measures
        if (maximum <= 0.0) {
            return null;
        }

        if (Math.abs(wcp.getRelatedness1(measure) - maximum) < EPSILON) {
            maximumOffsets.add(1);
        }
        if (Math.abs(wcp.getRelatedness2(measure) - maximum) < EPSILON) {
            maximumOffsets.add(2);
        }
        if (Math.abs(wcp.getRelatedness3(measure) - maximum) < EPSILON) {
            maximumOffsets.add(3);
        }
        if (Math.abs(wcp.getRelatedness4(measure) - maximum) < EPSILON) {
            maximumOffsets.add(4);
        }

        return maximumOffsets;
    }

    /**
     * @param measure The name of the measure.
     * @return Returen whether a measure is a distance measure or not.
     */
    private boolean isDistanceMeasure(String measure) {
        return isDistanceMeasureMap.get(measure);
    }

    /**
     * @param valueList A list containing the values.
     * @return Returns the maximum of the values in the given list.
     */
    private double getMaximum(List<Double> valueList) {

        double maximum = NOT_FOUND;

        if (valueList == null) {
            return maximum;
        }

        for (double value : valueList) {
            if (value > maximum) {
                maximum = value;
            }
        }

        // a maximum of 0.0 should not be counted as a real maximum
        if (maximum == 0.0) {
            maximum = -1.0;
        }

        return maximum;
    }

    /**
     * @param valueList A list containing the values.
     * @return Returns the minimum of the values in the given list.
     */
    private double getMinimum(List<Double> valueList) {

        double minimum = Double.POSITIVE_INFINITY;

        if (valueList == null) {
            return minimum;
        }

        for (double value : valueList) {
            if (value >= 0.0 && value < minimum) {
                minimum = value;
            }
        }

        if (minimum == Double.POSITIVE_INFINITY) {
            minimum = -1.0;
        }

        return minimum;
    }


    /**
     * Get a string with the evaluation results for a certain measure.
     * Answered: How many questions could be answered.
     * % correct absolute: absolute percentage of questions correctly answered.
     * % correct relative: relative percentage of questions correctly answered (only the questions that could be answered).
     * Score: A correct answer give a score of 1. If values are tied, partial scores are assigned. 0.5/0.33/0.25 - if 2/3/4 are tied.
     * Queries not found: how many query words could not be found.
     * Words not found: if the query was found, how many words could not be found.
     *
     * @param measure The name of the used measure.
     * @param printProblems If true, problems are printed.
     */
    private String getEvaluationResults(String measure, boolean printProblems) {

        int answered = 0;
        int correct = 0;
        double score = 0;
        int nrofTies = 0;

        int nrOfProblems = wordChoiceProblems.size();

        for (WCProblem wcp : wordChoiceProblems) {
            List<Integer> answers = wcp.getAnswers(measure);
            int correctAnswer = wcp.getCorrect();

            if (answers != null) {
                answered++;
            }
            else {
                continue;
            }

            if (answers.size() > 1) {
                nrofTies++;
            }

            if (isCorrectAnswer(correctAnswer, answers)) {
                correct++;
                score += 1.0 / answers.size();
            }
        }

        StringBuilder sb = new StringBuilder();
        if (printProblems) {
            sb.append( getFormattedProblems(measure) );
        }

        double coverage = (double) answered / nrOfProblems;
        double correctAbsRatio = (double) correct / nrOfProblems;
        double correctRelRatio = (double) correct / answered;
        double scoreAbsRatio = score / nrOfProblems;
        double accuracy = score / answered;
        double tieRatio = (double) nrofTies / answered;



        sb.append(LF);
        sb.append("Measure:      " + measure); sb.append(LF);
        sb.append("Answered:     " + answered + "/" + nrOfProblems + " (" +  String.format("%.3f",coverage) + "%)"); sb.append(LF);
        sb.append("Correct(abs): " + correct + "/" + nrOfProblems + " (" +  String.format("%.3f",correctAbsRatio) + "%)"); sb.append(LF);
        sb.append("Correct(rel): " + correct + "/" + answered + " (" +  String.format("%.3f",correctRelRatio) + "%)"); sb.append(LF);
        sb.append("Score(abs):   " + String.format("%.2f",score) + "/" + nrOfProblems + " (" +  String.format("%.3f",scoreAbsRatio) + "%)"); sb.append(LF);
        sb.append("Score(rel):   " + String.format("%.2f",score) + "/" + answered + " (" +  String.format("%.3f",accuracy) + "%)"); sb.append(LF);
        sb.append("# Ties:       " + nrofTies + "/" + answered + " (" +  String.format("%.3f",tieRatio) + "%)"); sb.append(LF);
        sb.append(LF);

        sb.append("Accuracy: " + String.format("%.2f",score) + "/" + answered + " (" +  String.format("%.3f",accuracy) + "%)"); sb.append(LF);
        sb.append("Coverage: " + answered + "/" + nrOfProblems + " (" +  String.format("%.3f",coverage) + "%)"); sb.append(LF);

        return sb.toString();
    }

    /**
     * An answer is correctly answered, if the correct answer number is in the list of answers numbers.
     * @param correct The correct answer number.
     * @param answers The list of answers.
     * @return True if the answer is correct, false otherwise.
     */
    private boolean isCorrectAnswer(int correct, List<Integer> answers) {
        for (int answer : answers) {
            if (answer == correct) {
                return true;
            }
        }
        return false;
    }


    /**
     * Get a string with the target, the candidates, and the computed values.
     * @param answerValues
     * @return
     */
    private String getFormattedProblems(String measure) {
        StringBuilder sb = new StringBuilder();

        sb.append("Target:Cand1:Cand2:Cand3:Cand4:Value1:Value2:Value3:Value4:Answer(s):Correct");
        sb.append(LF);

        for (WCProblem wcp : wordChoiceProblems) {
            sb.append(wcp.getFormattedString(measure));
            sb.append(LF);
        }
        return sb.toString();
    }
}