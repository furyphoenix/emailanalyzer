package neos.component.ner;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;

public interface NeosEntityLocator {
    List<IndexRange> locate(String src);
}
