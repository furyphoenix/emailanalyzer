package neos.component.dict;

public interface NeosDictionaryEntry {
	String getWord();
	NeosSentiment getSentiment();
	int getPosFlag();
	boolean isPos(NeosPartOfSpeech pos);
}
