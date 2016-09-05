package neos.component.dict;

public interface NeosDictionary<T extends NeosDictionaryEntry> {
	boolean contains(String word);
	T lookup(String word);
	int size();
}
