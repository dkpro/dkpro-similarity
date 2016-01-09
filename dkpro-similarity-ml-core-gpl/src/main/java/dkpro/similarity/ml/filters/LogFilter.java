/**
 * Copyright 2012-2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package dkpro.similarity.ml.filters;

import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Capabilities.Capability;
import weka.filters.SimpleStreamFilter;

/**
 * Applies a log-transformation on the input scores.
 */
public class LogFilter
	extends SimpleStreamFilter
{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected Instances determineOutputFormat(Instances inst)
		throws Exception
	{
		// Filter leaves the instance format unchanged
		return inst;
	}

	@Override
	protected Instance process(Instance inst)
		throws Exception
	{
	     Instance newInst = new DenseInstance(inst.numAttributes());
	     
	     newInst.setValue(0, inst.value(0));

	     for(int i = 1; i < inst.numAttributes() - 1; i++)
	     {
	    	 double newVal = Math.log(inst.value(i) + 1);
	    	 // double newVal = inst.value(i);					// Passthrough
	    	 
	    	 newInst.setValue(i, newVal);
	     }
	     
	     newInst.setValue(inst.numAttributes() - 1, inst.value(inst.numAttributes() - 1));
	     
	     return newInst;
	}
	
	@Override
	public Capabilities getCapabilities()
	{
		Capabilities result = super.getCapabilities();
	    result.enableAllAttributes();
	    result.enableAllClasses();
	    result.enable(Capability.MISSING_VALUES);
	    result.enable(Capability.NO_CLASS);  // filter doesn't need class to be set
	    return result;
	}

	@Override
	public String globalInfo()
	{
		return "A simple filter which applies the log function to all attribute values.";
	}

}
