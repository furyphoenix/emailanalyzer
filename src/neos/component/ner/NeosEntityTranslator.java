package neos.component.ner;

import java.util.List;

import neos.util.ConvertResult;

public interface NeosEntityTranslator<T extends NeosNamedEntity> {
	boolean isCapable(String src);
	List<ConvertResult<T>> translate(String src);
}
