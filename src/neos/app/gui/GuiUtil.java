package neos.app.gui;

import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;



public class GuiUtil {
    public static void addStringToJList(String str, JList list) {
        if ((str == null) || (list == null)) {
            return;
        }

        DefaultListModel lstModel = (DefaultListModel) list.getModel();

        if (!lstModel.contains(str)) {
            lstModel.addElement(str);
        }

        list.updateUI();
    }

    public static void addStringsToJList(Collection<String> strs, JList list) {
        if ((strs == null) || (list == null)) {
            return;
        }

        DefaultListModel lstModel = (DefaultListModel) list.getModel();
        Iterator<String> iter = strs.iterator();

        while (iter.hasNext()) {
            String str = iter.next();

            if ((str != null) && (!lstModel.contains(str))) {
                lstModel.addElement(str);
            }
        }

        list.updateUI();
    }

    public static void addStringsToJList(String[] strs, JList list) {
        if ((strs == null) || (list == null)) {
            return;
        }

        DefaultListModel lstModel = (DefaultListModel) list.getModel();

        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];

            if ((str != null) && (!lstModel.contains(str))) {
                lstModel.addElement(str);
            }
        }

        list.updateUI();
    }

    public static void removeSelFromJList(JList list) {
        if (list == null) {
            return;
        }

        DefaultListModel lstModel = (DefaultListModel) list.getModel();
        int[] indices = list.getSelectedIndices();

        for (int i = indices.length - 1; i >= 0; i--) {
            lstModel.remove(indices[i]);
        }

        list.updateUI();
    }

    public static void removeAllFromJList(JList list) {
        if (list == null) {
            return;
        }

        DefaultListModel lstModel = (DefaultListModel) list.getModel();

        lstModel.clear();
        list.updateUI();
    }

    public static void copyAllStringsFromJList(JList list) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.getModel().getSize(); i++) {
            sb.append(list.getModel().getElementAt(i) + "\r\n");
        }

        selection = new StringSelection(sb.toString());
        clip.setContents(selection, null);
    }

    public static void copySelStringsFromJList(JList list) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection;
        StringBuilder sb = new StringBuilder();
        Object[] selValues = list.getSelectedValues();

        if (selValues.length > 0) {
            for (int i = 0; i < selValues.length; i++) {
                sb.append(selValues[i] + "\r\n");
            }

            selection = new StringSelection(sb.toString());
            clip.setContents(selection, null);
        }
    }

    public static void copyStringsToClipboard(Collection<String> strs) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection;
        StringBuilder sb = new StringBuilder();

        if (strs == null) {
            return;
        }

        for (String str : strs) {
            sb.append(str + "\r\n");
        }

        selection = new StringSelection(sb.toString());
        clip.setContents(selection, null);
    }

    public static void pasteStringsToJList(JList list) {
        String str = null;

        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable clipT = clip.getContents(null);

            if (clipT != null) {
                if (clipT.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    str = (String) clipT.getTransferData(DataFlavor.stringFlavor);
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }

        String[] items = null;

        if (str != null) {
            items = str.split("\\s+");
        }

        addStringsToJList(items, list);
    }

    public static void removeSelectedRowFromJTable(JTable table) {
        if (table == null) {
            return;
        }

        int[] rows = table.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = rows.length - 1; i >= 0; i--) {
            model.removeRow(rows[i]);
        }

        //table.updateUI();
    }

    public static List<String> getSelectedRowValueInCol(JTable table, int col) {
        List<String> lst = new ArrayList<String>();

        if ((table == null) || (col < 0) ||
                (col >= table.getModel().getColumnCount())) {
            return lst;
        }

        int[] rows = table.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < rows.length; i++) {
            lst.add(model.getValueAt(rows[i], col).toString());
        }

        return lst;
    }

    public static ArrayList<String> getRowValueInCol(JTable table, int col) {
        ArrayList<String> lst = new ArrayList<String>();

        if ((table == null) || (col < 0) ||
                (col >= table.getModel().getColumnCount())) {
            return lst;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int i = 0; i < model.getRowCount(); i++) {
            lst.add(model.getValueAt(i, col).toString());
        }

        return lst;
    }

    public static void writePngImage(VisualizationViewer viewer, File file) {
        int width = viewer.getWidth();
        int height = viewer.getHeight();
        BufferedImage bi = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bi.createGraphics();

        viewer.paint(graphics);
        graphics.dispose();

        try {
            ImageIO.write(bi, "png", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <E> void copySelVertexFromViewer(
        VisualizationViewer<String, E> viewer) {
        if (viewer != null) {
            Set<String> vertexs = viewer.getPickedVertexState().getPicked();
            StringBuilder sb = new StringBuilder();

            for (String vertex : vertexs) {
                sb.append(vertex + "\r\n");
            }

            StringSelection selection = new StringSelection(sb.toString());
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

            clip.setContents(selection, null);
        }
    }
    
    public static List<File> listFilesByFilter(File file, FileFilter filter){
    	List<File> lst=new ArrayList<File> ();
    	
    	if(file.isDirectory()){
    		File[] children=file.listFiles();
    		for(int i=0; i<children.length; i++){
    			lst.addAll(listFilesByFilter(children[i], filter));
    		}
    	}else{
    		if((filter==null)||(filter.accept(file))){
    			lst.add(file);
    		}
    	}
    	
    	return lst;
    }
    
    public static  Map.Entry []  sortMap(Hashtable tab, boolean key){
		Set set=tab.entrySet();
		Map.Entry [] entryArray=(Map.Entry [])set.toArray(new Map.Entry[set.size()]);
		final boolean is=key;
		Arrays.sort(entryArray, new Comparator(){
			@Override
			public int compare(Object arg0, Object arg1) {
				if(is){
					Object key0=((Map.Entry)arg0).getKey();
					Object key1=((Map.Entry)arg1).getKey();
					return ((Comparable)key0).compareTo((Comparable)key1);
				}else{
					Object val0=((Map.Entry)arg0).getValue();
					Object val1=((Map.Entry)arg1).getValue();
					return ((Comparable)val0).compareTo((Comparable)val1);
				}
			}
			
		});
		
		return entryArray;
	}
}
