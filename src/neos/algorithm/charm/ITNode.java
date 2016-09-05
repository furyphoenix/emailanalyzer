package neos.algorithm.charm;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents an ITNode
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
public class ITNode {
	Itemset itemsetObject = new Itemset();
	private ITNode parent = null;
	private List<ITNode> childNodes = new ArrayList<ITNode>();
	
	public int size(){
		return itemsetObject.cardinality;
	}
	

	public double getRelativeSupport(int nbObject) {
		return ((double) itemsetObject.cardinality) / ((double) nbObject);
	}
	
	public ITNode(Set<Integer> itemset){
		this.itemsetObject.itemset = itemset;
	}

	public Set<Integer> getItemset() {
		return itemsetObject.itemset;
	}

	public BitSet getTidset() {
		return itemsetObject.tidset;
	}

	public void setTidset(BitSet tidset, int cardinality) {
		this.itemsetObject.tidset = tidset;
		this.itemsetObject.cardinality = cardinality;
	}

	public List<ITNode> getChildNodes() {
		return childNodes;
	}

	public ITNode getParent() {
		return parent;
	}

	public void setParent(ITNode parent) {
		this.parent = parent;
	}
	
	// for charm
	public void replaceInChildren(Set<Integer> replacement) {
		for(ITNode node : getChildNodes()){
			Set<Integer> itemset  = node.getItemset();
			for(Integer item : replacement){
				if(!itemset.contains(item)){
					itemset.add(item);
				}
			}
			node.replaceInChildren(replacement);
		}
	}


	public void setItemset(Set<Integer> union) {
		this.itemsetObject.itemset = union;
	}


}
