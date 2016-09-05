package neos.app.email.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import neos.algorithm.afisa.AfisaTool;
import neos.algorithm.afisa.Cluster;
import neos.algorithm.charm.Charm;
import neos.app.gui.SimpleNeosEdge;
import neos.app.gui.SimpleNeosVertex;

import org.apache.commons.collections15.Transformer;
import org.jdesktop.swingx.JXDatePicker;

import phoenix.visualization.network.MapBaseValueTransformer;



import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

import javax.swing.JTabbedPane;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.FlowLayout;
import javax.swing.JCheckBox;

public class EmailNetworkWin extends NeosStandardFrame {
	private static final long serialVersionUID = 5435423113769253164L;
	
	private final static SimpleDateFormat fmt = new SimpleDateFormat(
    "yyyy-MM-dd");
	private final static DecimalFormat df=new DecimalFormat("#.########");
	private final EmailMainWin parent;
	
	private JXDatePicker dpEnd;
    private JXDatePicker dpStart;
    private final NetworkOperatePanel panelFreqGraph;
    private final NetworkOperatePanel panelRelationGraph;
    private final NetworkOperatePanel panelReciverGraph;
    private final NetworkOperatePanel panelDomainGraph;
    
    private Graph<SimpleNeosVertex, SimpleNeosEdge> freqGraph;
    private Graph<SimpleNeosVertex, SimpleNeosEdge> recvGraph;
    private Graph<SimpleNeosVertex, SimpleNeosEdge> relGraph;
    private Graph<SimpleNeosVertex, SimpleNeosEdge> domainGraph;
    
    private final Hashtable<SimpleNeosVertex, Double> outEmailCntTab=new Hashtable<SimpleNeosVertex, Double> ();
    private final Hashtable<SimpleNeosVertex, Double> outDomainCntTab=new Hashtable<SimpleNeosVertex, Double> ();
    private final JTable table;
    private final DefaultTableModel model;
    private final static String[] Headers={"计数","成员"};
    //private BitSet[] bitVectors;
    private List<String> toAddrList;
    private List<List<String>> multiToAddrList;

	/**
	 * Create the frame.
	 */
	public EmailNetworkWin(EmailMainWin parent) {
		super();
        this.parent = parent;
        this.setBounds(100, 100, 800, 600);
        this.setTitle("邮件网络分析");
        
        JPanel panel = new JPanel();
        getCenterPanel().add(panel, BorderLayout.NORTH);

        JLabel label = new JLabel("\u8D77\u6B62\u65E5\u671F\uFF1A  ");
        panel.add(label);

        dpStart = new JXDatePicker(new Date());
        dpStart.setFormats(fmt);
        panel.add(dpStart);

        JLabel label_1 = new JLabel("  \u81F3  ");
        panel.add(label_1);

        dpEnd = new JXDatePicker(new Date());
        dpEnd.setFormats(fmt);
        panel.add(dpEnd);

        JLabel lblNewLabel = new JLabel("        ");
        panel.add(lblNewLabel);

        JButton btnNewButton = new JButton("\u5206\u6790");
        btnNewButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    try {
                        new Thread() {
                                public void run() {
                                	analyze();
                                }
                            }.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        panel.add(btnNewButton);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getCenterPanel().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("\u6765\u5F80\u9891\u5EA6\u7F51\u7EDC", null, panel_1, null);
        panel_1.setLayout(new BorderLayout(0, 0));
        
        panelFreqGraph = new NetworkOperatePanel();
        panel_1.add(panelFreqGraph, BorderLayout.CENTER);
        
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("\u6536\u4EF6\u5173\u7CFB\u7F51\u7EDC", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        panelReciverGraph = new NetworkOperatePanel();
        panel_2.add(panelReciverGraph, BorderLayout.CENTER);
        
        JPanel panel_4 = new JPanel();
        panel_4.setPreferredSize(new Dimension(10, 150));
        panel_2.add(panel_4, BorderLayout.SOUTH);
        panel_4.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        panel_4.add(scrollPane, BorderLayout.CENTER);
        
        model=new DefaultTableModel(Headers, 0);
        table = new JTable(model);
        scrollPane.setViewportView(table);
        
        JPanel panel_5 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_5.getLayout();
        flowLayout.setHgap(20);
        panel_4.add(panel_5, BorderLayout.SOUTH);
        
        JButton buttonCommunity = new JButton("\u793E\u56E2\u5206\u6790");
        buttonCommunity.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					freqItemSetAnalyse(multiToAddrList);
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_5.add(buttonCommunity);
        
        JButton buttonSearch = new JButton("\u793E\u56E2\u67E5\u627E");
        buttonSearch.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					locateEmailTable();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_5.add(buttonSearch);
        
        JButton buttonCopy = new JButton("\u590D\u5236\u6210\u5458");
        buttonCopy.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					copyEmailTable();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_5.add(buttonCopy);
        
        JButton buttonLocate = new JButton("\u5B9A\u4F4D\u6210\u5458");
        buttonLocate.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		try{
        			new Thread(){
        				public void run(){
        					selectEmailInNetwork();
        				}
        			}.start();
        		}catch(Exception e){
        			e.printStackTrace();
        		}
        	}
        });
        panel_5.add(buttonLocate);
        
        JPanel panel_3 = new JPanel();
        tabbedPane.addTab("\u6765\u5F80\u5173\u7CFB\u7F51\u7EDC", null, panel_3, null);
        panel_3.setLayout(new BorderLayout(0, 0));
        
        panelRelationGraph = new NetworkOperatePanel();
        panel_3.add(panelRelationGraph, BorderLayout.CENTER);
        
        JPanel panel_6 = new JPanel();
        tabbedPane.addTab("域名关系网络", null, panel_6, null);
        panel_6.setLayout(new BorderLayout(0,0));
        
        panelDomainGraph=new NetworkOperatePanel();
        panel_6.add(panelDomainGraph, BorderLayout.CENTER);
		
	}
	
	private void analyze(){
		Date start = dpStart.getDate();
        Date end = dpEnd.getDate();

        if (start.after(end)) {
            setMessage("错误：结束日期不可早于起始日期。");

            return;
        }

        setMessage("分析中……");
        setProgress(true);
        
        buildFreqGraphs(start, end);
        Transformer<SimpleNeosVertex, Double> vt=new MapBaseValueTransformer<SimpleNeosVertex> (outEmailCntTab);
        Transformer<SimpleNeosEdge, Double> et=new Transformer<SimpleNeosEdge, Double> (){
			@Override
			public Double transform(SimpleNeosEdge e) {
				return e.getWeight();
			}
        };
        Transformer<SimpleNeosVertex, Double> dvt=new MapBaseValueTransformer<SimpleNeosVertex>(outDomainCntTab);
        
        if(SwingUtilities.isEventDispatchThread()){
        	panelFreqGraph.setGraph(freqGraph, vt, et);
            panelReciverGraph.setGraph(recvGraph, vt, et);
            panelRelationGraph.setGraph(relGraph, vt, et);
            panelDomainGraph.setGraph(domainGraph, dvt, et);
        }else{
        	final Transformer<SimpleNeosVertex, Double> fvt=vt;
        	final Transformer<SimpleNeosEdge, Double> fet=et;
        	final Transformer<SimpleNeosVertex, Double> fdvt=dvt;
        	SwingUtilities.invokeLater(new Runnable(){
        		public void run(){
        			panelFreqGraph.setGraph(freqGraph, fvt, fet);
                    panelReciverGraph.setGraph(recvGraph, fvt, fet);
                    panelRelationGraph.setGraph(relGraph, fvt, fet);
                    panelDomainGraph.setGraph(domainGraph, fdvt, fet);
        		}
        	});
        }
        
        
        this.getCenterPanel().updateUI();
        
        setMessage("");
        setProgress(0);
	}
	
	void analyzeSearchResult(Collection<Integer> docIds){
		setMessage("分析中……");
        setProgress(true);
        
        buildFreqGraphs(docIds);
        Transformer<SimpleNeosVertex, Double> vt=new MapBaseValueTransformer<SimpleNeosVertex> (outEmailCntTab);
        Transformer<SimpleNeosEdge, Double> et=new Transformer<SimpleNeosEdge, Double> (){
			@Override
			public Double transform(SimpleNeosEdge e) {
				return e.getWeight();
			}
        };
        Transformer<SimpleNeosVertex, Double> dvt=new MapBaseValueTransformer<SimpleNeosVertex>(outDomainCntTab);
        
        if(SwingUtilities.isEventDispatchThread()){
        	panelFreqGraph.setGraph(freqGraph, vt, et);
            panelReciverGraph.setGraph(recvGraph, vt, et);
            panelRelationGraph.setGraph(relGraph, vt, et);
            panelDomainGraph.setGraph(domainGraph, dvt, et);
        }else{
        	final Transformer<SimpleNeosVertex, Double> fvt=vt;
        	final Transformer<SimpleNeosEdge, Double> fet=et;
        	final Transformer<SimpleNeosVertex, Double> fdvt=dvt;
        	SwingUtilities.invokeLater(new Runnable(){
        		public void run(){
        			panelFreqGraph.setGraph(freqGraph, fvt, fet);
                    panelReciverGraph.setGraph(recvGraph, fvt, fet);
                    panelRelationGraph.setGraph(relGraph, fvt, fet);
                    panelDomainGraph.setGraph(domainGraph, fdvt, fet);
        		}
        	});
        }
        
        
        this.getCenterPanel().updateUI();
        
        setMessage("");
        setProgress(0);
	}
	
	private final static String getEmailDomain(String email){
		String domain=null;
		int idx=email.indexOf("@");
		try{
			domain=email.substring(idx+1);
		}catch(Exception e){
			
		}
		
		return domain;
	}
	
	void buildFreqGraphs(Collection<Integer> docIds){
		System.out.println("开始生成网络");
		
		Connection emailConn = parent.getEmailDbConnect();
        String dbName = parent.getEmailDbName();
        
        outEmailCntTab.clear();
        outDomainCntTab.clear();
        
        Hashtable<String, Integer> toAddrIdxTab=new Hashtable<String, Integer> ();
        toAddrList=new ArrayList<String> ();
        List<List<String>> idAddrList=new ArrayList<List<String>> ();
        multiToAddrList=new ArrayList<List<String>> ();
        
        freqGraph = new DirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        recvGraph = new UndirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        relGraph = new UndirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        domainGraph=new DirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge> ();
        
        
        for(int id:docIds){
        	List<String> addrList=new ArrayList<String> ();
        	
        	StringBuilder sb=new StringBuilder();
            sb.append("Select `FromAddr`, `ToAddr` From `");
            sb.append(dbName);
            sb.append("`.`emailfromto` Where `EmailID`=");
            sb.append(id);
            
            String sql=sb.toString();
            
            try{
            	Statement st = emailConn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                
                while(rs.next()){
                	//___________vertex weight__________________
                	String from=rs.getString("FromAddr").toLowerCase();
                	String to=rs.getString("ToAddr").toLowerCase();
                	double fromCnt=0;
                	if(outEmailCntTab.containsKey(from)){
                		fromCnt=outEmailCntTab.get(from);
                	}
                	outEmailCntTab.put(new SimpleNeosVertex(from), fromCnt+1.0);
                	
                	//________domain weight________________
                	String fromDomain=getEmailDomain(from);
                	String toDomain=getEmailDomain(to);
                	double fromDCnt=0;
                	if(outDomainCntTab.containsKey(fromDomain)){
                		fromDCnt=outDomainCntTab.get(fromDomain);
                	}
                	outDomainCntTab.put(new SimpleNeosVertex(fromDomain), fromDCnt+1.0);
                	
                	//___________freq graph______________
                	SimpleNeosVertex fromVertex = new SimpleNeosVertex(from);
                	SimpleNeosVertex toVertex = new SimpleNeosVertex(to);
                	
                	if (!freqGraph.containsVertex(fromVertex)) {
                        freqGraph.addVertex(fromVertex);
                        relGraph.addVertex(fromVertex);
                    }

                    if (!freqGraph.containsVertex(toVertex)) {
                        freqGraph.addVertex(toVertex);
                        relGraph.addVertex(toVertex);
                    }
                    
                    if(!outEmailCntTab.containsKey(toVertex)){
                    	outEmailCntTab.put(toVertex, 0.0);
                    }

                    SimpleNeosEdge e = freqGraph.findEdge(fromVertex, toVertex);

                    if (e == null) {
                        freqGraph.addEdge(new SimpleNeosEdge(fromVertex, toVertex, 1.0),
                            fromVertex, toVertex, EdgeType.DIRECTED);
                    } else {
                        double val = e.getWeight();
                        freqGraph.removeEdge(e);
                        freqGraph.addEdge(new SimpleNeosEdge(fromVertex, toVertex,
                                val + 1.0), fromVertex, toVertex, EdgeType.DIRECTED);

                    }
                    
                    if(!toAddrIdxTab.contains(to)){
                    	int idx=toAddrIdxTab.size();
                    	toAddrIdxTab.put(to, idx);
                    	toAddrList.add(to);
                    }
                    addrList.add(to);
                    
                    //_________domain graph___________
                    SimpleNeosVertex fromDomainVertex=new SimpleNeosVertex(fromDomain);
                    SimpleNeosVertex toDomainVertex=new SimpleNeosVertex(toDomain);
                    if(!domainGraph.containsVertex(fromDomainVertex)){
                    	domainGraph.addVertex(fromDomainVertex);
                    }
                    if(!domainGraph.containsVertex(toDomainVertex)){
                    	domainGraph.addVertex(toDomainVertex);
                    }
                    if(!outDomainCntTab.containsKey(toDomainVertex)){
                    	outDomainCntTab.put(toDomainVertex, 0.0);
                    }
                    SimpleNeosEdge de=domainGraph.findEdge(fromDomainVertex, toDomainVertex);
                    if(de==null){
                    	domainGraph.addEdge(new SimpleNeosEdge(fromDomainVertex, toDomainVertex, 1.0),fromDomainVertex, toDomainVertex, EdgeType.DIRECTED);
                    }else{
                    	double val=de.getWeight();
                    	domainGraph.removeEdge(de);
                    	domainGraph.addEdge(new SimpleNeosEdge(fromDomainVertex, toDomainVertex, val+1.0),fromDomainVertex, toDomainVertex, EdgeType.DIRECTED);
                    }
                    
                    
                }
            }catch(Exception e){
            	e.printStackTrace();
            }
            
            idAddrList.add(addrList);
    		if(addrList.size()>1){
    			multiToAddrList.add(addrList);
    		}
        }
        System.out.println("频度网络包含"+freqGraph.getVertexCount()+"节点，"+freqGraph.getEdgeCount()+"连边");
        System.out.println("域名网络包含"+domainGraph.getVertexCount()+"节点，"+domainGraph.getEdgeCount()+"连边");
        
        //recvGraph
        for(List<String> addrList:multiToAddrList){
        	
        	for(int i=1; i<addrList.size(); i++){
        		String addr=addrList.get(i);
        		SimpleNeosVertex v1 = new SimpleNeosVertex(addr);
        		if(!recvGraph.containsVertex(v1)){
        			recvGraph.addVertex(v1);
        		}
        		for(int j=0; j<i; j++){
        			SimpleNeosVertex v2 = new SimpleNeosVertex(addrList.get(j));
        			if(!recvGraph.containsVertex(v2)){
        				recvGraph.addVertex(v2);
        			}
        			SimpleNeosEdge e = recvGraph.findEdge(v1, v2);
        			if(e==null){
        				recvGraph.addEdge(new SimpleNeosEdge(v1, v2, 1.0), v1, v2, EdgeType.UNDIRECTED);
        			}else{
        				double val = e.getWeight();
        				recvGraph.removeEdge(e);
        				recvGraph.addEdge(new SimpleNeosEdge(v1, v2, val + 1.0), v1, v2, EdgeType.UNDIRECTED);
        			}
        		}
        	}
        	
        }
        System.out.println("收件网络包含"+recvGraph.getVertexCount()+"节点，"+recvGraph.getEdgeCount()+"连边");
        
        //relGraph
        for(SimpleNeosEdge e:freqGraph.getEdges()){
        	SimpleNeosVertex v1=e.getFrom();
        	SimpleNeosVertex v2=e.getTo();
        	
        	//freqGraph仅有v1至v2的连结，无v2至v1的连结，即单向连结
        	if(!freqGraph.isNeighbor(v2, v1)){
        		continue; 
        	}
        	
        	//relGraph中还无这两个节点之间的连边
        	if(!relGraph.isNeighbor(v1, v2)){
        		double val1=e.getWeight();
        		SimpleNeosEdge e2=freqGraph.findEdge(v2, v1);
        		if(e2==null){
        			continue;
        		}
        		double val2=e2.getWeight();
        		double minVal=val1<val2?val1:val2;
        		
        		double total1=outEmailCntTab.get(v1);
        		double total2=outEmailCntTab.get(v2);
        		
        		double ratio=2.0*minVal/(total1+total2);
        		
        		SimpleNeosEdge rele=new SimpleNeosEdge(v1,v2, ratio);
        		relGraph.addEdge(rele, v1, v2, EdgeType.UNDIRECTED);
        	}
        }
        System.out.println("关系网络包含"+relGraph.getVertexCount()+"节点，"+relGraph.getEdgeCount()+"连边");
        
        System.out.println("生成网络完毕");
		
	}
	
	private void buildFreqGraphs(Date startDate, Date endDate){
		System.out.println("开始生成网络");
		
		Connection emailConn = parent.getEmailDbConnect();
        String dbName = parent.getEmailDbName();
        
        outEmailCntTab.clear();
        outDomainCntTab.clear();
        
        StringBuilder sbCnt=new StringBuilder();
        sbCnt.append("Select `FromAddr`, Count(Distinct `EmailID`) AS CNT From `");
        sbCnt.append(dbName);
        sbCnt.append("`.`emailfromto` Where `SendDate`>=\"");
        sbCnt.append(fmt.format(startDate));
        sbCnt.append(" 00:00:00\" AND `SendDate`<=\"");
        sbCnt.append(fmt.format(endDate));
        sbCnt.append(" 23:59:59\" Group By `FromAddr`");
        
        String cntSql=sbCnt.toString();
        try{
        	Statement st = emailConn.createStatement();
            ResultSet rs = st.executeQuery(cntSql);
            
            while(rs.next()){
            	String email=rs.getString("FromAddr").toLowerCase();
            	int cnt=rs.getInt("CNT");
            	outEmailCntTab.put(new SimpleNeosVertex(email), 0.0+cnt);
            	
            	String domain=getEmailDomain(email);
            	double val=0;
            	if(outDomainCntTab.containsKey(domain)){
            		val=outDomainCntTab.get(domain);
            	}
            	outDomainCntTab.put(new SimpleNeosVertex(domain), val+cnt);
            }
        }catch(Exception e){
        	e.printStackTrace();
        	return;
        }
        
        
        Hashtable<String, Integer> toAddrIdxTab=new Hashtable<String, Integer> ();
        toAddrList=new ArrayList<String> ();
        List<List<String>> idAddrList=new ArrayList<List<String>> ();
        multiToAddrList=new ArrayList<List<String>> ();

        StringBuilder sb = new StringBuilder();
        sb.append("Select `EmailID`, `FromAddr`, `ToAddr` From `");
        sb.append(dbName);
        sb.append("`.`emailfromto` Where `SendDate`>=\"");
        sb.append(fmt.format(startDate));
        sb.append(" 00:00:00\" AND `SendDate`<=\"");
        sb.append(fmt.format(endDate));
        sb.append(" 23:59:59\" Order By `EmailID`");

        String sql = sb.toString();

        freqGraph = new DirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        recvGraph = new UndirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        relGraph = new UndirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge>();
        domainGraph=new DirectedSparseGraph<SimpleNeosVertex, SimpleNeosEdge> ();
        

        try {
            Statement st = emailConn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            int n = 0;
            
            int prevId=-1;
            List<String> addrList=null;

            while (rs.next()) {
            	//___________freq graph______________
            	int id=rs.getInt("EmailID");
                String fromAddr = rs.getString("FromAddr").toLowerCase();
                SimpleNeosVertex fromVertex = new SimpleNeosVertex(fromAddr);
                String toAddr = rs.getString("ToAddr").toLowerCase();
                SimpleNeosVertex toVertex = new SimpleNeosVertex(toAddr);
                
                String fromDomain=getEmailDomain(fromAddr);
            	String toDomain=getEmailDomain(toAddr);
//            	double fromDCnt=0;
//            	if(outDomainCntTab.containsKey(fromDomain)){
//            		fromDCnt=outDomainCntTab.get(fromDomain);
//            	}
//            	outDomainCntTab.put(new SimpleNeosVertex(fromDomain), fromDCnt+1.0);

                if (!freqGraph.containsVertex(fromVertex)) {
                    freqGraph.addVertex(fromVertex);
                    relGraph.addVertex(fromVertex);
                }

                if (!freqGraph.containsVertex(toVertex)) {
                    freqGraph.addVertex(toVertex);
                    relGraph.addVertex(toVertex);
                }
                
                if(!outEmailCntTab.containsKey(toVertex)){
                	outEmailCntTab.put(toVertex, 0.0);
                }

                SimpleNeosEdge e = freqGraph.findEdge(fromVertex, toVertex);

                if (e == null) {
                    freqGraph.addEdge(new SimpleNeosEdge(fromVertex, toVertex, 1.0),
                        fromVertex, toVertex, EdgeType.DIRECTED);
                } else {
                    double val = e.getWeight();
                    freqGraph.removeEdge(e);
                    freqGraph.addEdge(new SimpleNeosEdge(fromVertex, toVertex,
                            val + 1.0), fromVertex, toVertex, EdgeType.DIRECTED);

                }
                
                if(!toAddrIdxTab.contains(toAddr)){
                	int idx=toAddrIdxTab.size();
                	toAddrIdxTab.put(toAddr, idx);
                	toAddrList.add(toAddr);
                }
                
                if(id!=prevId){
                	if(addrList!=null){
                		idAddrList.add(addrList);
                		if(addrList.size()>1){
                			multiToAddrList.add(addrList);
                		}
                	}
                	addrList=new ArrayList<String> ();
                }
                addrList.add(toAddr);
                prevId=id;

                //System.out.println(n);
                n++;
                
              //_________domain graph___________
                SimpleNeosVertex fromDomainVertex=new SimpleNeosVertex(fromDomain);
                SimpleNeosVertex toDomainVertex=new SimpleNeosVertex(toDomain);
                if(!domainGraph.containsVertex(fromDomainVertex)){
                	domainGraph.addVertex(fromDomainVertex);
                }
                if(!domainGraph.containsVertex(toDomainVertex)){
                	domainGraph.addVertex(toDomainVertex);
                }
                if(!outDomainCntTab.containsKey(toDomainVertex)){
                	outDomainCntTab.put(toDomainVertex, 0.0);
                }
                SimpleNeosEdge de=domainGraph.findEdge(fromDomainVertex, toDomainVertex);
                if(de==null){
                	domainGraph.addEdge(new SimpleNeosEdge(fromDomainVertex, toDomainVertex, 1.0),fromDomainVertex, toDomainVertex, EdgeType.DIRECTED);
                }else{
                	double val=de.getWeight();
                	domainGraph.removeEdge(de);
                	domainGraph.addEdge(new SimpleNeosEdge(fromDomainVertex, toDomainVertex, val+1.0),fromDomainVertex, toDomainVertex, EdgeType.DIRECTED);
                }
            }
            idAddrList.add(addrList);
    		if(addrList.size()>1){
    			multiToAddrList.add(addrList);
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("频度网络包含"+freqGraph.getVertexCount()+"节点，"+freqGraph.getEdgeCount()+"连边");
        
        //recvGraph
        for(List<String> addrList:multiToAddrList){
        	
        	for(int i=1; i<addrList.size(); i++){
        		String addr=addrList.get(i);
        		SimpleNeosVertex v1 = new SimpleNeosVertex(addr);
        		if(!recvGraph.containsVertex(v1)){
        			recvGraph.addVertex(v1);
        		}
        		for(int j=0; j<i; j++){
        			SimpleNeosVertex v2 = new SimpleNeosVertex(addrList.get(j));
        			if(!recvGraph.containsVertex(v2)){
        				recvGraph.addVertex(v2);
        			}
        			SimpleNeosEdge e = recvGraph.findEdge(v1, v2);
        			if(e==null){
        				recvGraph.addEdge(new SimpleNeosEdge(v1, v2, 1.0), v1, v2, EdgeType.UNDIRECTED);
        			}else{
        				double val = e.getWeight();
        				recvGraph.removeEdge(e);
        				recvGraph.addEdge(new SimpleNeosEdge(v1, v2, val + 1.0), v1, v2, EdgeType.UNDIRECTED);
        			}
        		}
        	}
        	
        }
        
        
        /*bitVectors=new BitSet[multiToAddrList.size()];
        int cnt=0;
        for(List<String> addrList:multiToAddrList){
        	BitSet bitSet=new BitSet(toAddrList.size());
        	for(String addr:addrList){
        		int idx=toAddrIdxTab.get(addr);
        		bitSet.set(idx);
        	}
        	bitVectors[cnt++]=bitSet;
        }*/
        
        System.out.println("收件网络包含"+recvGraph.getVertexCount()+"节点，"+recvGraph.getEdgeCount()+"连边");
        
        //relGraph
        for(SimpleNeosEdge e:freqGraph.getEdges()){
        	SimpleNeosVertex v1=e.getFrom();
        	SimpleNeosVertex v2=e.getTo();
        	
        	//freqGraph仅有v1至v2的连结，无v2至v1的连结，即单向连结
        	if(!freqGraph.isNeighbor(v2, v1)){
        		continue; 
        	}
        	
        	//relGraph中还无这两个节点之间的连边
        	if(!relGraph.isNeighbor(v1, v2)){
        		double val1=e.getWeight();
        		SimpleNeosEdge e2=freqGraph.findEdge(v2, v1);
        		if(e2==null){
        			continue;
        		}
        		double val2=e2.getWeight();
        		double minVal=val1<val2?val1:val2;
        		
        		double total1=outEmailCntTab.get(v1);
        		double total2=outEmailCntTab.get(v2);
        		
        		double ratio=2.0*minVal/(total1+total2);
        		
        		SimpleNeosEdge rele=new SimpleNeosEdge(v1,v2, ratio);
        		relGraph.addEdge(rele, v1, v2, EdgeType.UNDIRECTED);
        	}
        }
        System.out.println("关系网络包含"+relGraph.getVertexCount()+"节点，"+relGraph.getEdgeCount()+"连边");
        
        System.out.println("生成网络完毕");
        
	}
	
	private void locateEmailTable(){
		String words=panelReciverGraph.getSearchWords();
		if(words.length()<=0){
			return;
		}
		
		table.clearSelection();
		
		for(int i=0; i<table.getRowCount(); i++){
			if(table.getValueAt(i, 1).toString().indexOf(words)>=0){
				table.getSelectionModel().addSelectionInterval(i, i);
			}
		}
	}
	
	private void copyEmailTable(){
		StringBuffer sb=new StringBuffer();
		int[] idxs=table.getSelectedRows();
		
		for(int idx:idxs){
			sb.append(table.getValueAt(idx, 1).toString());
			sb.append("\r\n");
		}
		
		Clipboard       clip      = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(sb.toString());

        clip.setContents(selection, null);
		
	}
	
	private void selectEmailInNetwork(){
		int[] idxs=table.getSelectedRows();
		
		Set<String> emailSet=new HashSet<String> ();
		
		for(int idx:idxs){
			String emailsStr=table.getValueAt(idx, 1).toString();
			String[] emails=emailsStr.split(";    ");
			for(String email:emails){
				if(!emailSet.contains(email)){
					emailSet.add(email);
				}
			}
		}
		panelReciverGraph.getNetworkPanel().selectVertexByName(emailSet, true, false);
		
	}
	
	private void freqItemSetAnalyse(List<List<String>> database) {
		setMessage("分析中...");
		setProgress(true);
		model.setRowCount(0);
		int minSupport=(int)panelReciverGraph.getCurrentStrength();
		/*AfisaTool tool=new AfisaTool(minSupport);
		List<Map.Entry<Integer, List<Cluster>>> entryList=tool.getSortedFrequentItemSet(bitVectors, true);
		for(Map.Entry<Integer, List<Cluster>> entry:entryList){
			Integer sup=entry.getKey();
			for(Cluster cluster:entry.getValue()){
				StringBuffer sb=new StringBuffer();
				for(Integer idx:cluster.getItems()){
					sb.append(toAddrList.get(idx));
					sb.append("; ");
				}
				String[] row=new String[2];
				row[0]=sup.toString();
				row[1]=sb.toString();
				model.addRow(row);
			}
		}*/
		Charm charm=new Charm();
		try {
			Map<Integer, List<List<String>>> res=charm.runAlgorithm(database, minSupport, 100000);
			List<Integer> supList=new ArrayList<Integer> ();
			for(Integer sup:res.keySet()){
				supList.add(sup);
			}
			Collections.sort(supList);
			for(int i=supList.size()-1; i>=0; i--){
				Integer sup=supList.get(i);
				List<List<String>> sets=res.get(sup);
				for(List<String> elist:sets){
					if(elist.size()<2){
						continue;
					}
					StringBuffer sb=new StringBuffer();
					Collections.sort(elist);
					for(String email:elist){
						sb.append(email);
						sb.append(";    ");
					}
					String[] row=new String[2];
					row[0]=sup.toString();
					row[1]=sb.toString();
					model.addRow(row);
				}
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setMessage("");
		setProgress(false);
	}
	
	

}
