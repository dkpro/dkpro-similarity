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
package dkpro.similarity.uima.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import dkpro.similarity.uima.io.util.CombinationPair;


/**
 * Abstract reader which allows to combine the texts of two views within a
 * JCas in a number of ways, e.g. each text with every other one. This class
 * serves as the base for any concrete reader that only have to overwrite the
 * {@link #getAlignedPairs()} method.
 */
public abstract class CombinationReader
	extends CasCollectionReader_ImplBase
{
    public static final String PARAM_LANGUAGE = "Language";
    @ConfigurationParameter(name=PARAM_LANGUAGE, mandatory=true)
    private String language;
    
	public static final String PARAM_COMBINATION_STRATEGY = "CombinationStrategy";
	@ConfigurationParameter(name=PARAM_COMBINATION_STRATEGY, mandatory=true)
	private CombinationStrategy combinationStrategy;

	public static final String PARAM_NUMBER_OF_OTHER_ROWS = "NumberOfOtherRows";
	@ConfigurationParameter(name=PARAM_NUMBER_OF_OTHER_ROWS, mandatory=false)
	private int numberOfOtherRows;

	public enum CombinationStrategy
	{
		SAME_ROW_ONLY,
		ALL_ROWS,
		EACH_COMBINATION_ONCE,
		EACH_COMBINATION_ONCE_WITHOUT_SELF,
		EACH_ROW_WITH_EVERY_OTHER_DISTINCT_ROW,
		EACH_ROW_WITH_GIVEN_NUMER_OF_RANDOM_OTHER_ROWS
	}

	public static final String INITIAL_VIEW		= "_InitialView";
	public static final String VIEW_1 			= "View1";
	public static final String VIEW_2 			= "View2";

	private int index = 0;
	private int firstPairIndex = 0;
	private int secondPairIndex = 0;
	private int numberOfCombinations;
	private boolean alignedPairDone = false;

	private Set<Integer> otherRowIndexes = new HashSet<Integer>();

	private List<CombinationPair> alignedPairs = new ArrayList<CombinationPair>();
	private List<CombinationPair> secondIndexPairs = new ArrayList<CombinationPair>();
	
	protected CombinationPair currentPair1;
    protected CombinationPair currentPair2;

	protected Random randomGenerator = new Random();
	

	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);
		
		this.currentPair1 = null;
        this.currentPair2 = null;
		
		// Read pairs
		alignedPairs = getAlignedPairs();
		if (combinationStrategy.equals(CombinationStrategy.EACH_ROW_WITH_EVERY_OTHER_DISTINCT_ROW)) {
			secondIndexPairs = getDistinctPairs(alignedPairs);
		}
		else {
			secondIndexPairs = alignedPairs;
		}

		// Initialize combinations and indexes
		switch (combinationStrategy)
		{
			case SAME_ROW_ONLY:
				numberOfCombinations = alignedPairs.size();
				break;
			case ALL_ROWS:
			case EACH_ROW_WITH_EVERY_OTHER_DISTINCT_ROW:
				numberOfCombinations = alignedPairs.size() * secondIndexPairs.size();
				break;
			case EACH_ROW_WITH_GIVEN_NUMER_OF_RANDOM_OTHER_ROWS:
				numberOfCombinations = alignedPairs.size() * numberOfOtherRows;
				break;
			case EACH_COMBINATION_ONCE:
				numberOfCombinations = 0;
				for (int i = alignedPairs.size(); i > 0; i--) {
					numberOfCombinations += i;
				}
				break;
			case EACH_COMBINATION_ONCE_WITHOUT_SELF:
				numberOfCombinations = 0;
				for (int i = alignedPairs.size(); i > 0; i--) {
					numberOfCombinations += i;
				}
				numberOfCombinations -= alignedPairs.size();
				secondPairIndex = 1;
				break;
		}
	}

	public abstract List<CombinationPair> getAlignedPairs() throws ResourceInitializationException;

	@Override
	public void getNext(CAS aCAS)
		throws IOException, CollectionException
	{
		try {
			JCas jcas = aCAS.getJCas();

			JCas view1 = jcas.createView(VIEW_1);
			JCas view2 = jcas.createView(VIEW_2);

			if (combinationStrategy.equals(CombinationStrategy.SAME_ROW_ONLY))
			{
				firstPairIndex = index;
				secondPairIndex = index;
			}
			else if (combinationStrategy.equals(CombinationStrategy.EACH_ROW_WITH_GIVEN_NUMER_OF_RANDOM_OTHER_ROWS))
			{
				if (!alignedPairDone)
				{
					secondPairIndex = firstPairIndex;
					alignedPairDone = true;
				}
				else
				{
					secondPairIndex = -1;

					while (secondPairIndex == -1 || otherRowIndexes.contains(secondPairIndex)) {
						secondPairIndex = randomGenerator.nextInt(secondIndexPairs.size());
					}
				}

				otherRowIndexes.add(secondPairIndex);	// Do not process any combination twice
			}

			// Get item 1
			currentPair1 = alignedPairs.get(firstPairIndex);

			// Prepare view 1
			view1.setDocumentText(currentPair1.getText1());
			view1.setDocumentLanguage(language);

			DocumentMetaData md1 = DocumentMetaData.create(view1);
			md1.setCollectionId(currentPair1.getCollectionID());
			md1.setDocumentId(currentPair1.getID1());

			// Get item 2
			currentPair2 = secondIndexPairs.get(secondPairIndex);

			// Prepare view 2
			view2.setDocumentText(currentPair2.getText2());
            view2.setDocumentLanguage(language);

			DocumentMetaData md2 = DocumentMetaData.create(view2);
			md2.setCollectionId(currentPair2.getCollectionID());
			md2.setDocumentId(currentPair2.getID2());
			
			
			// add DMD also to base cas - required for many writers
			DocumentMetaData dmd = DocumentMetaData.create(jcas);
            dmd.setCollectionId(currentPair1.getCollectionID());
            dmd.setDocumentId(currentPair1.getID1() + "-" + currentPair2.getID2());
		}
		catch (CASException e) {
			throw new CollectionException(e);
		}

		// Move on
		index++;

		if (combinationStrategy.equals(CombinationStrategy.EACH_ROW_WITH_GIVEN_NUMER_OF_RANDOM_OTHER_ROWS))
		{
			if (otherRowIndexes.size() >= numberOfOtherRows)
			{
				firstPairIndex++;
				otherRowIndexes.clear();
				alignedPairDone = false;
			}
		}
		else if (combinationStrategy.equals(CombinationStrategy.EACH_COMBINATION_ONCE))
		{
			secondPairIndex++;
			if (secondPairIndex >= secondIndexPairs.size())
			{
				firstPairIndex++;
				secondPairIndex = firstPairIndex;
			}
		}
		else if (combinationStrategy.equals(CombinationStrategy.EACH_COMBINATION_ONCE_WITHOUT_SELF))
		{
			secondPairIndex++;
			if (secondPairIndex >= secondIndexPairs.size())
			{
				firstPairIndex++;
				secondPairIndex = firstPairIndex + 1;
			}
		}
		else
		{
			secondPairIndex++;
			if (secondPairIndex >= secondIndexPairs.size())
			{
				firstPairIndex++;
				secondPairIndex = 0;
				alignedPairDone = false;
			}
		}
	}

	private List<CombinationPair> getDistinctPairs(List<CombinationPair> pairs)
	{
		List<CombinationPair> distinctPairs = new ArrayList<CombinationPair>();

		Set<String> doneIDs = new HashSet<String>();

		for (CombinationPair pair : pairs)
		{
			if (!doneIDs.contains(pair.getID2()))
			{
				distinctPairs.add(pair);
				doneIDs.add(pair.getID2());
			}
		}

		return distinctPairs;
	}

	@Override
	public void close()
		throws IOException
	{
		// Nothing yet
	}

	@Override
	public Progress[] getProgress()
	{
		return new Progress[]{new ProgressImpl(index + 1, numberOfCombinations, Progress.ENTITIES)};
	}

	@Override
	public boolean hasNext()
		throws IOException, CollectionException
	{
		return index < numberOfCombinations;
	}

    protected int getIndex()
    {
        return index;
    }

    protected int getNumberOfCombinations()
    {
        return numberOfCombinations;
    }
}
