package neos.lang;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class NeosWordsNumberConverter {
    
    private final static String[] englishDigitWords = {
            "zero", "one", "two", "three", "four", "five", "six", "seven",
            "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
            "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety",
            "hundred", "thousand", "million", "billion"
        };
    private final static int[] englishDigitValues = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            20, 30, 40, 50, 60, 70, 80, 90, 100, 1000, 1000000, 1000000000
        };
    
    private final static Map<Locale, NeosWordNumberConverterPlugin> map=new Hashtable<Locale, NeosWordNumberConverterPlugin> ();
    
    

    public static int words2num(String str, Locale lang) throws NeosUnsupportLanguageException{
        if(!map.containsKey(lang)){
        	throw new NeosUnsupportLanguageException();
        }
        
        
    	return 0;
    }

    public static String num2words(int val, Locale lang) throws NeosUnsupportLanguageException{
        return null;
    }
    
}
