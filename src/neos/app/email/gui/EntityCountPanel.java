package neos.app.email.gui;

//~--- JDK imports ------------------------------------------------------------

import java.awt.BorderLayout;

import java.text.Collator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class EntityCountPanel extends JPanel {
    private final static String[]      Headers          = { "信息", "出现次数", "邮件数量" };
    private static final long          serialVersionUID = 1L;
    private final static Comparator<?> localStringCmp   = Collator.getInstance(Locale.CHINA);
    private final static Comparator<?> intStringCmp     = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            return Integer.parseInt(o1.toString()) - Integer.parseInt(o2.toString());
        }
    };
    private final Map<Integer, Set<String>> cntInfoSetMap;
    private final DefaultListModel          docListModel;
    private final Map<Integer, String>      idTitleMap;
    private final Map<String, Set<Integer>> infoEmailIdMap;
    private final Map<String, Integer> infoCntMap;
    private final DefaultTableModel         model;
    private final JTable                    table;
    private final int TOPCOUNT=100;

    /**
     * Create the panel.
     */
    public EntityCountPanel(DefaultListModel listModel, Map<Integer, String> idTitleMap) {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();

        add(scrollPane, BorderLayout.CENTER);
        model = new DefaultTableModel(Headers, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                try {
                    new Thread() {
                        public void run() {
                            updateDocList(docListModel);
                        }
                    }.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<DefaultTableModel>(model);

        rowSorter.setComparator(0, localStringCmp);
        rowSorter.setComparator(1, intStringCmp);
        table.setRowSorter(rowSorter);
        scrollPane.setViewportView(table);

        // custom code start here
        infoEmailIdMap  = new HashMap<String, Set<Integer>>();
        infoCntMap=new HashMap<String, Integer> ();
        cntInfoSetMap   = new HashMap<Integer, Set<String>>();
        docListModel    = listModel;
        this.idTitleMap = idTitleMap;
    }

    public void clearData() {
        infoEmailIdMap.clear();
        cntInfoSetMap.clear();

        if (SwingUtilities.isEventDispatchThread()) {
            model.setRowCount(0);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    model.setRowCount(0);
                }
            });
        }
    }

    public void addData(String info, int docId) {
        Set<Integer> idSet;

        if (infoEmailIdMap.containsKey(info)) {
            idSet = infoEmailIdMap.get(info);
        } else {
            idSet = new HashSet<Integer>();
            infoEmailIdMap.put(info, idSet);
        }

        idSet.add(docId);
        int cnt=0;
        if(infoCntMap.containsKey(info)){
        	cnt=infoCntMap.get(info);
        }
        infoCntMap.put(info, cnt+1);
    }

    public void updateEntityCountTable(Collection<String> topInfos) {
        List<Integer> cntList = new ArrayList<Integer>();

        for (String entity : infoEmailIdMap.keySet()) {
            int         cnt = infoEmailIdMap.get(entity).size();
            Set<String> infoSet;

            if (cntInfoSetMap.containsKey(cnt)) {
                infoSet = cntInfoSetMap.get(cnt);
            } else {
                infoSet = new HashSet<String>();
                cntInfoSetMap.put(cnt, infoSet);
                cntList.add(cnt);
            }

            infoSet.add(entity);
        }

        Collections.sort(cntList);
        
        int topCnt=0;
        
        for (int i = cntList.size() - 1; i >= 0; i--) {
            int         cnt     = cntList.get(i);
            Set<String> infoSet = cntInfoSetMap.get(cnt);

            for (String info : infoSet) {
                String[] row = new String[3];

                row[0] = info;
                row[1]= infoCntMap.get(info).toString();
                row[2] = cnt + "";
                model.addRow(row);
                
                if(topCnt<100){
                	topInfos.add(info);
                	topCnt++;
                }
            }
        }
    }

    void updateDocList(DefaultListModel listModel) {
        if (listModel == null) {
            return;
        }

        int idx = table.getSelectedRow();

        if (idx < 0) {
            return;
        }

        listModel.clear();

        String       info   = table.getValueAt(idx, 0).toString();
        final List<String> titles = new ArrayList<String>();

        if (infoEmailIdMap.containsKey(info)) {
            Set<Integer> docIdSet = infoEmailIdMap.get(info);

            for (Integer id : docIdSet) {
                titles.add(idTitleMap.get(id));
            }
        }

        Collections.sort(titles);
        
        if(SwingUtilities.isEventDispatchThread()){
        	for (String title : titles) {
                listModel.addElement(title);
            }
        }else{
        	final DefaultListModel flistModel=listModel;
        	SwingUtilities.invokeLater(new Runnable(){
        		public void run(){
        			for (String title : titles) {
        	            flistModel.addElement(title);
        	        }
        		}
        	});
        }
        
    }
}
