package neos.algorithm.charm;

import java.util.BitSet;
import java.util.Set;

/**
 * This class represents an itemset
 * 
 * Copyright (c) 2008-2012 Philippe Fournier-Viger
 * 
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 *
 * SPMF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPMF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SPMF.  If not, see <http://www.gnu.org/licenses/>.
 */
public class Itemset {
	 Set<Integer> itemset; // ordered
	 int cardinality;
	 BitSet tidset;
	 

	public String toString() {
		StringBuffer r = new StringBuffer();
		for (Integer attribute : itemset) {

			r.append(attribute.toString());
			
			r.append(' ');
		}
		return r.toString();
	}
	
	public double getRelativeSupport(int nbObject) {
		return ((double) cardinality) / ((double) nbObject);
	}
}
