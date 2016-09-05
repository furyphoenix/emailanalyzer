package neos.component.ner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NeosDictEntityLocator implements NeosEntityLocator {
	Collection<String> m_dict;
	private int avarageWordLength;
	private final static int defaultAvarageWordLength=2;
	private int maxWordLength;
	private final static int defaultMaxWordLength=8;
	
	public NeosDictEntityLocator(Collection<String> dict){
		m_dict=dict;
		avarageWordLength=defaultAvarageWordLength;
		maxWordLength=defaultMaxWordLength;
	}
	
	public void setAvarageWordLength(int n){
		if(n>0){
			this.avarageWordLength=n;
		}
	}
	
	public void setMaxWordLength(int n){
		if(n>0){
			this.maxWordLength=n;
		}
	}
	
	@Override
	public List<IndexRange> locate(String src) {
		List<IndexRange> resultList = new ArrayList<IndexRange>();
		
		if(src.length()>m_dict.size()*this.avarageWordLength){
			//待处理的文本长度大于词典长度(词典中词汇较少)
			for(String word:m_dict){
				resultList.addAll(locateByWord(word, src));
			}
		}else{
			//词典中词汇很多
			for(int i=maxWordLength; i>=2; i--){
				for(int j=0;j<src.length()-i;j++){
					String word=src.substring(j, j+i);
					if(m_dict.contains(word)){
						resultList.add(new IndexRange(j,j+i));
					}
				}
			}
			
		}
		
		return resultList;
	}
	
	private List<IndexRange> locateByWord(String word, String src){
		NeosRegexEntityLocator loc=new NeosRegexEntityLocator(word);
		return loc.locate(src);
	}

}
