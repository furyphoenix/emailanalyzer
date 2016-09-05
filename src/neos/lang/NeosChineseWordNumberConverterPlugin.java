package neos.lang;

public class NeosChineseWordNumberConverterPlugin
    implements NeosWordNumberConverterPlugin {
    private final static char[] chineseDigitChars = {
            '〇', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万',
            '亿'
        };
    private final static int[] chineseDigitValues = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100, 1000, 10000, 100000000
        };
    private final static char[] chinesecapitalDigitChars = {
            '零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾', '佰', '仟', '万',
            '亿'
        };
    
    private final static NeosChineseWordNumberConverterPlugin instance=new NeosChineseWordNumberConverterPlugin();
    
    private NeosChineseWordNumberConverterPlugin(){
    	
    }
    
    public static NeosChineseWordNumberConverterPlugin getInstance(){
    	return instance;
    }

    @Override
    public int convert(String str) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isCapable(String str) {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public String convert(int val){
		String str=Integer.toString(val);
		char[] digitArray=str.toCharArray();
		
		StringBuffer sb=new StringBuffer();
		
		for(int i=0; i<digitArray.length; i++){
			int pos=digitArray.length-i;
			if(pos%4==0){
				
			}
		}
		
		return sb.toString();
	}
	/*public String convert(int val) {
		StringBuffer sb=new StringBuffer();
		
		if(val<0){
			sb.append('负');
			val=-val;
		}
		//Yi
		int valYi=val/100000000;
		if(valYi>0){
			int valWanYi=valYi/10000;
			
			if(valWanYi>0){
				sb.append(convertWithin1Wan(valWanYi));
				sb.append('万');
			}
			
			int valYiLeft=valYi%10000;
			if(valYiLeft>0){
				sb.append(convertWithin1Wan(valYiLeft));
			}else{
				sb.append('零');
			}
			
			sb.append('亿');
		}
		//Wan
		int valWan=(val-valYi*100000000)/10000;
		if(valWan>0){
			sb.append(convertWithin1Wan(valWan));
			sb.append('万');
		}else{
			sb.append('零');
		}
		//left
		int valLeft=val-valYi*100000000-valWan*10000;
		if(valLeft>0){
			sb.append(convertWithin1Wan(valLeft));
		}
		
		String tempStr=sb.toString();
		tempStr=tempStr.replaceAll("零+", "零");
		if(tempStr.startsWith("零")){
			tempStr=tempStr.substring(1);
		}
		if(tempStr.endsWith("零")){
			tempStr=tempStr.substring(0, tempStr.length()-1);
		}
		
		if(tempStr.startsWith("一十")){
			tempStr=tempStr.substring(1);
		}
		
		return tempStr;
	}*/
	
	/*private static String convertWithin1Wan(int val){
		if(val==0){
			return "零";
		}
		
		StringBuffer sb=new StringBuffer();
		//
		int c1=val/1000;
		if(c1>0){
			sb.append(chineseDigitChars[c1]);
			sb.append('千');
		}else{
			sb.append('零');
		}
		//
		int c2=(val-c1*1000)/100;
		if(c2>0){
			sb.append(chineseDigitChars[c2]);
			sb.append('百');
		}else{
			sb.append('零');
		}
		//
		int c3=(val-c1*1000-c2*100)/10;
		if(c3>0){
			sb.append(chineseDigitChars[c3]);
			sb.append('十');
		}else{
			sb.append('零');
		}
		//
		int c4=val-c1*1000-c2*100-c3*10;
		if(c4>0){
			sb.append(chineseDigitChars[c4]);
		}
		//
		
		String tempStr=sb.toString();
		
		tempStr=tempStr.replaceAll("零+", "零");
		
		if(tempStr.endsWith("零")){
			tempStr=tempStr.substring(0, tempStr.length()-1);
		}
		
		return tempStr;
		
	}*/
	
	
	
	public static void main(String[] args){
		int[] test={10000023, 950612453, 9006133,100500,107,35,60000};
		for(int i=0; i<test.length; i++){
			System.out.println(NeosChineseWordNumberConverterPlugin.getInstance().convert(test[i]));
		}
			
	}
}
