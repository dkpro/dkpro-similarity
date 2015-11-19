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
/**
 * More details
 * http://illya-keeplearning.blogspot.com/search/label/suffix%20tree
 */
package dkpro.similarity.algorithms.lexical.string.util.gst;

import java.util.*;

public class GeneralizedSuffixTree {
    // SOH - START OF HEADING
    public static final char TERMINATOR1 = '\u0001';
    // STX - START OF TEXT
    public static final char TERMINATOR2 = '\u0002';

    // unallocated Unicode space
    public static final char TERMINATORS_RANGE = '\ud800';

    private String texts[];
    private char terminators[];

    private String generalized;
    private SuffixTree suffixTree;

    public GeneralizedSuffixTree(String text1, String text2) {
        this(text1, text2, TERMINATOR1, TERMINATOR2);
    }

    public GeneralizedSuffixTree(String text1, String text2, char terminator1, char terminator2) {
        this(new String[]{text1, text2}, new char[]{terminator1, terminator2});
    }

    public GeneralizedSuffixTree(String texts[]) {
        this(texts, getDefaultTerminators(texts.length));
    }

    public GeneralizedSuffixTree(String texts[], char terminators[]) {
        this.texts = texts;
        this.terminators = terminators;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            sb.append(texts[i]);
            sb.append(terminators[i]);
        }
        generalized = sb.toString();
        suffixTree = new SuffixTree(generalized);
        fixSpanSuffixes(suffixTree.getRootNode());
    }

    private void fixSpanSuffixes(Node node) {
        for (Edge edge : node.getEdges()) {
            for (int i = 0; i < texts.length; i++) {
                if ((edge.getBeginIndex() <= getGeneralizedSubstringLength(i)) &&
                        (edge.getEndIndex() > getGeneralizedSubstringLength(i))) {

                    edge.setEndIndex(getGeneralizedSubstringLength(i));
                    continue;
                }
            }
            fixSpanSuffixes(edge.getEndNode());
        }
    }

    private int getGeneralizedSubstringLength(int n) {
        int length = 0;
        for (int i = 0; i <= n; i++) {
            length += texts[i].length() + 1;
        }
        return length - 1;
    }

    public String getLcsAsString() {
        CommonSubstr commonSubstr = getLcs();
        
        try
        {        
        	return texts[0].substring(commonSubstr.beginIndexes[0], commonSubstr.endIndexes[0] + 1);
        }
        catch (Exception e)
        {
        	return "";
        }
    }

    public CommonSubstr getLcs() {
        int beginIndexes[] = new int[texts.length];
        int endIndexes[] = new int[texts.length];
        initBeginEndIndexes(beginIndexes, endIndexes);
        return getLcs(beginIndexes, endIndexes);
    }

    private void initBeginEndIndexes(int beginIndexes[], int endIndexes[]) {
        beginIndexes[0] = 0;
        endIndexes[0] = texts[0].length();

        for(int i=1; i<texts.length; i++) {
            beginIndexes[i] = endIndexes[i-1]+1;
            endIndexes[i] = beginIndexes[i]+texts[i].length();
        }
    }

    public int[] getDefaultBeginIndexes() {
        int beginIndexes[] = new int[texts.length];
        initBeginEndIndexes(beginIndexes, new int[texts.length]);
        return beginIndexes;
    }

    public int[] getDefaultEndIndexes() {
        int endIndexes[] = new int[texts.length];
        initBeginEndIndexes(new int[texts.length], endIndexes);
        return endIndexes;
    }

    public static char[] getDefaultTerminators(int length) {
        char terminators[] = new char[length];
        for(int i=0; i<length; i++)
            terminators[i] = (char)(TERMINATORS_RANGE + i);
        return terminators;
    }

    public int[] incIndexes(int indexes[]) {
        for(int i=0; i<texts.length; i++)
            indexes[i]++;
        return indexes;
    }

    public CommonSubstr getLcs(int beginIndexes[], int endIndexes[]) {
        // calculate LCS status for each node: strings count, suffix length
        Map<Node, LCSNodeStatus> statuses = new HashMap<Node, LCSNodeStatus>();
        getLCSNodeStatus(suffixTree.getRootNode(), 0, statuses);

        CommonSubstr commonSubstr = getLcs(beginIndexes, endIndexes, statuses);
        statuses.clear();

        return commonSubstr;
    }

    private CommonSubstr getLcs(int beginIndexes[], int endIndexes[], Map<Node, LCSNodeStatus> statuses) {
        int max = 0;
        int foundBeginIndexes[] = null;

        for (LCSNodeStatus status : statuses.values()) {
            if ((status.getHeight() > 0) && (status.isAllStrings()) && (max <= status.getHeight())) {
                Node node = status.getNode();
                int workingBeginIndexes[] = initFoundBeginIndexes();

                updateFoundBeginIndexes(beginIndexes, endIndexes, node, status.getHeight(),
                        statuses, workingBeginIndexes);

                if (verifyFoundBeginIndexes(workingBeginIndexes)) {
                    foundBeginIndexes = workingBeginIndexes;
                    max = status.getHeight();
                }
            }
        }

        if (foundBeginIndexes == null)
            return null;

        return new CommonSubstr(foundBeginIndexes, max);
    }

    private int[] initFoundBeginIndexes() {
        int beginIndexes[] = new int[texts.length];
        for(int i=0; i<texts.length; i++)
            beginIndexes[i] = Integer.MAX_VALUE;
        return beginIndexes;
    }

    private void updateFoundBeginIndexes(int beginIndexes[], int endIndexes[], Node node, int height,
                                            Map<Node, LCSNodeStatus> statuses, int[] foundBeginIndexes) {
        for (Edge edge : node.getEdges()) {
            LCSNodeStatus nodeStatus = statuses.get(edge.getEndNode());
            if ((nodeStatus != null) && nodeStatus.isAllStrings()) {
                updateFoundBeginIndexes(beginIndexes, endIndexes, edge.getEndNode(),
                            height + getEdgeHeight(edge), statuses, foundBeginIndexes);
            } else {
                int stringNumber = getEdgeStringNumber(edge);
                int beginIndex = edge.getBeginIndex() - height;

                if ((beginIndex < endIndexes[stringNumber]) &&
                        (beginIndex >= beginIndexes[stringNumber]) &&
                        (foundBeginIndexes[stringNumber] > beginIndex)) {

                    foundBeginIndexes[stringNumber] = beginIndex;
                }
            }
        }
    }

    private boolean verifyFoundBeginIndexes(int[] beginIndexes) {
        for(int i=0; i<texts.length; i++)
            if (beginIndexes[i] == Integer.MAX_VALUE)
                return false;
        return true;
    }

    public List<CommonSubstr> diff() {
        // calculate LCS status for each node: strings count, suffix length
        Map<Node, LCSNodeStatus> statuses = new HashMap<Node, LCSNodeStatus>();
        getLCSNodeStatus(suffixTree.getRootNode(), 0, statuses);

        List<CommonSubstr> list = diff(getDefaultBeginIndexes(), getDefaultEndIndexes(), statuses);
        statuses.clear();

        return list;
    }

    private List<CommonSubstr> diff(int beginIndexes[], int endIndexes[], Map<Node, LCSNodeStatus> statuses) {
        CommonSubstr commonSubstr = getLcs(beginIndexes, endIndexes, statuses);
        if (commonSubstr == null)
            return new LinkedList<CommonSubstr>();

        List<CommonSubstr> prev = diff(beginIndexes, commonSubstr.beginIndexes, statuses);
        List<CommonSubstr> next = diff(incIndexes(commonSubstr.endIndexes), endIndexes, statuses);

        prev.add(commonSubstr);
        if (next != null)
            prev.addAll(next);
        return prev;
    }

    private int getEdgeStringNumber(Edge edge) {
        for (int i = 0; i < texts.length; i++) {
            if (edge.getEndIndex() <= getGeneralizedSubstringLength(i))
                return i;
        }
        return -1;
    }

    private int getEdgeHeight(Edge edge) {
        return edge.getEndIndex() - edge.getBeginIndex();
    }

    private int getEdgeHeightForNodeStatus(Edge edge) {
        int result = getEdgeHeight(edge);

        int stringNumber = getEdgeStringNumber(edge);
        if (edge.getEndIndex() != getGeneralizedSubstringLength(stringNumber))
            result += 1;
        return result;
    }

    private LCSNodeStatus getLCSNodeStatus(Node node, int height, Map<Node, LCSNodeStatus> statuses) {
        LCSNodeStatus nodeStatus = new LCSNodeStatus(node, height);
        if (node.getEdges().size() == 0) {
            return nodeStatus;
        }

        for (Edge edge : node.getEdges()) {
            LCSNodeStatus status = getLCSNodeStatus(edge.getEndNode(),
                    height + getEdgeHeightForNodeStatus(edge), statuses);

            status.addString(getEdgeStringNumber(edge));
            nodeStatus.mergeStatus(status);
        }
        statuses.put(node, nodeStatus);
        return nodeStatus;
    }

    public String getGeneralizedString() {
        return generalized;
    }

    public SuffixTree getSuffixTree() {
        return suffixTree;
    }


    /**
     * Suffix Tree Node Status for Longest Common Substring (LCS)
     */
    private class LCSNodeStatus {
        private Node node;
        private boolean allStrings;
        private Set<Integer> stringSet;
        private int height;

        public LCSNodeStatus(Node node, int height) {
            this.node = node;
            allStrings = false;
            stringSet = new HashSet<Integer>();

            this.height = height;
        }

        public boolean isAllStrings() {
            if (!allStrings) {
                for (int i = 0; i < texts.length; i++) {
                    if (!stringSet.contains(i))
                        return false;
                }
                allStrings = true;
            }
            return allStrings;
        }

        public void addString(int number) {
            if (!isAllStrings()) {
                if (!stringSet.contains(number))
                    stringSet.add(number);
            }
        }

        public int getHeight() {
            return height;
        }

        public Node getNode() {
            return node;
        }

        public void mergeStatus(LCSNodeStatus status) {
            allStrings |= status.allStrings;
            if (!isAllStrings()) {
                stringSet.addAll(status.stringSet);
            }
        }
    }


    public class CommonSubstr {
        private int beginIndexes[];
        private int endIndexes[];

        public CommonSubstr(int beginIndexes[], int endIndexes[]) {
            this.beginIndexes = beginIndexes;
            this.endIndexes = endIndexes;
        }

        public CommonSubstr(int beginIndexes[], int max) {
            this.beginIndexes = beginIndexes;
            endIndexes = new int[texts.length];
            for(int i=0; i<texts.length; i++) {
                endIndexes[i] = beginIndexes[i] + max - 1;
            }
        }

        public int[] getBeginIndexes() {
            return beginIndexes;
        }

        public int[] getEndIndexes() {
            return endIndexes;
        }
    }
}
