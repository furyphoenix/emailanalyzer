package neos.component.ner;

//~--- non-JDK imports --------------------------------------------------------

import neos.component.ner.NeosNamedEntity.NamedEntityType;

import neos.tool.fudannlp.NeosFudanNerTool;
import neos.tool.stanfordnlp.NeosStanfordNerTool;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashMap;

public class NeosDefaultNerTool implements NeosNerTool {
    private final static String datePat01_1 =
        "(?<=[\\D[^零一二三四五六七八九十]])(((([1920]{2})?\\d{2})|([零一二三四五六七八九十]{2,4}))[年\\-/\\s])(?!((\\d{3,}|[零一二三四五六七八九十]{4,})))(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[月\\-/\\s])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[日号\\s]?)?(早上|凌晨|上午|中午|下午|晚上|半夜)?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[时点\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十半]{1,3}))[分\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[秒]?)?(?<!(\\d{1,2}[\\s\\-:]?)|[零一二三四五六七八九十])(?=\\D)";
    private final static String datePat01_2 =
        "(?<=[\\D[^零一二三四五六七八九十]])(((([1920]{2})?\\d{2})|([零一二三四五六七八九十]{2,4}))[年\\-/\\s])?(?!((\\d{3,}|[零一二三四五六七八九十]{4,})))(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[月\\-/\\s])(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[日号\\s]?)?(早上|凌晨|上午|中午|下午|晚上|半夜)?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[时点\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十半]{1,3}))[分\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[秒]?)?(?<!(\\d{1,2}[\\s\\-:]?)|[零一二三四五六七八九十])(?=\\D)";
    private final static String datePat01_3 =
        "(?<=[\\D[^零一二三四五六七八九十]])(((([1920]{2})?\\d{2})|([零一二三四五六七八九十]{2,4}))[年\\-/\\s])?(?!((\\d{3,}|[零一二三四五六七八九十]{4,})))(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[月\\-/\\s])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[日号\\s]?)(早上|凌晨|上午|中午|下午|晚上|半夜)?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[时点\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十半]{1,3}))[分\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[秒]?)?(?<!(\\d{1,2}[\\s\\-:]?)|[零一二三四五六七八九十])(?=\\D)";
    private final static String datePat01_4 =
        "(?<=[\\D[^零一二三四五六七八九十]])(((([1920]{2})?\\d{2})|([零一二三四五六七八九十]{2,4}))[年\\-/\\s])?(?!((\\d{3,}|[零一二三四五六七八九十]{4,})))(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[月\\-/\\s])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[日号\\s]?)?(早上|凌晨|上午|中午|下午|晚上|半夜)?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[时点\\-/:])(((\\d{1,2})|([零一二三四五六七八九十半]{1,3}))[分\\-/:])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[秒]?)?(?<!(\\d{1,2}[\\s\\-:]?)|[零一二三四五六七八九十])(?=\\D)";
    private final static String datePat01_5 =
        "(?<=[\\D[^零一二三四五六七八九十]])(((([1920]{2})?\\d{2})|([零一二三四五六七八九十]{2,4}))[年\\-/\\s])?(?!((\\d{3,}|[零一二三四五六七八九十]{4,})))(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[月\\-/\\s])?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[日号\\s]?)?(早上|凌晨|上午|中午|下午|晚上|半夜)?(((\\d{1,2})|([零一二三四五六七八九十]{1,3}))[时点\\-/:])(((\\d{1,2})|([零一二三四五六七八九十半]{1,3}))[分\\-/:]?)?(?<!\\d{2}[\\s\\-:]?)(?=\\D)";
    private final static String datePat02 = "[本这上下][个]?(周|礼拜|星期)[一二三四五六日天\\d]";
    private final static String datePat03 = "[这上下本][个]?月[初中末]?([零一二三四五六七八九十]{1,3}|\\d{1,2})[日号]";
    private final static String datePat04 = "(?<=[\\D[^零一二三四五六七八九十]])[零一二三四五六七八九十]{1,3}([年月日天周]|礼拜|星期|小时)[以之][前后]";
    private final static String datePat05 =
        "((Jan(uary)?)|(Feb(ruary)?)|(Mar(ch)?)|(Apr(il)?)|(May)|(Jun(e)?)|(Jul(y)?)|(Aug(ust)?)|(Sep(tember)?)|(Oct(ober)?)|(Nov(ember)?)|(Dec(ember)))[\\.,](\\s*)[0-9]{1,2}((st)|(nd)|(rd)|(th))?([,]*[\\s*][\\d]+)?";
    private final static String datePat06 =
        "((last)|(next)|(this))?(\\s)*((Mon(day)?)|(Tues(day)?)|(Wed(nesday)?)|(Thur(day)?)|(Fri(day)?)|(Sat(day)?)|(Sun(day)))";
    private final static String emailPat =
        "[_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.(([0-9]{1,3})|([a-zA-Z]{2,3})|(aero|coop|info|museum|name))";
    private final static String idCardPat01 = "(?<=\\D)[1-9]\\d{14}(?=\\D)";
    private final static String idCardPat02 = "(?<=\\D)[1-9]\\d{17}(?=\\D)";
    private final static String locPat01    =
        "(?<=[在到去来])[\\w\u4e00-\u9fa5]{2,4}(路|街|(大道)|(小区)|(宾馆))([零一二三四五六七八九十\\d]{1,3}号)?";
    private final static String locPat02  = "(?<=[在来到去])([\\w\u4e00-\u9fa5]{2,4}(州|省|市|县|乡|村|(自治区)))+";
    private final static String locPat03  = "(?<=[在来到去])([^\\s在来到去\\pP\\pS]){3,20}(?=(见|碰头|会面|里))";
    private final static String mobilePat = "(?<=\\D)1[358]\\d{9}(?=\\D)";
    private final static String orgPat01  =
        "(?<=[在来到去])([^\\s在来到去\\pP\\pS]){2,8}(公司|银行|处|部|局|厅|组织|会议|机构|院|办)(?=(工作|学习|上班)?)";
    private final static String phonePat01 =
        "(?<=\\D)(\\+[\\d]{2,4}[\\s]?)?(\\(?0[\\d]{2,4}\\)?[\\s-]?)?([\\d]{3,4})[\\s-]?([\\d]{3,4})(?=\\D)";
    private final static String phonePat02 = "(?<=\\D)\\(?[\\d]{3}\\)?[\\s-]?[\\d]{3}[\\s-]?[\\d]{4}(?=\\D)";
    private final static String phonePat03 = "(?<=\\D)(1?(-?\\d{3})-?)?(\\d{3})(-?\\d{4})(?=\\D)";
    //private final static String phonePat04="(?<=\\D)[\\d]"
    private final static String urlPat     =
        "(http|ftp|https):\\/\\/[\\w\\-_]+(\\.[\\w\\-_]+)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?";
    private final static String zipCodePat   = "(?<=\\D)\\d{6}(?=\\D)";
    private boolean             isEnglishNer = true;
    private boolean             isChineseNer = true;

    // private HashMap<String, NamedEntityType> currMap;
    private final NeosFudanNerTool    ftool;
    private final NeosRegexNerTool    rtool;
    private final NeosStanfordNerTool stool;

    public NeosDefaultNerTool() {
        stool = NeosStanfordNerTool.getInstance();
        ftool = NeosFudanNerTool.getInstance();
        rtool = new NeosRegexNerTool();
        initRegexNerTool();
    }

    private void initRegexNerTool() {
        rtool.addRegex(phonePat01, NamedEntityType.PhoneNumber);
        rtool.addRegex(phonePat02, NamedEntityType.PhoneNumber);
        rtool.addRegex(phonePat03, NamedEntityType.PhoneNumber);
        rtool.addRegex(datePat01_1, NamedEntityType.DateTime);
        rtool.addRegex(datePat01_2, NamedEntityType.DateTime);
        rtool.addRegex(datePat01_3, NamedEntityType.DateTime);
        rtool.addRegex(datePat01_4, NamedEntityType.DateTime);
        rtool.addRegex(datePat01_5, NamedEntityType.DateTime);
        rtool.addRegex(datePat02, NamedEntityType.DateTime);
        rtool.addRegex(datePat03, NamedEntityType.DateTime);
        rtool.addRegex(datePat04, NamedEntityType.DateTime);
        rtool.addRegex(datePat05, NamedEntityType.DateTime);
        rtool.addRegex(datePat06, NamedEntityType.DateTime);
        rtool.addRegex(emailPat, NamedEntityType.EmailAddress);
        rtool.addRegex(zipCodePat, NamedEntityType.PostalCode);
        rtool.addRegex(idCardPat01, NamedEntityType.IDCardNumber);
        rtool.addRegex(idCardPat02, NamedEntityType.IDCardNumber);
        rtool.addRegex(mobilePat, NamedEntityType.MobilePhoneNumber);
        rtool.addRegex(urlPat, NamedEntityType.URL);
        rtool.addRegex(locPat01, NamedEntityType.LocationName);
        rtool.addRegex(locPat02, NamedEntityType.LocationName);
        rtool.addRegex(locPat03, NamedEntityType.LocationName);
        rtool.addRegex(orgPat01, NamedEntityType.OrgnizationName);
    }

    public void enableEnglishNer(boolean is) {
        isEnglishNer = is;
    }

    public void enableChineseNer(boolean is) {
        isChineseNer = is;
    }

    @Override
    public HashMap<String, NamedEntityType> locate(String text) {
        HashMap<String, NamedEntityType> map  = new HashMap<String, NamedEntityType>();
        HashMap<String, NamedEntityType> rmap = rtool.locate(text);

        for (String word : rmap.keySet()) {
            map.put(word, rmap.get(word));
        }

        rmap.clear();

        if (isEnglishNer) {
            HashMap<String, NamedEntityType> smap = stool.locate(text);

            for (String word : smap.keySet()) {
                map.put(word, smap.get(word));
            }

            smap.clear();
        }

        if (isChineseNer) {
            HashMap<String, NamedEntityType> fmap = ftool.locate(text);

            for (String word : fmap.keySet()) {
                map.put(word, fmap.get(word));
            }

            fmap.clear();
        }

        return map;
    }

    public static void main(String[] args) {
        String             text =
            "刘晓是在一九八一年一月二十三日早上5点在湖南长沙出生，而我是在1980-01-23晚上10点在河南安阳出生的。三天以后的时候就来安全局看你。I was bord on Jan. 23th, 1980. 我的电话号码是15829622540。";
        NeosDefaultNerTool nt   = new NeosDefaultNerTool();

        nt.enableChineseNer(true);
        nt.enableEnglishNer(true);
        System.out.println(nt.locate(text));
    }
}
