/** The API doc for HotTopicFinder
 * 	@version 1.00
 * 	@author Li Xiao Yu (Phoenix)
 */

package neos.algorithm.lcscs;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.text.*;
import java.util.Date;
import java.util.Hashtable;

import neos.app.email.gui.EmailNetworkWin;
/*import neos.app.email.gui.EmailNetworkWinOld;*/
import neos.app.gui.SimpleNeosEdge;
import neos.app.gui.SimpleNeosVertex;

import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class HotTopicFinder {
	protected Vector<String> Titles;
	protected LinkedList<SortableIdxList> LCSMatrix;
	private String SQLQuery;
	private int volume;
	private int[][] LCSLenMatrix;
	private double delta;
	private double gama;
	private String defaultDir;
	protected boolean isWriteMatrix; 
	protected boolean isWriteTitleSeq;
	protected boolean isWriteHotSeq;
	protected boolean isWriteHotTopic;
	protected boolean isWriteNet;
	protected boolean isDebugInfo;
	protected boolean isDoMerge;
	protected Vector<String> StopWords;
	/*protected static String[] defaultStopWords={
			",",".","!","?","-","_"," ","~","\"","(",")",":",	//英文标点
			"，","。","？","！","—","―","－","…","“","”","《","》","～","（","）","：","、",				//中文标点
			"我","你","他","这","那","哪","们","自己","先生",			//指示代词
			"一些","一个","一点","一下","一次","一种",			//程度
			"转贴","zt","ZT","原创","认为","现在","关于","对于","能够",
			"中国人","中国","看","问题","有一","以为","知道","觉得","应该","应当","建议",
			"如何","当然","多少","最后","怎样","时候",
			"如果","就","因为","所以","已经","只",	"其实","非常","确实",					//连词
			"没有","应该","可能","一定","必须","还有","为什么","什么",
			"的","了","么","好","不","很",
			"怎么","是","可以","也","还","么","些",
			"呵","哈","嘻","吧","嘿","吗","呢"	,"呀"									//叹词
			
	};*/
	protected static String[] defaultStopWords={};
	protected int statLCSCnt=0;		//Counter of LCS
	protected int statDWordCnt=0;	//Counter of Double Words LCS
	protected int statHSCnt=0;		//Counter of Hot Seq
	protected int statFHSCnt=0;		//Counter of Filtered Hot Seq
	protected int statSPCnt=0;		//Counter of Sequence Pair
	protected LinkedList<SortableIdxStr> HotSeq, FilteredHotSeq;
	protected LinkedList<SortableStrPair> HotTopic;
	private Date date;
	private SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm:ss SSS");
	
	/**Construct a new HotTopicFinder class
	 * 
	 * @param query is the SQL statment which can generate the desirable titles 
	 */
	public HotTopicFinder(String query){
		setSQLQuery(query);
		Titles=new Vector<String> ();
		LCSMatrix=new LinkedList<SortableIdxList> ();
		delta=0.01;
		gama=0.002;
		defaultDir=".\\";
		isWriteMatrix=true;
		isWriteTitleSeq=false;
		isWriteHotSeq=true;
		isWriteHotTopic=true;
		isWriteNet=true;
		isDebugInfo=true;
		isDoMerge=true;
		StopWords=new Vector<String> ();
		for(int i=0;i<defaultStopWords.length;i++){
			StopWords.add(defaultStopWords[i]);
		}
		HotSeq=new LinkedList<SortableIdxStr> ();
		FilteredHotSeq=new LinkedList<SortableIdxStr> ();
		HotTopic=new LinkedList<SortableStrPair> ();
	}
	
	protected void setSQLQuery(String query){
		SQLQuery=query;
	}
	
	public void setDefaultDir(String dir){
		defaultDir=dir;
	}
	
	public void evaluate(){
		System.out.println("Process begin.");
		
		setTitles();
		if(volume<=0){
			System.err.println("Error: Can not get titles to analyse.exit...");
			return;
		}
		
		buildLCSMatrix();
		buildHotSeq();
		buildFilteredHotSeq();
		buildSeqPair();
		
		date=new Date();
		System.out.println("["+dateFm.format(date)+"]\t"+"End");
		
	}
	
	public void setTitles(){
		if(isDebugInfo){
			date=new Date();			
			System.out.println("["+dateFm.format(date)+"]\t"+"Preparing the titles...");
		}
		readData();
		volume=Titles.size();
		System.out.println("\tTotal "+ volume+" records read.");
		
		//elimate the html tags 
		for(int i=0;i<volume;i++){
			String currStr=Titles.get(i);
			int startIdx, endIdx;
			while((startIdx=currStr.indexOf("<"))>=0){
				if((endIdx=currStr.indexOf(">"))>=0){
					String headStr=currStr.substring(0,(startIdx-1>=0)?(startIdx-1):0);
					String tailStr=currStr.substring((endIdx+1<=(currStr.length()-1))?(endIdx+1):(currStr.length()-1));
					Titles.set(i,headStr+tailStr);
				}else{
					break;
				}
				currStr=Titles.get(i);
			}
		}
	}
	
	protected void readData(){
		String dbUserName="root";
		String dbPassword="Iamabird";
		String dbUrl="jdbc:mysql://localhost/cyberctm";
		Connection connection;
		
		//load JDBC Driver
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}catch(Exception e){
			System.err.println("Error: Can not load JDBC Driver.");
			return;
		}
		
		//connect to mySQL server
		try{
			connection=DriverManager.getConnection(dbUrl,dbUserName,dbPassword);
		}catch(SQLException se){
			System.err.println("Connect problem: "+se.getMessage());
			return;
		}
		
		//prepare the titles
		try{
			Statement st=connection.createStatement();
			ResultSet rs;
			//String sqlQuery="SELECT bbsbrd14.FileTitle FROM bbsbrd14 WHERE (FileOwnPenName='强国论坛看客' AND FileID>5529150) ORDER BY bbsbrd14.FileID LIMIT "+offset+","+num;
			rs=st.executeQuery(SQLQuery);
			while(rs.next()){
				if(rs.getString(1)==null){
					Titles.add("");
				}else{
					Titles.add(new String(rs.getString(1)));
				}
			}
			st.close();
			connection.close();
		}catch(SQLException se){
			System.out.println("Error read data from db: "+se.getMessage());
		}
	} 
	
	public void buildLCSMatrix(){
		if(isDebugInfo){
			date=new Date();
			System.out.println("["+dateFm.format(date)+"]\t"+"Building LCS Matrix...");
		}
		
		LinkedList<SortableIdxList> LCSMatrixCol=new LinkedList<SortableIdxList> ();
		FileWriter fw=null;
				
		for(int i=0;i<volume;i++){
			LCSMatrix.add(new SortableIdxList(i));
			LCSMatrixCol.add(new SortableIdxList(i));
		}
		
		String currLcs, srcStrA, srcStrB;
		LinkedList<SortableIdxStr> RowList, ColList;
		Date date;
		SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm:ss");
		
		if(isDebugInfo){
			date=new Date();
			
			System.out.println("\t\t"+"["+dateFm.format(date)+"]\t"+"Building LCS Delta Matrix...");
		}
		for(int i=0;i<volume;i++){
			srcStrA=Titles.get(i);
			RowList=LCSMatrix.get(i).list;
			for(int j=0;j<i;j++){
				srcStrB=Titles.get(j);
				ColList=LCSMatrixCol.get(j).list;
				LCSLenMatrix=new int[srcStrA.length()+1][srcStrB.length()+1];
				currLcs=getLcs(srcStrA,srcStrB);
				if(currLcs.length()>1){
					RowList.add(new SortableIdxStr(currLcs,j));
					ColList.add(new SortableIdxStr(currLcs,i));
					statLCSCnt++;
					if(currLcs.length()==2){
						statDWordCnt++;
					}
				}
				LCSLenMatrix=null;
			}
		}
		if(isWriteMatrix){
			SortableIdxStr currIdxStr;
			try{
				fw=new FileWriter(defaultDir+"LCSMatrix.txt");
				for(int i=0;i<volume;i++){
					RowList=LCSMatrix.get(i).list;
					for(int j=0;j<RowList.size();j++){
						currIdxStr=RowList.get(j);
						fw.append("["+i+","+currIdxStr.index+"]");
						fw.append(currIdxStr.strElem+"\t");
					}
					fw.append("\r\n");
				}
				fw.append("\r\n\r\ntotal "+statLCSCnt+" pairs of topics hava LCS.");
				fw.append("\r\nTotal "+statDWordCnt+" of them are double words.");
				fw.close();
			}catch(IOException ie){
				System.err.println("Error: can not close LCS Matrix file.");
			}
		}
		
		if(isDebugInfo){
			date=new Date();
			System.out.println("\t\t"+"["+dateFm.format(date)+"]\t"+"Building LCS Matrix...");
		}
		for(int i=0;i<volume;i++){
			RowList=LCSMatrix.get(i).list;
			ColList=LCSMatrixCol.get(i).list;
			for(int j=0;j<ColList.size();j++){
				RowList.add(ColList.get(j));
			}
			LCSMatrix.get(i).sortElem=RowList.size();
			//StdAlgorithm.removeDup(RowList);
			Hashtable<String, Integer> KeyWordTable=new Hashtable<String, Integer> ();
			LinkedList<SortableIdxStr> srcList;
			srcList=LCSMatrix.get(i).list;
			for(int j=0;j<srcList.size();j++){
				String strA=srcList.get(j).strElem;
				Integer KeyIdx=(Integer)KeyWordTable.get(strA);
				if(KeyIdx!=null){
					srcList.get(KeyIdx.intValue()).sortElem++;
					srcList.remove(j);
					j--;
				}else{
					KeyWordTable.put(strA,new Integer(j));
				}
			}
			StdAlgorithm.quickSort(RowList);
		}
		StdAlgorithm.quickSort(LCSMatrix);
		
		if(isWriteTitleSeq){
			SortableIdxList currIdxList;
			SortableIdxStr currIdxStr;
			try{
				fw=new FileWriter(defaultDir+"TitleSeq.txt");
				for(int i=volume-1;i>=0;i--){
					currIdxList=LCSMatrix.get(i);
					fw.append("["+currIdxList.index+"]\t");
					fw.append("(Total "+currIdxList.sortElem+" refs)\t");
					for(int j=currIdxList.list.size()-1;j>=0;j--){
						currIdxStr=currIdxList.list.get(j);
						fw.append(currIdxStr.strElem);
						fw.append("("+currIdxStr.sortElem+")\t");
					}
					fw.append("\t<"+Titles.get(currIdxList.index)+">\r\n");
				}
				fw.close();
				
			}catch(IOException ie){
				System.err.println("Error: Title Seq file operation failed.");
			}
		}
	}
	
	protected String getLcs(String strA, String strB){
		int i=strA.length();
        int j=strB.length();
        int k=getLcsLen(strA,strB);
        char[] LCS=new char[k];

        while(k>0){
            if(LCSLenMatrix[i][j]==LCSLenMatrix[i-1][j]){
                i--;
            }else if(LCSLenMatrix[i][j]==LCSLenMatrix[i][j-1]){
                j--;
            }else{
                LCS[--k]=strA.charAt(i-1);
                i--;
                j--;
            }
        }

        return (new String(LCS));
	}
	
	protected int getLcsLen(String strA, String strB){
		int strALen=strA.length();
        int strBLen=strB.length();
        boolean isCont=false,isNextCont=false;

        for(int i=1;i<=strALen;i++){
            isNextCont=false;
            for(int j=1;j<=strBLen;j++){
               boolean isNextEqual=(i<strALen)&&(j<strBLen)&&(strA.charAt(i)==strB.charAt(j));
               if((strA.charAt(i-1)==strB.charAt(j-1))&&
                       (isCont||isNextEqual)){
                        LCSLenMatrix[i][j] = LCSLenMatrix[i - 1][j - 1] + 1;
                        isNextCont=isNextEqual||isNextCont;
                }else if(LCSLenMatrix[i-1][j]>=LCSLenMatrix[i][j-1]){
                    LCSLenMatrix[i][j]=LCSLenMatrix[i-1][j];
                }else{
                    LCSLenMatrix[i][j]=LCSLenMatrix[i][j-1];
                }
            }
            isCont=isNextCont;
        }

        return LCSLenMatrix[strALen][strBLen];
	}
	
	public void buildHotSeq(){
		if(isDebugInfo){
			date=new Date();
			System.out.println("["+dateFm.format(date)+"]\t"+"Building Hot Seq...");
		}
		
		LinkedList<SortableIdxStr> srcList;
		Hashtable<String, Integer> KeyWordTable=new Hashtable<String, Integer> ();
		for(int i=0;i<volume;i++){
			srcList=LCSMatrix.get(i).list;
			while(srcList.size()>0){
				String strA=srcList.get(0).strElem;
				Integer KeyIdx=(Integer)KeyWordTable.get(strA);
				if(KeyIdx!=null){
					HotSeq.get(KeyIdx.intValue()).sortElem++;
					srcList.remove(0);
				}else{
					KeyWordTable.put(strA,new Integer(HotSeq.size()));
					HotSeq.add(srcList.remove(0));
					HotSeq.getLast().sortElem=1;
				}
			}
		}
		
		for(int i=0;i<HotSeq.size();i++){
			if(HotSeq.get(i).sortElem<(int)(volume*delta)){
				HotSeq.remove(i);
				i--;
			}
		}
		StdAlgorithm.quickSort(HotSeq);
		
		statHSCnt=HotSeq.size();
		if(isWriteHotSeq){
			try{
				FileWriter fw=new FileWriter(defaultDir+"HotSeq.txt");
				fw.append("Hot Seq:\t");
				for(int i=HotSeq.size()-1;i>=0;i--){
					fw.append(HotSeq.get(i).strElem);
					fw.append("("+HotSeq.get(i).sortElem+")\t");
				}
				fw.append("\r\n");
				fw.close();
			}catch(IOException ie){
				System.err.println("Error: file operation failed while writing hot seq.");
			}
		}
	}
	
	public void buildFilteredHotSeq(){
		if(isDebugInfo){
			date=new Date();
			System.out.println("["+dateFm.format(date)+"]\t"+"Building Filtered Hot Seq...");
		}
		
		int currWordIdx;
		SortableIdxStr currIdxStr;
		for(int i=0;i<HotSeq.size();i++){
			currIdxStr=HotSeq.get(i);
			for(int j=0;j<StopWords.size();j++){
				currWordIdx=currIdxStr.strElem.indexOf(StopWords.get(j));
				if(currWordIdx>=0){
					currIdxStr.strElem=currIdxStr.strElem.replace(StopWords.get(j),"");
				}
			}
			if(currIdxStr.strElem.length()<2){
				continue;
			}else{
				FilteredHotSeq.add(currIdxStr);
			}
		}
		
		StdAlgorithm.removeDup(FilteredHotSeq);
		for(int i=0;i<FilteredHotSeq.size();i++){
			int counter=0;
			currIdxStr=FilteredHotSeq.get(i);
			for(int j=0;j<volume;j++){
				if(isSubsequence(Titles.get(j),currIdxStr.strElem)){
					counter++;
				}
			}
			
			currIdxStr.sortElem=counter;
			
		}
		StdAlgorithm.quickSort(FilteredHotSeq);
		
		if(isDoMerge){
			LinkedList<SortableIdxStr> MergeList=new LinkedList<SortableIdxStr> ();
			for(int i=0;i<FilteredHotSeq.size();i++){
				String currStr=FilteredHotSeq.get(i).strElem;
				MergeList.add(new SortableIdxStr(currStr,i, currStr.length()));
			}
			StdAlgorithm.quickSort(MergeList);
			int idxA, idxB;
			for(int i=MergeList.size()-1;i>=0;i--){
				idxA=MergeList.get(i).index;
				for(int j=i-1;j>=0;j--){
					idxB=MergeList.get(j).index;
					if(MergeList.get(i).strElem.indexOf(MergeList.get(j).strElem)>=0){
						FilteredHotSeq.get(idxB).sortElem-=FilteredHotSeq.get(idxA).sortElem;
					}
				}
			}
			for(int i=0;i<FilteredHotSeq.size();i++){
				if(FilteredHotSeq.get(i).sortElem<(int)(volume*delta)){
					FilteredHotSeq.remove(i);
					i--;
				}
			}
			StdAlgorithm.quickSort(FilteredHotSeq);
		}
		
		statFHSCnt=FilteredHotSeq.size();
			
		if(isWriteHotSeq){
			try{
				FileWriter fw=new FileWriter(defaultDir+"FHotSeq.txt");
				fw.append("Filtered Hot Seq:\t");
				for(int i=FilteredHotSeq.size()-1;i>=0;i--){
					fw.append(FilteredHotSeq.get(i).strElem);
					fw.append("("+FilteredHotSeq.get(i).sortElem+")\t");
				}
				fw.append("\r\n");
				fw.close();
			}catch(IOException ie){
				System.err.println("Error: file operation failed while writing hot seq.");
			}
		}
	}
	
	public void buildSeqPair(){
		if(isDebugInfo){
			date=new Date();
			System.out.println("["+dateFm.format(date)+"]\t"+"Building Filtered Hot Seq Pairs...");
		}
		
		String strA,strB;
		int counter=0;
		int[][] WeightedEdges=new int[statFHSCnt][statFHSCnt];
		
		if(isDebugInfo){
			date=new Date();
			System.out.println("\t\t["+dateFm.format(date)+"]\t"+"Building ...");
		}
		for(int i=statFHSCnt-1;i>=0;i--){
			strA=FilteredHotSeq.get(i).strElem;
			for(int j=i-1;j>=0;j--){
				counter=0;
				strB=FilteredHotSeq.get(j).strElem;
				if((strA.indexOf(strB)>=0)||(strB.indexOf(strA)>=0)){
					continue;
				}
				for(int k=0;k<volume;k++){
					if((isSubsequence(Titles.get(k),strA))&&(isSubsequence(Titles.get(k),strB))){
						counter++;
					}
				}
				if(isWriteNet){
					WeightedEdges[i][j]=counter;
					WeightedEdges[j][i]=counter;
				}
				if(counter>=(int)(volume*gama)){
					HotTopic.add(new SortableStrPair(strA,strB,counter));
				}
			}
		}
		StdAlgorithm.quickSort(HotTopic);
		statSPCnt=HotTopic.size();
		
		if(isDebugInfo){
			date=new Date();
			System.out.println("\t\t["+dateFm.format(date)+"]\t"+"Finding Singles...");
		}
		LinkedList<SortableIdxStr> Single=new LinkedList<SortableIdxStr> ();
		int singleCnt=0;
		String currTitle;
		boolean isSingle=true;
		
		for(int i=0;i<statFHSCnt;i++){
			strA=FilteredHotSeq.get(i).strElem;
			singleCnt=0;
			for(int v=0;v<volume;v++){
				isSingle=true;
				currTitle=Titles.get(v);
				if(currTitle.indexOf(strA)>=0){
					for(int j=0;j<statFHSCnt;j++){
						if(j==i){
							continue;
						}
						strB=FilteredHotSeq.get(j).strElem;
						if(strA.indexOf(strB)>=0){
							continue;
						}
						if(currTitle.indexOf(strB)>=0){
							isSingle=false;
							break;
						}
					}
					if(isSingle){
						singleCnt++;
					}
				}
			}
			Single.add(new SortableIdxStr(strA,i,singleCnt));
			
		}
		StdAlgorithm.quickSort(Single);
		
		if(isWriteHotTopic){
			try{
				FileWriter fw=new FileWriter(defaultDir+"HotTopic.txt");
				for(int i=HotTopic.size()-1;i>=0;i--){
					SortableStrPair currPair=HotTopic.get(i);
					fw.write(currPair.strA+"+"+currPair.strB);
					fw.write("("+currPair.sortElem+")\t");
				}
				fw.write("\r\n");
				for(int i=Single.size()-1;i>=0;i--){
					SortableIdxStr currIdxStr=Single.get(i);
					if(currIdxStr.sortElem>=(int)(volume*gama)){
						fw.write(currIdxStr.strElem);
						fw.write("("+currIdxStr.sortElem+")\t");
					}
				}
				fw.close();
			}catch(IOException ie){
				System.err.println("Error: file operation failed while writing hot topic...");
			}
		}
		
		if(isWriteNet){
			if(isDoMerge){
				if(isDebugInfo){
					date=new Date();
					System.out.println("\t\t["+dateFm.format(date)+"]\t"+"Building net...");
				}
								
				LinkedList<SortableIdxStr> MergeList=new LinkedList<SortableIdxStr> ();
				for(int i=0;i<FilteredHotSeq.size();i++){
					String currStr=FilteredHotSeq.get(i).strElem;
					MergeList.add(new SortableIdxStr(currStr,i, currStr.length()));
				}
				StdAlgorithm.quickSort(MergeList);
				int idxA, idxB;
				for(int i=MergeList.size()-1;i>=0;i--){
					idxA=MergeList.get(i).index;
					for(int j=i-1;j>=0;j--){
						idxB=MergeList.get(j).index;
						if(MergeList.get(i).strElem.indexOf(MergeList.get(j).strElem)>=0){
							for(int k=0;k<statFHSCnt;k++){
								WeightedEdges[idxB][k]-=WeightedEdges[idxA][k];
							}
						}
					}
				}
				MergeList.clear();
				MergeList=null;
			}
			
			
			
			int Degree[]=new int[statFHSCnt];
			int Weighth[]=new int[statFHSCnt];
			int degreeMax=0,weighthMax=0;
			for(int i=0;i<statFHSCnt;i++){
				int maxDegree=0, currWeighth=0;
				for(int j=0;j<statFHSCnt;j++){
					if(WeightedEdges[i][j]>(int)(volume*gama)){
						currWeighth++;
						if(WeightedEdges[i][j]>maxDegree){
							maxDegree=WeightedEdges[i][j];
						}
					}
				}
				Degree[i]=maxDegree>(Single.get(i).sortElem)?maxDegree:(Single.get(i).sortElem);
				Weighth[i]=currWeighth;
				if(Degree[i]>degreeMax){
					degreeMax=Degree[i];
				}
				if(Weighth[i]>weighthMax){
					weighthMax=Weighth[i];
				}
			}
			
			try{
				UndirectedSparseGraph<SimpleNeosVertex,SimpleNeosEdge> graph=new UndirectedSparseGraph<SimpleNeosVertex,SimpleNeosEdge> ();
				
				FileWriter fw=new FileWriter(defaultDir+"NetLog.net");
				fw.write("*Vertices "+FilteredHotSeq.size()+"\r\n");
				for(int i=0;i<FilteredHotSeq.size();i++){
					fw.write((i+1)+"   \""+FilteredHotSeq.get(i).strElem+"<"+FilteredHotSeq.get(i).sortElem+">\"");
					fw.write("  "+(0.05+0.9*Degree[i]/degreeMax)+"  "+(0.05+0.9*Weighth[i]/weighthMax)+"  "+"0.5000\r\n");
					graph.addVertex(new SimpleNeosVertex(FilteredHotSeq.get(i).strElem));
				}
				fw.write("*Edges\r\n");
				for(int i=0;i<statFHSCnt;i++){
					for(int j=0;j<i;j++){
						if(WeightedEdges[i][j]>(int)(volume*gama)){
							fw.write((i+1)+"  "+(j+1)+"\r\n");
							//double ratio=1.0*WeightedEdges[i][j]/(FilteredHotSeq.get(i).sortElem+FilteredHotSeq.get(j).sortElem);
							double ratio=WeightedEdges[i][j];
							SimpleNeosVertex v1=new SimpleNeosVertex(FilteredHotSeq.get(i).strElem);
							SimpleNeosVertex v2=new SimpleNeosVertex(FilteredHotSeq.get(j).strElem);
							SimpleNeosEdge e=new SimpleNeosEdge(v1,v2,ratio);
							graph.addEdge(e, v1, v2);
						}
					}
				}
				fw.close();
				/*EmailNetworkWin win=new EmailNetworkWin(null);
				win.setVisible(true);
				win.setGraph(graph);
				win.showGraph(graph);*/
			}catch(IOException ie){
				System.err.println("Error: writing net file failed...");
			}
		}
	}
	
	protected boolean isSubsequence(String origStr, String subStr){
		int prevIdx=-1,currIdx=-1;
		
		for(int i=0;i<subStr.length();i++){
			if((currIdx=origStr.indexOf(subStr.charAt(i),prevIdx+1))>=0){
				if(currIdx>prevIdx){
					prevIdx=currIdx;
				}else{
					prevIdx=currIdx;
					i--;
				}
			}else{
				return false;
			}
		}
		return true;
	}
	
	public void freeMem(){
		LCSMatrix.clear();
		LCSMatrix=null;
		Titles.clear();
		Titles=null;
		HotSeq.clear();
		HotSeq=null;
		FilteredHotSeq.clear();
		FilteredHotSeq=null;
		HotTopic.clear();
		HotTopic=null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			
		HotTopicFinder myFinder=null;
		
		
		/*String sqlPrefix="Select FileTitle From `cyberctm`.`forum` Where FileTime>=\"2009-07-01\" Order By FileTime DESC Limit ";
		String dirPrefix=".\\";
		double[] LCS_CNT=new double[100];
		double[] DW_CNT=new double[100];
		double[] HS_CNT=new double[100];
		double[] FHS_CNT=new double[100];
		double[] SP_CNT=new double[100];
		//for(int i=0;i<100;i++){
		for(int i=0;i<20;i++){
			System.out.println("--------------"+i+"--------------");
			//int offset=i*1000;
			//myFinder=new HotTopicFinder(sqlPrefix+offset+",1000");
			//myFinder.setDefaultDir(dirPrefix+"Result\\1000\\"+offset);
			myFinder=new HotTopicFinder(sqlPrefix+(500*(i+1)));
			myFinder.setDefaultDir(dirPrefix+""+(500*(i+1)));
			myFinder.evaluate();
			LCS_CNT[i]=myFinder.statLCSCnt;
			DW_CNT[i]=myFinder.statDWordCnt;
			HS_CNT[i]=myFinder.statHSCnt;
			FHS_CNT[i]=myFinder.statFHSCnt;
			SP_CNT[i]=myFinder.statSPCnt;
			myFinder.freeMem();
			myFinder=null;
		}
		try{
			StatResult rs;
			FileWriter fw=new FileWriter(dirPrefix+"Stat\\1000.txt");
			
			fw.write("LCS counter:\r\n");
			for(int i=0;i<LCS_CNT.length;i++){
				fw.write(LCS_CNT[i]+"\t");
			}
			fw.write("\r\n");
			rs=StdAlgorithm.statistic(LCS_CNT);
			fw.write("mean="+rs.mean+"\tstderr="+rs.stdErr+"\r\n\r\n\r\n");
			
			fw.write("Double Word counter:\r\n");
			for(int i=0;i<DW_CNT.length;i++){
				fw.write(DW_CNT[i]+"\t");
			}
			fw.write("\r\n");
			rs=StdAlgorithm.statistic(DW_CNT);
			fw.write("mean="+rs.mean+"\tstderr="+rs.stdErr+"\r\n\r\n\r\n");
			
			fw.write("Hot Seq counter:\r\n");
			for(int i=0;i<HS_CNT.length;i++){
				fw.write(HS_CNT[i]+"\t");
			}
			fw.write("\r\n");
			rs=StdAlgorithm.statistic(HS_CNT);
			fw.write("mean="+rs.mean+"\tstderr="+rs.stdErr+"\r\n\r\n\r\n");
			
			fw.write("Filtered Hot Seq counter:\r\n");
			for(int i=0;i<FHS_CNT.length;i++){
				fw.write(FHS_CNT[i]+"\t");
			}
			fw.write("\r\n");
			rs=StdAlgorithm.statistic(FHS_CNT);
			fw.write("mean="+rs.mean+"\tstderr="+rs.stdErr+"\r\n\r\n\r\n");
			
			fw.write("Seq Pair counter:\r\n");
			for(int i=0;i<SP_CNT.length;i++){
				fw.write(SP_CNT[i]+"\t");
			}
			fw.write("\r\n");
			rs=StdAlgorithm.statistic(SP_CNT);
			fw.write("mean="+rs.mean+"\tstderr="+rs.stdErr+"\r\n\r\n\r\n");
			
			fw.close();
			
		}catch(IOException ie){
			System.err.println("Error: file operation failed while writing statistic file.");
		}*/
		
	
		
		
		//String sql="Select FileTitle from bbsbrd17 Where FileRootID IN (SELECT FileRootID from bbsbrd17 Where FileOwn=16380) ORDER BY FileID";
		/*String sql="Select FileContent From `cyberctm`.`forum` Where FileTime>=\"2009-07-01\" Order By FileTime DESC Limit 200;";
		myFinder=new HotTopicFinder(sql);
		//myFinder.StopWords.add("数学");
		myFinder.evaluate();
		myFinder.freeMem();*/
		
		String sql="Select FileTitle From `cyberctm`.`forum` Where FileTime>=\"2009-07-01\" Order By FileTime DESC Limit 2000";
		myFinder=new HotTopicFinder(sql);
		myFinder.evaluate();
		myFinder.freeMem();

	}

}
