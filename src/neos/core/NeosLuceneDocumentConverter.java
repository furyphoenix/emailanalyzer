package neos.core;

import org.apache.lucene.document.Document;

public interface NeosLuceneDocumentConverter<T> {
	Document convert(T obj);
}
