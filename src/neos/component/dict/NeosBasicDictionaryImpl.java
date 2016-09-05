package neos.component.dict;

import java.util.Hashtable;
import java.util.Map;

public class NeosBasicDictionaryImpl implements NeosDictionary<NeosBasicDictionaryEntryImpl> {
	private Map<String, NeosBasicDictionaryEntryImpl> tab;
	
	
	public NeosBasicDictionaryImpl(){
		tab=new Hashtable<String, NeosBasicDictionaryEntryImpl> ();
	}
	
	public void add(String word){
		tab.put(word, new NeosBasicDictionaryEntryImpl(word));
	}
	
	public void add(NeosBasicDictionaryEntryImpl entry){
		tab.put(entry.getWord(), entry);
	}
	
	@Override
	public boolean contains(String word) {
		return tab.containsKey(word);
	}

	@Override
	public NeosBasicDictionaryEntryImpl lookup(String word) {
		return tab.get(word);
	}

	@Override
	public int size() {
		return tab.size();
	}

}
