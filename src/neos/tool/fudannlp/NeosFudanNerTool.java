package neos.tool.fudannlp;

import java.util.HashMap;

import neos.component.ner.NeosNamedEntity.NamedEntityType;
import neos.component.ner.NeosNerTool;
import edu.fudan.nlp.tag.NERTagger;

public class NeosFudanNerTool implements NeosNerTool {
	private final static String model = "./data/ner.p111014.gz";

	private final static NeosFudanNerTool tool = new NeosFudanNerTool();

	private NERTagger tag;

	private NeosFudanNerTool() {
		try {
			tag = new NERTagger(model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static NeosFudanNerTool getInstance() {
		return tool;
	}

	@Override
	public HashMap<String, NamedEntityType> locate(String text) {
		HashMap<String, NamedEntityType> map = new HashMap<String, NamedEntityType>();
		try {
			HashMap<String, String> omap = tag.tag(text);
			for (String word : omap.keySet()) {
				NamedEntityType type = trans(omap.get(word));
				map.put(word, type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	private static NamedEntityType trans(String type) {
		if (type.toLowerCase().equals("ns")) {
			return NamedEntityType.LocationName;
		} else if (type.toLowerCase().equals("nr")) {
			return NamedEntityType.PersonName;
		} else {
			return NamedEntityType.GeneralNumber;
		}
	}
}
