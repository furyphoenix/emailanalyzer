package neos.app.gui;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


public class NeosNetworkViewer<V extends NeosVertex, E extends NeosEdge>
    extends JFrame {
    //custom code start here
    private final static String[] layoutTypes = { "自适应", "信息流" };
    private JPanel contentPane;
    private JTextField textFieldVertex;
    private Graph<V, E> m_graph;
    private Graph<V, E> currGraph;
    private Layout<V, E> m_layout;
    private VisualizationViewer<V, E> m_viewer;
    private JPanel panelCenter;
    private JEditorPane editorPane;
    private JComboBox comboBoxLayoutType;
    private SpinnerNumberModel strengthModel;

    /**
     * Create the frame.
     */
    public NeosNetworkViewer(Graph<V, E> graph) {
        setIconImage(Toolkit.getDefaultToolkit()
                            .getImage(NeosNetworkViewer.class.getResource(
                    "/icon/1307955269_applications-systemg-icon.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JPanel panel = new JPanel();
        contentPane.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));

        JLabel labelStatus = new JLabel("  ");
        panel.add(labelStatus, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        panel.add(progressBar, BorderLayout.EAST);

        panelCenter = new JPanel();
        contentPane.add(panelCenter, BorderLayout.CENTER);
        panelCenter.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panelCenter.add(panel_2, BorderLayout.SOUTH);

        JLabel label_4 = new JLabel("\u5173\u8054\u5F3A\u5EA6");
        panel_2.add(label_4);

        strengthModel=new SpinnerNumberModel();
        strengthModel.setMinimum(1);
        strengthModel.setStepSize(1);
        JSpinner spinnerLinkStrength = new JSpinner(strengthModel);
        spinnerLinkStrength.setPreferredSize(new Dimension(60, 22));
        panel_2.add(spinnerLinkStrength);

        JLabel label_5 = new JLabel("    ");
        panel_2.add(label_5);

        JCheckBox checkBoxRemoveIsolated = new JCheckBox(
                "\u79FB\u9664\u5B64\u7ACB\u8282\u70B9");
        panel_2.add(checkBoxRemoveIsolated);
        
        JLabel label_7 = new JLabel("  ");
        panel_2.add(label_7);
        
        JButton buttonRestore = new JButton("\u8FD8\u539F");
        panel_2.add(buttonRestore);

        JPanel panelGraphContainer = new JPanel();
        panelCenter.add(panelGraphContainer, BorderLayout.CENTER);
        panelGraphContainer.setLayout(new BorderLayout(0, 0));

        JToolBar toolBar = new JToolBar();
        contentPane.add(toolBar, BorderLayout.NORTH);

        JButton btnTransMode = new JButton("");
        btnTransMode.setMaximumSize(new Dimension(24, 24));
        btnTransMode.setMinimumSize(new Dimension(24, 24));
        btnTransMode.setPreferredSize(new Dimension(24, 24));
        btnTransMode.setIcon(new ImageIcon(NeosNetworkViewer.class.getResource(
                    "/icon/1307955175_transform-move-icon.png")));
        btnTransMode.setToolTipText("\u53D8\u6362\u6A21\u5F0F");
        toolBar.add(btnTransMode);

        JButton btnPickMode = new JButton("");
        btnPickMode.setIcon(new ImageIcon(NeosNetworkViewer.class.getResource(
                    "/icon/1307955935_select-rectangular-icon.png")));
        btnPickMode.setToolTipText("\u9009\u53D6\u6A21\u5F0F");
        toolBar.add(btnPickMode);

        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        toolBar.add(separator);

        JButton btnSelNeighbour = new JButton("");
        btnSelNeighbour.setIcon(new ImageIcon(
                NeosNetworkViewer.class.getResource("/icon/neighbour.png")));
        btnSelNeighbour.setToolTipText("\u9009\u53D6\u76F8\u90BB\u8282\u70B9");
        toolBar.add(btnSelNeighbour);

        JButton btnSelSubgraph = new JButton("");
        btnSelSubgraph.setIcon(new ImageIcon(
                NeosNetworkViewer.class.getResource("/icon/subgraph.png")));
        btnSelSubgraph.setToolTipText("\u9009\u53D6\u8FDE\u901A\u5B50\u56FE");
        toolBar.add(btnSelSubgraph);

        JButton btnDesel = new JButton("");
        btnDesel.setIcon(new ImageIcon(NeosNetworkViewer.class.getResource(
                    "/icon/deselect.png")));
        btnDesel.setToolTipText("\u53D6\u6D88\u9009\u62E9");
        toolBar.add(btnDesel);

        JSeparator separator_1 = new JSeparator();
        separator_1.setOrientation(SwingConstants.VERTICAL);
        toolBar.add(separator_1);

        JButton btnSaveImage = new JButton("");
        btnSaveImage.setIcon(new ImageIcon(NeosNetworkViewer.class.getResource(
                    "/icon/1307956007_document-save-icon.png")));
        btnSaveImage.setToolTipText("\u4FDD\u5B58\u56FE\u50CF");
        toolBar.add(btnSaveImage);

        JSeparator separator_2 = new JSeparator();
        separator_2.setOrientation(SwingConstants.VERTICAL);
        toolBar.add(separator_2);

        JLabel label_2 = new JLabel("    ");
        toolBar.add(label_2);

        JLabel lblNewLabel_1 = new JLabel("\u8282\u70B9\u5E03\u5C40  ");
        toolBar.add(lblNewLabel_1);

        comboBoxLayoutType = new JComboBox(layoutTypes);
        toolBar.add(comboBoxLayoutType);

        JLabel label_3 = new JLabel("    ");
        toolBar.add(label_3);

        JSeparator separator_3 = new JSeparator();
        separator_3.setOrientation(SwingConstants.VERTICAL);
        toolBar.add(separator_3);

        JLabel label = new JLabel("    ");
        toolBar.add(label);

        JLabel lblNewLabel = new JLabel("\u641C\u7D22\u8282\u70B9  ");
        toolBar.add(lblNewLabel);

        textFieldVertex = new JTextField();
        toolBar.add(textFieldVertex);
        textFieldVertex.setColumns(10);

        JLabel label_1 = new JLabel("  ");
        toolBar.add(label_1);

        JButton btnSearch = new JButton(" \u641C \u7D22 ");
        toolBar.add(btnSearch);

        JPanel panel_1 = new JPanel();
        panel_1.setPreferredSize(new Dimension(200, 10));
        panel_1.setMinimumSize(new Dimension(200, 10));
        contentPane.add(panel_1, BorderLayout.EAST);
        panel_1.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane);

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        scrollPane.setViewportView(editorPane);

        //custom code start here
        init(graph);
    }

    private void init(Graph<V, E> graph) {
        m_graph = graph;
        m_layout = new FRLayout<V, E>(m_graph);
        m_viewer=new VisualizationViewer<V,E> (m_layout);
        GraphZoomScrollPane graphPane=new GraphZoomScrollPane(m_viewer);
    }
    
    public VisualizationViewer getViewer(){
    	return m_viewer;
    }
    
    //public void set
}
