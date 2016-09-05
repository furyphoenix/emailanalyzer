package neos.app.email.gui;

import java.util.Collections;
import java.util.List;

import neos.util.PinyinComparator;
import neos.util.TextFile;

public class UserDictionary {
	private final static String vipWordDict=".\\data\\vipword.dic";
	private final static String stopWordDict=".\\data\\stopword.dic";
	private final static String userWordDict=".\\data\\userword.dic";
	private final static PinyinComparator cmp=new PinyinComparator();
	
	public UserDictionary(){
		
	}
	
	public void addVipWord(String word){
		addWord(word, vipWordDict);
		addWord(word, userWordDict);
	}
	
	public void addStopWord(String word){
		addWord(word, stopWordDict);
	}
	
	public void addUserWord(String word){
		addWord(word, userWordDict);
	}
	
	public void removeVipWord(String word){
		removeWord(word, vipWordDict);
	}
	
	public void removeStopWord(String word){
		removeWord(word, stopWordDict);
	}
	
	public void removeUserWord(String word){
		removeWord(word, vipWordDict);
		removeWord(word, userWordDict);
	}
	
	public List<String> getVipWordList(){
		return getWordList(vipWordDict);
	}
	
	public List<String> getStopWordList(){
		return getWordList(stopWordDict);
	}
	
	public List<String> getUserWordList(){
		return getWordList(userWordDict);
	}
	
	private void addWord(String word, String fileName){
		TextFile file=new TextFile(fileName);
		if(!file.contains(word)){
			file.add(word);
		}
		Collections.sort(file, cmp);
		file.write(fileName);
	}
	
	private void removeWord(String word, String fileName){
		TextFile file=new TextFile(fileName);
		file.remove(word);
		Collections.sort(file, cmp);
		file.write(fileName);
	}
	
	private List<String> getWordList(String fileName){
		return new TextFile(fileName);
	}
}
