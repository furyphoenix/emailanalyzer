package neos.app.email.gui;

import java.util.List;

public class KeywordSearchResult {
	public final List<Integer> docIdList;
	public final List<String[]> docDataList;
	public final List<String> previews;
	public final List<Integer> attIdList;
	public final List<String[]> attDataList;
	public final List<String> attPreviews;
	
	public KeywordSearchResult(List<Integer> docIdList, List<String[]> docDataList, List<String> previews, List<Integer> attIdList, List<String[]> attDataList, List<String> attPreviews){
		this.docIdList=docIdList;
		this.docDataList=docDataList;
		this.previews=previews;
		this.attIdList=attIdList;
		this.attDataList=attDataList;
		this.attPreviews=attPreviews;
	}
}
