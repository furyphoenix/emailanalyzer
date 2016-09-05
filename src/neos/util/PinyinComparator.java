package neos.util;

import java.text.Collator;
import java.util.Comparator;

public class PinyinComparator implements Comparator<String>
{
    public final static int UP = 1; 

    public final static int DOWN = -1; 

    private int state; 
    
    Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
    
    public PinyinComparator(int state) { 
        this.state = state; 
    } 
    
    public PinyinComparator() { 
        this.state = UP;

    } 
    
    @Override
    public int compare(String o1, String o2)
    {  
        if (state == DOWN ) 
        { 
            return sortDown(o1, o2); 
        } 
        else
            return sortUp(o1, o2); 
    }
    
    private int sortUp(String o1, String o2) 
    {
        return cmp.compare(o1, o2);        
    } 

    private int sortDown(String o1, String o2) 
    { 
        int result = cmp.compare(o1, o2);
        
        if ( result > 0) 
        { 
            return -1; 
        } else if ( result < 0) 
        { 
            return 1; 
        } else 
        { 
            return 0; 
        } 
    }

}
