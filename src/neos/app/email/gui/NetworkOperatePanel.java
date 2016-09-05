package neos.app.email.gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JSpinner;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import javax.swing.ListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import neos.app.gui.SimpleNeosEdge;
import neos.app.gui.SimpleNeosVertex;

import phoenix.visualization.StandardGuiUtil;
import phoenix.visualization.network.BidirectionEdgePredicate;
import phoenix.visualization.network.NetworkPanel;
import phoenix.visualization.network.ValuePredicate;
import phoenix.visualization.network.VertexIsolatePredicate;
import phoenix.visualization.network.VertexNamePredicate;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.apache.commons.collections15.PredicateUtils;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class NetworkOperatePanel extends JPanel {
	private final JTextField textFieldEmail;
	private final JSpinner spinner;
	private final JCheckBox chckbxIsolate;
	private final JTextField textField;
	private NetworkPanel<SimpleNeosVertex, SimpleNeosEdge> panelGraph;
	private final JList listEmail;
	private final SpinnerNumberModel sm;
	private final JCheckBox chckbxKeep;
	private final JCheckBox checkBoxDirection;
	private final DefaultListModel listModel;
	
	private Graph<SimpleNeosVertex, SimpleNeosEdge> origGraph;
	private Transformer<SimpleNeosVertex, Double> vt;
	private Transformer<SimpleNeosEdge, Double> et;
	
	private final static int MaxVisiableVertexNum=500;
	private final static int MaxVisiableEdgeNum=2000;

	/**
	 * Create the panel.
	 */
	public NetworkOperatePanel() {
		setLayout(new BorderLayout(0, 0));
		setPreferredSize(new Dimension(800, 480));
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.EAST);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_3 = new JPanel();
		panel_1.add(panel_3, BorderLayout.NORTH);
		
		JLabel label = new JLabel("\u9ED1\u540D\u5355");
		panel_3.add(label);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JPanel panel_5 = new JPanel();
		panel_2.add(panel_5, BorderLayout.NORTH);
		
		textFieldEmail = new JTextField();
		textFieldEmail.setPreferredSize(new Dimension(100, 21));
		StandardGuiUtil.addMouseMenu4TextComponent(textFieldEmail);
		panel_5.add(textFieldEmail);
		textFieldEmail.setColumns(10);
		
		JButton buttonAdd = new JButton("\u6DFB\u52A0");
		buttonAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							addEmailToBlackList();
						}
					}.start();
				}catch(Exception e){
					
				}
			}
		});
		panel_5.add(buttonAdd);
		
		JPanel panel_6 = new JPanel();
		panel_2.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_6.add(scrollPane, BorderLayout.CENTER);
		
		listModel=new DefaultListModel();
		listEmail = new JList(listModel);
		StandardGuiUtil.addMouseMenu4JList(listEmail);
		scrollPane.setViewportView(listEmail);
		
		JPanel panel_4 = new JPanel();
		panel_2.add(panel_4, BorderLayout.SOUTH);
		
		JButton buttonRemove = new JButton("\u5220\u9664");
		buttonRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							removeEmailFromBlackList();
						}
					}.start();
				}catch(Exception e){
					
				}
			}
		});
		panel_4.add(buttonRemove);
		
		JButton btnApply = new JButton("\u5E94\u7528");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							transform();
						}
					}.start();
				}catch(Exception e){
					
				}
			}
		});
		panel_4.add(btnApply);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));
		
		JToolBar toolBar = new JToolBar();
		panel.add(toolBar, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelGraph.setMouseMode(ModalGraphMouse.Mode.PICKING);
			}
		});
		btnNewButton.setToolTipText("\u9009\u62E9\u6A21\u5F0F");
		btnNewButton.setIcon(new ImageIcon(NetworkOperatePanel.class.getResource("/icon/1307955935_select-rectangular-icon.png")));
		toolBar.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				panelGraph.setMouseMode(ModalGraphMouse.Mode.TRANSFORMING);
			}
		});
		btnNewButton_1.setToolTipText("\u62D6\u653E\u6A21\u5F0F");
		btnNewButton_1.setIcon(new ImageIcon(NetworkOperatePanel.class.getResource("/icon/1307955396_view-restore-icon.png")));
		toolBar.add(btnNewButton_1);
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);
		
		JPanel panel_9 = new JPanel();
		toolBar.add(panel_9);
		
		textField = new JTextField();
		textField.setPreferredSize(new Dimension(80, 21));
		StandardGuiUtil.addMouseMenu4TextComponent(textField);
		panel_9.add(textField);
		textField.setColumns(10);
		
		JButton buttonLocate = new JButton("\u5339\u914D");
		buttonLocate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							locate();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_9.add(buttonLocate);
		
		chckbxKeep = new JCheckBox("\u4FDD\u6301\u539F\u6709\u9009\u62E9");
		panel_9.add(chckbxKeep);
		
		JPanel panel_7 = new JPanel();
		panel.add(panel_7, BorderLayout.SOUTH);
		
		JLabel lblNewLabel = new JLabel("\u5173\u7CFB\u5F3A\u5EA6\u9608\u503C");
		panel_7.add(lblNewLabel);
		
		sm=new SpinnerNumberModel(0.0, 0.0, 100000.0, 0.01);
		spinner = new JSpinner(sm);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				try{
					new Thread(){
						public void run(){
							transform();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		spinner.setPreferredSize(new Dimension(60, 22));
		panel_7.add(spinner);
		
		chckbxIsolate = new JCheckBox("\u79FB\u9664\u5B64\u7ACB\u8282\u70B9");
		chckbxIsolate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							transform();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_7.add(chckbxIsolate);
		
		checkBoxDirection = new JCheckBox("\u79FB\u9664\u5355\u5411\u8FDE\u7ED3");
		checkBoxDirection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try{
					new Thread(){
						public void run(){
							transform();
						}
					}.start();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		panel_7.add(checkBoxDirection);
		
		panelGraph = new NetworkPanel<SimpleNeosVertex, SimpleNeosEdge>();
		panelGraph.setMaxEdgeWidth(10);
		panelGraph.setLabelFontSizeRange(10, 48);
		panelGraph.setVertexSize(10);
		panel.add(panelGraph, BorderLayout.CENTER);
		
	}
	
	public void setGraph(Graph<SimpleNeosVertex, SimpleNeosEdge> graph, Transformer<SimpleNeosVertex,Double> vt, Transformer<SimpleNeosEdge, Double> et){
		this.origGraph=graph;
		this.vt=vt;
		this.et=et;
		
		int vertexCnt=origGraph.getVertexCount();
		int edgeCnt=origGraph.getEdgeCount();
		
		if((vertexCnt<=MaxVisiableVertexNum)&&(edgeCnt<=MaxVisiableEdgeNum)){
			panelGraph.setGraph(graph, vt, et);;
		}else{
			autoAdjustGraph();
		}
		
		
		System.out.println(panelGraph.getViewer().getSize().width+", "+panelGraph.getViewer().getSize().height);
	}
	
	private void autoAdjustGraph(){
		
		//Collection<SimpleNeosVertex> vertices=origGraph.getVertices();
		Collection<SimpleNeosEdge> edges=origGraph.getEdges();
		
		double maxWeight=0.0;
		Hashtable<Double, Integer> weightCntTab=new Hashtable<Double, Integer> ();
		List<Double> weightList=new ArrayList<Double> ();
		List<Integer> cntList=new ArrayList<Integer> ();
		for(SimpleNeosEdge e:edges){
			double weight=e.getWeight();
			if(weight>maxWeight){
				maxWeight=weight;
			}
			int cnt=0;
			if(weightCntTab.containsKey(weight)){
				cnt=weightCntTab.get(weight);
			}else{
				weightList.add(weight);
			}
			weightCntTab.put(weight, cnt+1);
		}
		
		Collections.sort(weightList);
		for(int i=0;i<weightList.size(); i++){
			int cnt=weightCntTab.get(weightList.get(i));
			for(int j=0; j<i; j++){
				int prevCnt=cntList.get(j);
				cntList.set(j, prevCnt+cnt);
			}
			cntList.add(i, cnt);
		}
		
		double threshold=maxWeight;
		for(int i=0; i<cntList.size(); i++){
			if(cntList.get(i)<MaxVisiableEdgeNum){
				threshold=weightList.get(i);
				break;
			}
		}
		
		spinner.setValue(new Double(threshold));
		chckbxIsolate.setSelected(true);
		
		transform();
	}
	
	public NetworkPanel<SimpleNeosVertex, SimpleNeosEdge> getNetworkPanel(){
		return panelGraph;
	}
	
	public double getCurrentStrength(){
		return (Double)spinner.getValue();
	}
	
	public String getSearchWords(){
		return textField.getText();
	}
	
	private void locate(){
		String name=textField.getText().trim().toLowerCase();
		boolean isKeep=chckbxKeep.isSelected();
		
		if((name!=null)&&(name.length()>0)){
			panelGraph.selectVertexByName(name, false, isKeep);
		}
	}
	
	private void transform(){
		double threshold=sm.getNumber().doubleValue();
		boolean isolate=chckbxIsolate.isSelected();
		boolean bidir=checkBoxDirection.isSelected();
		
		Graph<SimpleNeosVertex, SimpleNeosEdge> graph=origGraph;
		
		if(origGraph!=null){
			List<String> lst=new ArrayList<String> ();
			for(int i=0; i<listModel.size(); i++){
				lst.add(listModel.get(i).toString());
			}
			if(lst.size()>0){
				VertexNamePredicate<SimpleNeosVertex> vnpred=new VertexNamePredicate<SimpleNeosVertex> (lst);
				VertexPredicateFilter<SimpleNeosVertex, SimpleNeosEdge> vnfilter=new VertexPredicateFilter<SimpleNeosVertex, SimpleNeosEdge> (PredicateUtils.notPredicate(vnpred));
				graph=vnfilter.transform(graph);
			}
			
			ValuePredicate<SimpleNeosEdge> wepred=new ValuePredicate<SimpleNeosEdge> (et, threshold);
			EdgePredicateFilter<SimpleNeosVertex, SimpleNeosEdge> wefilter=new EdgePredicateFilter<SimpleNeosVertex, SimpleNeosEdge> (wepred);
			graph=wefilter.transform(graph);
			
			if(bidir){
				BidirectionEdgePredicate<SimpleNeosVertex, SimpleNeosEdge> bepred=new BidirectionEdgePredicate<SimpleNeosVertex, SimpleNeosEdge> (graph);
				EdgePredicateFilter<SimpleNeosVertex, SimpleNeosEdge> befilter=new EdgePredicateFilter<SimpleNeosVertex, SimpleNeosEdge> (bepred);
				graph=befilter.transform(graph);
			}
			
			if(isolate){
				VertexIsolatePredicate<SimpleNeosVertex> vipred=new VertexIsolatePredicate<SimpleNeosVertex> (graph);
				VertexPredicateFilter<SimpleNeosVertex, SimpleNeosEdge> vifilter=new VertexPredicateFilter<SimpleNeosVertex, SimpleNeosEdge> (PredicateUtils.notPredicate(vipred));
				graph=vifilter.transform(graph);
			}
			
			panelGraph.setGraph(graph, vt, et);
			this.updateUI();
		}
	}
	
	private void addEmailToBlackList(){
		String email=textField.getText();
		
		if((email==null)||(email.length()<=0)){
			return;
		}
		
		if(listModel.contains(email)){
			return;
		}
		
		final List<String> items=new ArrayList<String> ();
		for(int i=0; i<listModel.size(); i++){
			items.add(listModel.get(i).toString());
		}
		
		items.add(email);
		
		Collections.sort(items);
		
		if(SwingUtilities.isEventDispatchThread()){
			listModel.clear();
			for(String item:items){
				listModel.addElement(item);
			}
		}else{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run(){
					listModel.clear();
					for(String item:items){
						listModel.addElement(item);
					}
				}
			});
		}
	}
	
	private void removeEmailFromBlackList(){
		
		int[] indices = listEmail.getSelectedIndices();
		final List<Integer> idxList=new ArrayList<Integer> ();
		for(int i=0; i<indices.length; i++){
			idxList.add(indices[i]);
		}
		Collections.sort(idxList);
		
		ListModel model=listEmail.getModel();
		DefaultListModel fm=null;
		if(model instanceof DefaultListModel){
			fm=(DefaultListModel)model;
			for (int i = idxList.size() - 1; i >= 0; i--) {
				System.out.println(idxList.get(i));
				fm.remove(idxList.get(i));
				
			}
		}
		
	}
	

}
