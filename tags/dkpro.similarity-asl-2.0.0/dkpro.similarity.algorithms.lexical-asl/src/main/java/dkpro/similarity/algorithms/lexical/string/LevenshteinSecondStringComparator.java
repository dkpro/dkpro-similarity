/*******************************************************************************
 * Copyright 2012
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 *******************************************************************************/
package dkpro.similarity.algorithms.lexical.string;

import com.wcohen.ss.Level2Levenstein;
import com.wcohen.ss.Levenstein;

public class LevenshteinSecondStringComparator
    extends SecondStringComparator_ImplBase
{
    public LevenshteinSecondStringComparator()
    {
            secondStringMeasureL1 = new Levenstein();
            secondStringMeasureL2 = new Level2Levenstein();
    }
}