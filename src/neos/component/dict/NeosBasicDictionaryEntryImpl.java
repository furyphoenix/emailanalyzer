package neos.component.dict;

public class NeosBasicDictionaryEntryImpl implements NeosDictionaryEntry {
	private final String word;
	private NeosSentiment sent;
	private int flag;
	
	public NeosBasicDictionaryEntryImpl(String word){
		this(word,NeosSentiment.NEUTRAL,0);
	}
	
	public NeosBasicDictionaryEntryImpl(String word, NeosSentiment sent){
		this(word, sent, 0);
	}
	
	public NeosBasicDictionaryEntryImpl(String word, int flag){
		this(word, NeosSentiment.NEUTRAL, flag);
	}
	
	public NeosBasicDictionaryEntryImpl(String word, NeosSentiment sent, int flag){
		this.word=word;
		this.sent=sent;
		this.flag=flag;
	}
	
	public void setSentiment(NeosSentiment sent){
		this.sent=sent;
	}
	
	public void setFlag(int flag){
		this.flag=flag;
	}
	
	public void addPos(NeosPartOfSpeech pos){
		flag|=NeosPartOfSpeechFlag.getPosFlag(pos);
	}
	
	public void clearPos(NeosPartOfSpeech pos){
		flag&=(~NeosPartOfSpeechFlag.getPosFlag(pos));
	}
	
	@Override
	public int getPosFlag() {
		return flag;
	}

	@Override
	public NeosSentiment getSentiment() {
		return sent;
	}

	@Override
	public String getWord() {
		return word;
	}

	@Override
	public boolean isPos(NeosPartOfSpeech pos) {
		return (flag&(NeosPartOfSpeechFlag.getPosFlag(pos)))!=0;
	}

}
