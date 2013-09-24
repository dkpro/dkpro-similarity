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
package dkpro.similarity.algorithms.lexical.string;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import dkpro.similarity.algorithms.api.SimilarityException;
import dkpro.similarity.algorithms.api.TextSimilarityMeasureBase;

/**
 * Algorithm taken from
 * Michael J. Wise. 1996. Yap3: Improved detection of similarities in computer
 * programs and other texts. In Proceedings of SIGCSE '96, pages 130-134. 
 * 
 * The algorithm was also described here:
 * Lutz Prechelt, Guido Malpohl, and Michael Philippsen. Finding plagiarisms among a
 * set of programs with JPlag. In Journal of Universal Computer Science, 8(11):10161038,
 * November 2002.
 * 
 * Normalization is done according to the strategy described here:
 * P. Clough, R. Gaizauskas, S.S.L. Piao, and Y. Wilks. 2002. In Proceedings of the
 * 40th Annual Meeting of the ACL, pages 152-159.
 */
public class GreedyStringTiling
	extends TextSimilarityMeasureBase
{
	int minMatchLength;
	
	public GreedyStringTiling(int minMatchLength)
	{
		this.minMatchLength = minMatchLength;
	}
	
	@Override
	public double getSimilarity(Collection<String> stringList1,
			Collection<String> stringList2)
		throws SimilarityException
	{
	    return getSimilarity(StringUtils.join(stringList1, " "), StringUtils.join(stringList2, " "));
	}
	
	/**
	 * Considers both parameters to be full texts, not individual terms.
	 * @parameter string1 The first string is considered to be the suspicious
	 * document.
	 */
	@Override
	public double getSimilarity(String string1, String string2)
		throws SimilarityException
	{
		MarkedString s1 = new MarkedString(string1);
		MarkedString s2 = new MarkedString(string2);
		
		TileCollection finalTiles = new TileCollection(s1, s2);
		
		int maxmatch = Integer.MAX_VALUE;
		while (maxmatch > minMatchLength)
		{
			maxmatch = minMatchLength;
			
			TileCollection tiles = new TileCollection(s1, s2);
			
			for (int i = s1.getFirstUnmarkedIndex(); i < s1.length(); i++)
			{
				for (int j = s2.getFirstUnmarkedIndex(); j < s2.length(); j++)
				{
					if (i == -1 || j == -1)
						break;
					
					int k = 0;
					while ((i + k) < s1.length() && (j + k) < s2.length() &&
						   s1.characterAtIndex(i + k).toLowerCase().equals(s2.characterAtIndex(j + k).toLowerCase()) && 
						   s1.isUnmarked(i + k) && s2.isUnmarked(j + k))
					{
						k++;
					}
					
					if (k >= maxmatch)
					{
						// We don't need to decide between k > maxmatch and
						// k == maxmatch, because TileCollection takes care of this.
						
						tiles.addTile(i, j, k);
						maxmatch = k;
					}
				}
			}
			
			for (Tile t : tiles.getTiles(maxmatch))
			{
				// Check if occluded
				boolean occluded = false;
				for (int i = 0; i < maxmatch; i++)
				{
					if (s1.isMarked(t.getIndex1() + i) ||
						s2.isMarked(t.getIndex2() + i))
						occluded = true;
						
				}
				
				if (!occluded)
				{
					for (int i = 0; i < maxmatch; i++)
					{
						s1.mark(t.getIndex1() + i);
						s2.mark(t.getIndex2() + i);
					}
					finalTiles.addTile(t.getIndex1(), t.getIndex2(), t.getLength());
				}				
			}
		}
		
//		// -- DEBUG --
//		List<Integer> startIndexes = new ArrayList<Integer>();
//		for (Tile t : finalTiles.getTiles())
//			startIndexes.add(t.getIndex1());
//			
//		System.out.println(s1.toString(startIndexes));
//		
//		System.out.println("--");
//		
//		startIndexes = new ArrayList<Integer>();
//		for (Tile t : finalTiles.getTiles())
//			startIndexes.add(t.getIndex2());
//		
//		System.out.println(s2.toString(startIndexes));
		
		double numerator = s1.getNumberOfMarkedCharacters();
		double denominator = s1.length(); 
		double score = numerator / denominator;
		
		return score;
	}
	
	@Override
	public String getName()
	{
		return this.getClass().getSimpleName() + "_" + minMatchLength;
	}
	
	
	private class MarkedString
	{	
		private String string;
		private boolean[] marker;
		
		public MarkedString(String string)
		{
			this.string = string;
			this.marker = new boolean[string.length()];
			
			for (int i = 0; i < marker.length; i++)
				marker[i] = false;
		}
		
		public void mark(int index)
		{
			marker[index] = true;
		}
		
		public void unmark(int index)
		{
			marker[index] = false;
		}
		
		public boolean isMarked(int index)
		{
			return marker[index] == true;
		}		
		
		public boolean isUnmarked(int index)
		{
			return marker[index] == false;
		}
		
		public boolean markAtIndex(int index)
		{
			return marker[index];
		}
		
		public int getFirstUnmarkedIndex()
		{
			for (int i = 0; i < marker.length; i++)
			{
				if (marker[i] == false)
					return i;
			}
			return -1;
		}
		
		public String characterAtIndex(int index)
		{
			return string.substring(index, index + 1);
		}

		public String getString()
		{
			return string;
		}
		
		public int length()
		{
			return string.length();
		}
		
		public int getNumberOfMarkedCharacters()
		{
			int count = 0;
			for (int i = 0; i < marker.length; i++)
			{
				if (marker[i] == true)
					count++;
			}
			return count;
		}
		
		public String toString(List<Integer> startIndexes)
		{
			StringBuilder sb = new StringBuilder();
			
			boolean markOpen = false;
			for (int i = 0; i < string.length(); i++)
			{
				if ((isMarked(i) && markOpen))
				{
					// This might be the end of token 1 and the start of token 2,
					// not a single contiguous token.
					
					boolean startOfTile = false;
					for (Integer startIndex : startIndexes)
					{
						if (startIndex.equals(i))
							startOfTile = true;
					}
					
					if (startOfTile)
						sb.append("][" + characterAtIndex(i));
					else
						sb.append(characterAtIndex(i));
				} else if (isMarked(i) && !markOpen)
				{
					sb.append("[" + characterAtIndex(i));
					markOpen = true;
				} else if (isUnmarked(i) && markOpen)
				{
					sb.append("]" + characterAtIndex(i));
					markOpen = false;
				} else if (isUnmarked(i) && !markOpen) {
					sb.append(characterAtIndex(i));
				}
			}
			
			return sb.toString();
		}
	}
	
	private class TileCollection
	{
		MarkedString string1;
		MarkedString string2;
		
		Map<Integer,List<Tile>> tiles = new HashMap<Integer,List<Tile>>();
		
		public TileCollection(MarkedString string1, MarkedString string2)
		{
			this.string1 = string1;
			this.string2 = string2;
		}
		
		public void addTile(int index1, int index2, int length)
		{
			List<Tile> tilesOfGivenLength;
			if (tiles.containsKey(length))
				tilesOfGivenLength = tiles.get(length);
			else
				tilesOfGivenLength = new ArrayList<Tile>();
			
			tilesOfGivenLength.add(new Tile(index1, index2, length));
			
			tiles.put(length, tilesOfGivenLength);
		}
		
		public List<Tile> getTiles()
		{
			List<Tile> allTiles = new ArrayList<Tile>();
			for (Integer i : tiles.keySet())
			{
				allTiles.addAll(tiles.get(i));
			}
			return allTiles;				
		}
		
		public List<Tile> getTiles(int length)
		{
			if (!tiles.containsKey(length))
				return new ArrayList<Tile>();
			
			return tiles.get(length);
		}

		public MarkedString getString1()
		{
			return string1;
		}

		public MarkedString getString2()
		{
			return string2;
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			
			for (Tile t : getTiles())
			{
				sb.append(t.getIndex1() + " [");
				for (int i = 0; i < t.length; i++)
					sb.append(string1.characterAtIndex(t.getIndex1() + i));
				sb.append("] ** ");
				
				sb.append(t.getIndex2() + " [");
				for (int i = 0; i < t.length; i++)
					sb.append(string2.characterAtIndex(t.getIndex2() + i));
				sb.append("]");
				
				sb.append("\n");
			}
			sb.append("\n");
			
			return sb.toString();
		}
	}
	
	private class Tile
	{
		int index1, index2;
		int length;
		
		public Tile(int index1, int index2, int length)
		{
			this.index1 = index1;
			this.index2 = index2;
			this.length = length;
		}

		public int getIndex1()
		{
			return index1;
		}

		public int getIndex2()
		{
			return index2;
		}

		public int getLength()
		{
			return length;
		}
	}
}
