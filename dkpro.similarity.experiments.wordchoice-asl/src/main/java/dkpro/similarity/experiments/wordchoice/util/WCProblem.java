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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class WCProblem {

    private String target;
    private String cand1;
    private String cand2;
    private String cand3;
    private String cand4;
    
    // alternative representation with phrases split into lists of tokens
    private List<String> candList1;
    private List<String> candList2;
    private List<String> candList3;
    private List<String> candList4;

    private Map<String,Double> relatedness1;
    private Map<String,Double> relatedness2;
    private Map<String,Double> relatedness3;
    private Map<String,Double> relatedness4;
    
    private Map<String,List<Integer>> answers;  // due to tie there can be multiple answers

    public int correct;

    public WCProblem(String target, String cand1, String cand2, String cand3, String cand4, List<String> candList1, List<String> candList2,
            List<String> candList3, List<String> candList4, int correct) {
        super();
        this.target = target;
        this.cand1 = cand1;
        this.cand2 = cand2;
        this.cand3 = cand3;
        this.cand4 = cand4;
        this.candList1 = candList1;
        this.candList2 = candList2;
        this.candList3 = candList3;
        this.candList4 = candList4;
        this.correct = correct;

        this.relatedness1 = new HashMap<String,Double>();
        this.relatedness2 = new HashMap<String,Double>();
        this.relatedness3 = new HashMap<String,Double>();
        this.relatedness4 = new HashMap<String,Double>();
        
        this.answers = new HashMap<String,List<Integer>>();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append(target);
        sb.append(" -- ");
        sb.append(correct);
        sb.append(" -- ");
        sb.append(cand1);
        sb.append("/");
        sb.append(cand2);
        sb.append("/");
        sb.append(cand3);
        sb.append("/");
        sb.append(cand4);
        sb.append(" -- ");
        sb.append(StringUtils.join(candList1, "+"));
        sb.append("/");
        sb.append(StringUtils.join(candList2, "+"));
        sb.append("/");
        sb.append(StringUtils.join(candList3, "+"));
        sb.append("/");
        sb.append(StringUtils.join(candList4, "+"));
        sb.append(" -- ");
        sb.append(relatedness1);
        sb.append(":");
        sb.append(relatedness2);
        sb.append(":");
        sb.append(relatedness3);
        sb.append(":");
        sb.append(relatedness4);
        return sb.toString();
    }

    public String getFormattedString(String measure) {
        StringBuilder sb = new StringBuilder(500);
        sb.append(target);
        sb.append(":");
        sb.append(cand1);
        sb.append(":");
        sb.append(cand2);
        sb.append(":");
        sb.append(cand3);
        sb.append(":");
        sb.append(cand4);
        sb.append(":");
        sb.append(relatedness1.get(measure));
        sb.append(":");
        sb.append(relatedness2.get(measure));
        sb.append(":");
        sb.append(relatedness3.get(measure));
        sb.append(":");
        sb.append(relatedness4.get(measure));
        sb.append(":");
        sb.append(StringUtils.join(answers.get(measure), ","));
        sb.append(":");
        sb.append(correct);
        return sb.toString();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCand1() {
        return cand1;
    }

    public void setCand1(String cand1) {
        this.cand1 = cand1;
    }

    public String getCand2() {
        return cand2;
    }

    public void setCand2(String cand2) {
        this.cand2 = cand2;
    }

    public String getCand3() {
        return cand3;
    }

    public void setCand3(String cand3) {
        this.cand3 = cand3;
    }

    public String getCand4() {
        return cand4;
    }

    public void setCand4(String cand4) {
        this.cand4 = cand4;
    }

    public List<String> getCandList1() {
        return candList1;
    }

    public void setCandList1(List<String> candList1) {
        this.candList1 = candList1;
    }

    public List<String> getCandList2() {
        return candList2;
    }

    public void setCandList2(List<String> candList2) {
        this.candList2 = candList2;
    }

    public List<String> getCandList3() {
        return candList3;
    }

    public void setCandList3(List<String> candList3) {
        this.candList3 = candList3;
    }

    public List<String> getCandList4() {
        return candList4;
    }

    public void setCandList4(List<String> candList4) {
        this.candList4 = candList4;
    }

    public double getRelatedness1(String measureType) {
        return relatedness1.get(measureType);
    }

    public void setRelatedness1(String measureType, double relatedness1) {
        this.relatedness1.put(measureType, relatedness1);
    }

    public double getRelatedness2(String measureType) {
        return relatedness2.get(measureType);
    }

    public void setRelatedness2(String measureType, double relatedness1) {
        this.relatedness2.put(measureType, relatedness1);
    }

    public double getRelatedness3(String measureType) {
        return relatedness3.get(measureType);
    }

    public void setRelatedness3(String measureType, double relatedness1) {
        this.relatedness3.put(measureType, relatedness1);
    }

    public double getRelatedness4(String measureType) {
        return relatedness4.get(measureType);
    }

    public void setRelatedness4(String measureType, double relatedness1) {
        this.relatedness4.put(measureType, relatedness1);
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public List<Integer> getAnswers(String measureType) {
        return answers.get(measureType);
    }

    public void setAnswers(String measureType, List<Integer> answers) {
        this.answers.put(measureType, answers);
    }
}