package neos.component.dict;

public class NeosPartOfSpeechFlag {
	public static final int ADJ    = 4;
    public static final int ADV    = 64;
    public static final int AUX    = 512;
    public static final int CLAS   = 16;
    public static final int CONJ   = 256;
    public static final int COOR   = 8192;
    public static final int ECHO   = 2048;
    public static final int IDIOM  = 65536;
    public static final int NOUN   = 1;
    public static final int NUM    = 8;
    public static final int PREFIX = 32768;
    public static final int PREP   = 128;
    public static final int PRON   = 32;
    public static final int QUES   = 1024;
    public static final int STRU   = 4096;
    public static final int SUFFIX = 16384;
    public static final int VERB   = 2;

    public static int getPosFlag(NeosPartOfSpeech pos) {
        switch (pos) {
        case ADJ :
            return ADJ;

        case ADV :
            return ADV;

        case AUX :
            return AUX;

        case SUFFIX :
            return SUFFIX;

        case CONJ :
            return CONJ;

        case QUES :
            return QUES;

        case CLAS :
            return CLAS;

        case PRON :
            return PRON;

        case NUM :
            return NUM;

        case IDIOM :
            return IDIOM;

        case STRU :
            return STRU;

        case COOR :
            return COOR;

        case ECHO :
            return ECHO;

        case N :
            return NOUN;

        case PREFIX :
            return PREFIX;

        case PREP :
            return PREP;

        case UNKNOWN :
            return 0;
        }

        return 0;
    }
}
