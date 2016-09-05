package neos.tool.whois;

public class WhoisServerSelector {
	private WhoisServerSelector instance=new WhoisServerSelector();
	
	private WhoisServerSelector(){
		
	}
	
	public WhoisServerSelector getInstance(){
		return instance;
	}
	
	public String getServer(String domain){
		return null;
	}
}
