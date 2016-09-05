package neos.component.ner;

import java.util.HashMap;

import neos.component.ner.NeosNamedEntity.NamedEntityType;

public interface NeosNerTool {
	public HashMap<String, NamedEntityType> locate(String text);
}
