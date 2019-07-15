/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template privateFolder, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.ui;

import com.mxgraph.examples.swing.GraphEditor;
import static com.mxgraph.examples.swing.GraphEditor.numberFormat;
import com.mxgraph.examples.swing.editor.BasicGraphEditor;
import com.mxgraph.examples.swing.editor.EditorPalette;
import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.handler.mxConnectionHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;
import com.techducat.codeblocks.logic.BaseBlock;
import com.techducat.codeblocks.logic.Basket;
import com.techducat.codeblocks.logic.Decision;
import com.techducat.codeblocks.logic.Lexicon;
import com.techducat.codeblocks.logic.LinearEquation;
import com.techducat.codeblocks.logic.Looper;
import com.techducat.codeblocks.logic.State;
import com.techducat.codeblocks.logic.Thing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.w3c.dom.Document;

/**
 *
 * @author odada
 */
public final class AssemblyWorkbench extends BasicGraphEditor {

    private static final Logger LOGGER = Logger.getLogger(AssemblyWorkbench.class.getName());

    private final mxGraph graph;

    private static JButton executeButton;

    private static JButton saveButton;

    private InputDialog inputDialog;

    private JFrame frame;

    private CodeAnimationGlassPane glass;
    
    private Properties applicationProperties;

    private mxGraph getGraph() {
        return getGraphComponent().getGraph();
    }

    private void cleanupGraph() {

        mxCell root = (mxCell) graph.getModel().getRoot();
        mxCell actualRoot = (mxCell) root.getChildAt(0);

        Object[] cells = graph.getChildCells(actualRoot);

        mxCell cell;
        for (Object c : cells) {
            cell = (mxCell) c;
            if (cell.isVertex()) {
                if (graph.getModel().getEdgeCount(c) == 0) {
                    actualRoot.remove(cell);
                    LOGGER.log(Level.INFO, "Removing hanging vertex...{0}", cell);
                }
            }
        }

        Basket.resetAll();
        LinearEquation.resetAll();
    }

    public void init() {
        try {
            InputStream stream = Lexicon.class.getResourceAsStream("/com/techducat/codeblocks/resources/application.properties");
            applicationProperties = new Properties();
            applicationProperties.load(stream);
            mxSwingConstants.SHADOW_COLOR = Color.LIGHT_GRAY;
            mxConstants.W3C_SHADOWCOLOR = "#D3D3D3";
            
            frame = this.createFrame(null);
//        frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            
            JPanel controlPane = new JPanel(new GridLayout(2, 1));
            controlPane.setOpaque(false);
            glass = new CodeAnimationGlassPane(frame.getJMenuBar(), frame.getContentPane());
            glass.setLayout(new GridLayout(0, 1));
            glass.setOpaque(false);
            glass.add(new JLabel()); // padding...
            glass.add(new JLabel());
            glass.add(controlPane);
            glass.add(new JLabel());
            glass.add(new JLabel());
            frame.setGlassPane(glass);
            
            executeButton.addActionListener((ActionEvent e) -> {
                try {
                    launchInputDialog();
                } catch (IOException ex) {
                    Logger.getLogger(AssemblyWorkbench.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            
            saveButton.addActionListener((ActionEvent e) -> {
                showSaveDialog();
            });
        } catch (IOException ex) {
            Logger.getLogger(AssemblyWorkbench.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void launchInputDialog() throws IOException {
        //TODO Remember to internationalize/localize
        inputDialog = new InputDialog(frame, "Object Selection", "Pick your objects from the palette", glass);
        inputDialog.setVisible(true);
        inputDialog.setSize(1050, 700);
        inputDialog.setAlwaysOnTop(false);
        CustomThingPalette variousObjectPalette = inputDialog.insertPalette("Various");//TODO Internationalized messages
        CustomThingPalette animalObjectPalette = inputDialog.insertPalette("Animals");//TODO Internationalized messages
        CustomThingPalette houseObjectPalette = inputDialog.insertPalette("Household");//TODO Internationalized messages
        CustomThingPalette fruitObjectPalette = inputDialog.insertPalette("Fruits");//TODO Internationalized messages

        animalObjectPalette.setDropTarget(inputDialog.getDropTarget());
        animalObjectPalette.setName("Animal");
        animalObjectPalette.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                //TODO Use id instead which will be used for category lookup
                Thing.componentName = e.getComponent().getName();
                LOGGER.log(Level.INFO, "Thing.componentName={0}", Thing.componentName);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //TODO Use id instead which will be used for category lookup
                Thing.componentName = e.getComponent().getName();
                LOGGER.log(Level.INFO, "Thing.componentName={0}", Thing.componentName);
            }
        });
        animalObjectPalette.addListener(mxEvent.SELECT, (Object sender, mxEventObject evt) -> {
            Object tmp = evt.getProperty("transferable");

            if (tmp instanceof mxGraphTransferable) {
                mxGraphTransferable t = (mxGraphTransferable) tmp;
                Object cell = t.getCells()[0];

                if (graph.getModel().isEdge(cell)) {
                    ((AssemblyWorkbench.CustomGraph) graph).setEdgeTemplate(cell);
                }
            }
        });

        //TODO Remember to internationalize/localize
        variousObjectPalette.setName("Various");

        variousObjectPalette
                .addTemplate(
                        "Airplane",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/general_airplane.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/general_airplane.png",
                        100, 100, "Airplane");
        variousObjectPalette
                .addTemplate(
                        "Bottle",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/general_bottle.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/general_bottle.png",
                        100, 100, "Bottle");
        variousObjectPalette
                .addTemplate(
                        "Truck",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/general_truck.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/general_truck.png",
                        100, 100, "Truck");

        animalObjectPalette
                .addTemplate(
                        "Horse",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/animal_horse.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/animal_horse.png",
                        100, 100, "Horse");
        animalObjectPalette
                .addTemplate(
                        "Chicken",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/animal_chicken.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/animal_chicken.png",
                        100, 100, "Chicken");
        animalObjectPalette
                .addTemplate(
                        "Lion",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/animal_lion.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/animal_lion.png",
                        100, 100, "Lion");

        fruitObjectPalette
                .addTemplate(
                        "Apple",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/fruit_apple.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/fruit_apple.png",
                        100, 100, "Apple");
        fruitObjectPalette
                .addTemplate(
                        "Banana",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/fruit_banana.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/fruit_banana.png",
                        100, 100, "Banana");
        fruitObjectPalette
                .addTemplate(
                        "Mango",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/fruit_mango.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/fruit_mango.png",
                        100, 100, "Mango");

        houseObjectPalette
                .addTemplate(
                        "Basket",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/house_basket.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/house_basket.png",
                        100, 100, "Basket");
        houseObjectPalette
                .addTemplate(
                        "Fan",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/house_fan.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/house_fan.png",
                        100, 100, "Fan");
        houseObjectPalette
                .addTemplate(
                        "Chair",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/thing/icons/house_chair.png")),
                        "image;image=/com/techducat/codeblocks/thing/icons/house_chair.png",
                        100, 100, "Chair");
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            LOGGER.warning(e1.getLocalizedMessage());
        }
        java.awt.EventQueue.invokeLater(() -> {
            CustomGraphComponent graphComponent = new CustomGraphComponent(new CustomGraph());
            JButton button = new JButton(new ImageIcon(
                    AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/icons/play.png")));

            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setEnabled(false);

            JButton button2 = new JButton(new ImageIcon(
                    AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/icons/save.png")));
            button2.setOpaque(false);
            button2.setContentAreaFilled(false);
            button2.setEnabled(false);

            AssemblyWorkbench workbench = new AssemblyWorkbench("CodeBlocks Bench", graphComponent, button, button2);
            SplashScreen sc = new SplashScreen(workbench);
            sc.setVisible(true);
        });

    }
    
    private void showSaveDialog() {
        String customBlockName = JOptionPane.showInputDialog(frame, "What should we call your work?");
        if(customBlockName != null) {
            if(customBlockName.matches("[0-9a-zA-Z]+")) {
                save(customBlockName);
            } else {
                JOptionPane.showMessageDialog(this, "The chosen file name should not contain any special characters like *,?, %. Only letters (a to z) and numbers (0 to 9) allowed: " + customBlockName, "Wrong Format", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public final void save(String customBlockName) {
        mxCell root = (mxCell) graph.getModel().getRoot();
        mxCell actualRoot = (mxCell) root.getChildAt(0);
        File privateFolder = new File(applicationProperties.getProperty("library.folder"));
        if(!privateFolder.exists()) {
            privateFolder.mkdirs();
        }
        String fileName =  privateFolder.getAbsolutePath() + customBlockName + "_" + System.currentTimeMillis() + ".csv";

        Object[] cells = graph.getChildCells(actualRoot);
        try (PrintWriter writer = new PrintWriter(fileName, "UTF-8")) {
            mxCell cell;
            mxCell edge;
            mxCellState edgeState;
            mxGeometry vertexGeom;
            CustomMxCell vert;
            List<mxPoint> points;
            for (Object c : cells) {
                cell = (mxCell) c;
                if (cell.isVertex()) {
                    vert = (CustomMxCell) cell;
                    vertexGeom = graph.getModel().getGeometry(cell);
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "\"" + vert.getName() + "\"", cell.isVertex(), cell.getId(), "\"" + cell.getValue() + "\"", "\"" + cell.getStyle() + "\"", "\"" + vertexGeom + "\"", "", "", "");
                    Object[] currentEdges = graph.getOutgoingEdges(cell);
                    for (Object e : currentEdges) {
                        edge = (mxCell) e;
                        edgeState = graph.getView().getState(e);
                        points = edgeState.getAbsolutePoints();
                        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", "", edge.isVertex(), edge.getId(), "\"" + edge.getValue() + "\"", "\"" + edge.getStyle() + "\"", "", edge.getSource().getId(), edge.getTarget().getId(), "\"" + points + "\"");
                    }
                }
                //TODO Add the saved custom Block to the private library
            }
            saveButton.setEnabled(false);
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(AssemblyWorkbench.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog(this, "Workspace saved to " + fileName, "Save Done", JOptionPane.INFORMATION_MESSAGE);
    }

    public AssemblyWorkbench(String appTitle, mxGraphComponent component, JButton runButton, JButton saveButton) {
        super(appTitle, component, runButton, saveButton);
        graph = graphComponent.getGraph();
        AssemblyWorkbench.executeButton = (JButton) runButton;
        AssemblyWorkbench.saveButton = (JButton) saveButton;

        // Creates the blocks palette
        //TODO Remember to internationalize/localize
        CustomEditorPalette generalFunctionPalette = insertPalette(mxResources.get("general"));
        CustomEditorPalette textFunctionPalette = insertPalette(mxResources.get("text"));
        CustomEditorPalette mathFunctionPalette = insertPalette(mxResources.get("math"));
        CustomEditorPalette animationFunctionPalette = insertPalette(mxResources.get("animation"));
        CustomEditorPalette privateFunctionPalette = insertPalette(mxResources.get("library"));
        CustomEditorPalette sharedFunctionPalette = insertPalette(mxResources.get("shared"));

        mxEventSource.mxIEventListener eventListener = (Object sender, mxEventObject evt) -> {
            Object tmp = evt.getProperty("transferable");

            if (tmp instanceof mxGraphTransferable) {
                mxGraphTransferable t = (mxGraphTransferable) tmp;
                Object cell = t.getCells()[0];

                if (graph.getModel().isEdge(cell)) {
                    ((AssemblyWorkbench.CustomGraph) graph).setEdgeTemplate(cell);
                }
            }
        };

        // Sets the edge template to be used for creating new edges if an edge
        // is clicked in the shape palette
        generalFunctionPalette.addListener(mxEvent.SELECT, eventListener);
        textFunctionPalette.addListener(mxEvent.SELECT, eventListener);
        mathFunctionPalette.addListener(mxEvent.SELECT, eventListener);

        //TODO Remember to internationalize/localize
        // Adds some template cells for dropping into the graph
        generalFunctionPalette
                .addTemplate(
                        "Looper",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/looper.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_looper2.png", 200, 200, Looper.LooperType.LIST);
        generalFunctionPalette
                .addTemplate(
                        "Equal",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/eq.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_branch_eq.png",
                        200, 200, "0");
        generalFunctionPalette
                .addTemplate(
                        "Is Type",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/is.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_branch_is.png",
                        200, 200, "0");
        generalFunctionPalette
                .addTemplate(
                        "Greater",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/ge.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_branch_ge.png",
                        200, 200, "0");
        generalFunctionPalette
                .addTemplate(
                        "Less",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/le.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_branch_le.png",
                        200, 200, "0");
        generalFunctionPalette
                .addTemplate(
                        "Basket",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/collect.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_collect2.png",
                        200, 200, "Basket");
        generalFunctionPalette
                .addTemplate(
                        "Combinator",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/combination.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_combination2.png",
                        200, 200, "1");
        generalFunctionPalette
                .addTemplate(
                        "Permutator",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/permutation.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_permutation2.png",
                        200, 200, "1");
        generalFunctionPalette
                .addTemplate(
                        "Organizer",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/sort.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_sort2.png",
                        200, 200, "0");

        textFunctionPalette
                .addTemplate(
                        "Anagram",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/anagram.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_anagram2.png",
                        200, 200, "Anagram");
        textFunctionPalette
                .addTemplate(
                        "DayCalc",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/calendar.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_calendar2.png",
                        200, 200, "0");
        textFunctionPalette
                .addTemplate(
                        "Char Counter",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/charcount.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_charcount2.png",
                        200, 200, "");
        textFunctionPalette
                .addTemplate(
                        "Reverser",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/reverse.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_reverse2.png",
                        200, 200, "Reverser");
        textFunctionPalette
                .addTemplate(
                        "Splitter",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/split.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_split2.png",
                        200, 200, " ");
        textFunctionPalette
                .addTemplate(
                        "Word Counter",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/wordcount.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_wordcount2.png",
                        200, 200, "");
        textFunctionPalette
                .addTemplate(
                        "Lexicon",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/lexicon.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_lexicon2.png",
                        200, 200, "Lexicon");

        mathFunctionPalette
                .addTemplate(
                        "Fibonacci",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/spiral.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_spiral2.png",
                        200, 200, "Fibonacci");
        mathFunctionPalette
                .addTemplate(
                        "Sequence",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/sequence.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_sequence2.png",
                        200, 200, "n");
        mathFunctionPalette
                .addTemplate(
                        "Series",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/series.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_series2.png",
                        200, 200, "n");
        mathFunctionPalette
                .addTemplate(
                        "Linear Equation",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/linear.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_linear2.png",
                        200, 200, "Linear Equation");
        mathFunctionPalette
                .addTemplate(
                        "Quadratic Equation",
                        new ImageIcon(
                                AssemblyWorkbench.class
                                .getResource("/com/techducat/codeblocks/icons/quadratic.png")),
                        "image;image=/com/techducat/codeblocks/icons/block_quadratic2.png",
                        200, 200, "Quadratic Equation");

        setOpaque(false);
    }

    /**
     *
     */
    public static class CustomGraphComponent extends mxGraphComponent {

        /**
         *
         */
        private static final long serialVersionUID = -6833603133512882012L;

        /**
         *
         * @param graph
         */
        public CustomGraphComponent(mxGraph graph) {//TODO pass reference to Tree/Graph structure to hold BaseBlock instances 
            super(graph);

            // Sets switches typically used in an editor
            setPageVisible(false);
            setGridVisible(true);
            setToolTips(true);
            setInheritsPopupMenu(true);

            getConnectionHandler().setCreateTarget(true);

            // Loads the defalt stylesheet from an external privateFolder
            mxCodec codec = new mxCodec();
            Document doc = mxUtils.loadDocument(GraphEditor.class.getResource(
                    "/com/techducat/codeblocks/resources/default-style.xml")
                    .toString());
            mxStylesheet stylesheet = (mxStylesheet) codec.decode(doc.getDocumentElement(), graph.getStylesheet());

            // Sets the background to white
            getViewport().setOpaque(true);
            getViewport().setBackground(Color.WHITE);
        }

        @Override
        protected mxConnectionHandler createConnectionHandler() {
            return new CustomConnectionHandler(this);
        }

        /**
         * Overrides drop behaviour to set the cell style if the target is not a
         * valid drop target and the cells are of the same type (eg. both
         * vertices or both edges).
         *
         * @param cells
         * @param target
         * @param dy
         * @param dx
         * @param location
         * @return
         */
        @Override
        public Object[] importCells(Object[] cells, double dx, double dy,
                Object target, Point location) {
            if (target == null && cells.length == 1 && location != null) {
                target = getCellAt(location.x, location.y);

                if (target instanceof mxICell && cells[0] instanceof mxICell) {
                    mxICell targetCell = (mxICell) target;
                    mxICell dropCell = (mxICell) cells[0];

                    if (targetCell.isVertex() == dropCell.isVertex()
                            || targetCell.isEdge() == dropCell.isEdge()) {
                        mxIGraphModel model = graph.getModel();
                        model.setStyle(target, model.getStyle(cells[0]));
                        graph.setSelectionCell(target);

                        return null;
                    }
                }
            }

            return super.importCells(cells, dx, dy, target, location);
        }

    }

    public static class CustomConnectionHandler extends mxConnectionHandler {

        public CustomConnectionHandler(CustomGraphComponent graphComponent) {
            super(graphComponent);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (first != null) {
                double dx = first.getX() - e.getX();
                double dy = first.getY() - e.getY();

                Object cell = connectPreview.stop(graphComponent.isSignificant(dx, dy), e);
            }
        }

        @Override
        public String validateConnection(Object source, Object target) {
            String validationError = super.validateConnection(source, target); //To change body of generated methods, choose Tools | Templates.

            //TODO Create corresponding codeblocks.logic instances
            //TODO Insert created edge ref in CodeBlocks to assist in deletion, update (of edges and nodes) and animation
            if (validationError == null && source != null & target != null) {
                CustomMxCell sourceCell = (CustomMxCell) source;
                CustomMxCell targetCell = (CustomMxCell) target;
                mxCell connectingEdge = (mxCell) connectPreview.getPreviewState().getCell();
                Class sourceClass = BaseBlock.INSTANCE_MAP.get(sourceCell.getName());
                Class targetClass = BaseBlock.INSTANCE_MAP.get(targetCell.getName());

                try {
                    BaseBlock src = sourceCell.getBlock();
                    BaseBlock tgt = targetCell.getBlock();
                    if (sourceClass != null && targetClass != null) {

                        if (src == null) {
                            sourceCell.mxCell2BaseBlock(sourceCell.getName(), sourceCell, sourceClass);
                            src = sourceCell.getBlock();
                        }
                        if (tgt == null) {
                            targetCell.mxCell2BaseBlock(targetCell.getName(), targetCell, targetClass);
                            tgt = targetCell.getBlock();
                        }

                        if (src instanceof Decision) {
                            Decision dsn = (Decision) src;
                            //Decision can only have 2 branches. Return not null validationError
                            //Add "Yes" label to first created edge and "No" to the second
                            Decision.createLink(dsn, tgt, sourceCell, targetCell, connectingEdge);
                        } else {
                            BaseBlock.createLink(src, tgt, sourceCell, targetCell);
                        }

                        if (validationError == null) {
                            AssemblyWorkbench.executeButton.setEnabled(true);
                        }
                    }
                } catch (InstantiationException | IllegalAccessException ex) {
                    Logger.getLogger(mxConnectionHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return validationError;
        }
    }

    /**
     * A graph that creates new edges from a given template edge.
     */
    public static class CustomGraph extends mxGraph {

        /**
         * Holds the edge to be used as a template for inserting new edges.
         */
        protected Object edgeTemplate;

        /**
         * Custom graph that defines the alternate edge style to be used when
         * the middle control point of edges is double clicked (flipped).
         */
        public CustomGraph() {
            setAlternateEdgeStyle("edgeStyle=mxEdgeStyle.ElbowConnector;elbow=vertical");
        }

        /**
         * Sets the edge template to be used to inserting edges.
         *
         * @param template
         */
        public void setEdgeTemplate(Object template) {
            edgeTemplate = template;
        }

        /**
         * Prints out some useful information about the cell in the tooltip.
         *
         * @param cell
         * @return
         */
        @Override
        public String getToolTipForCell(Object cell) {
            String tip = "<html>";
            mxGeometry geo = getModel().getGeometry(cell);
            mxCellState state = getView().getState(cell);

            if (getModel().isEdge(cell)) {
                tip += "points={";

                if (geo != null) {
                    List<mxPoint> points = geo.getPoints();

                    if (points != null) {
                        Iterator<mxPoint> it = points.iterator();

                        while (it.hasNext()) {
                            mxPoint point = it.next();
                            tip += "[x=" + numberFormat.format(point.getX())
                                    + ",y=" + numberFormat.format(point.getY())
                                    + "],";
                        }

                        tip = tip.substring(0, tip.length() - 1);
                    }
                }

                tip += "}<br>";
                tip += "absPoints={";

                if (state != null) {

                    for (int i = 0; i < state.getAbsolutePointCount(); i++) {
                        mxPoint point = state.getAbsolutePoint(i);
                        tip += "[x=" + numberFormat.format(point.getX())
                                + ",y=" + numberFormat.format(point.getY())
                                + "],";
                    }

                    tip = tip.substring(0, tip.length() - 1);
                }

                tip += "}";
            } else {
                tip += "geo=[";

                if (geo != null) {
                    tip += "x=" + numberFormat.format(geo.getX()) + ",y="
                            + numberFormat.format(geo.getY()) + ",width="
                            + numberFormat.format(geo.getWidth()) + ",height="
                            + numberFormat.format(geo.getHeight());
                }

                tip += "]<br>";
                tip += "state=[";

                if (state != null) {
                    tip += "x=" + numberFormat.format(state.getX()) + ",y="
                            + numberFormat.format(state.getY()) + ",width="
                            + numberFormat.format(state.getWidth())
                            + ",height="
                            + numberFormat.format(state.getHeight());
                }

                tip += "]";
            }

            mxPoint trans = getView().getTranslate();

            tip += "<br>scale=" + numberFormat.format(getView().getScale())
                    + ", translate=[x=" + numberFormat.format(trans.getX())
                    + ",y=" + numberFormat.format(trans.getY()) + "]";
            tip += "</html>";

            return tip;
        }

        /**
         * Overrides the method to use the currently selected edge template for
         * new edges.
         *
         * @param parent
         * @param id
         * @param value
         * @param source
         * @param target
         * @param style
         * @return
         */
        @Override
        public Object createEdge(Object parent, String id, Object value,
                Object source, Object target, String style) {
            style = "strokeWidth=10";

            if (edgeTemplate != null) {
                mxCell edge = (mxCell) cloneCells(new Object[]{edgeTemplate})[0];
                edge.setId(id);

                return edge;
            }

            return super.createEdge(parent, id, value, source, target, style);
        }

    }

    public class CodeAnimationGlassPane extends JPanel implements MouseListener,
            MouseMotionListener, FocusListener {

        Toolkit toolkit;

        JMenuBar menuBar;

        Container contentPane;

        boolean inDrag = false;

        boolean needToRedispatch = false;

        public CodeAnimationGlassPane(JMenuBar mb, Container cp) {
            toolkit = Toolkit.getDefaultToolkit();
            menuBar = mb;
            contentPane = cp;
            addMouseListener(this);
            addMouseMotionListener(this);
            addFocusListener(this);
        }

        @Override
        public void setVisible(boolean v) {
            if (v) {
                requestFocus();
            }
            super.setVisible(v);
        }

        @Override
        public void focusLost(FocusEvent fe) {
            if (isVisible()) {
                requestFocus();
            }
        }

        @Override
        public void focusGained(FocusEvent fe) {
        }

        public void setNeedToRedispatch(boolean need) {
            needToRedispatch = need;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (needToRedispatch) {
                redispatchMouseEvent(e);
                inDrag = false;
            }
        }

        private void redispatchMouseEvent(MouseEvent e) {
            boolean inButton;
            boolean inMenuBar = false;
            Point glassPanePoint = e.getPoint();
            Component component;
            Container container = contentPane;
            Point containerPoint = SwingUtilities.convertPoint(this,
                    glassPanePoint, contentPane);
            int eventID = e.getID();

            if (containerPoint.y < 0) {
                inMenuBar = true;
                container = menuBar;
                containerPoint = SwingUtilities.convertPoint(this, glassPanePoint,
                        menuBar);
                testForDrag(eventID);
            }

            component = SwingUtilities.getDeepestComponentAt(container,
                    containerPoint.x, containerPoint.y);

            if (component == null) {
                return;
            } else {
                inButton = true;
                testForDrag(eventID);
            }

            if (inMenuBar || inButton || inDrag) {
                Point componentPoint = SwingUtilities.convertPoint(this,
                        glassPanePoint, component);
                component.dispatchEvent(new MouseEvent(component, eventID, e
                        .getWhen(), e.getModifiers(), componentPoint.x,
                        componentPoint.y, e.getClickCount(), e.isPopupTrigger()));
            }
        }

        private void testForDrag(int eventID) {
            if (eventID == MouseEvent.MOUSE_PRESSED) {
                inDrag = true;
            }
        }
    }

    public class CustomEditorPalette extends EditorPalette {

        @Override
        public void addTemplate(String name, ImageIcon icon, String style, int width, int height, Object value) {
            CustomMxCell cell = new CustomMxCell(name, value, new mxGeometry(0, 0, width, height),
                    style);
            cell.setVertex(true);

            addTemplate(name, icon, cell);
        }

    }

    @Override
    public CustomEditorPalette insertPalette(String title) {
        final CustomEditorPalette palette = new CustomEditorPalette();
        final JScrollPane scrollPane = new JScrollPane(palette);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        libraryPane.add(title, scrollPane);

        libraryPane.addComponentListener(new ComponentAdapter() {
            /**
             *
             */
            @Override
            public void componentResized(ComponentEvent e) {
                int w = scrollPane.getWidth()
                        - scrollPane.getVerticalScrollBar().getWidth();
                palette.setPreferredWidth(w);
            }

        });

        return palette;
    }

    public class CustomThingPalette extends EditorPalette {

        @Override
        public void addTemplate(String name, ImageIcon icon, String style, int width, int height, Object value) {
            CustomMxCell cell = new CustomMxCell(name, value, new mxGeometry(0, 0, width, height),
                    style);
            cell.setVertex(true);

            addTemplate(name, icon, cell);
        }

    }

    public class InputDialog extends JDialog {

        private static final long serialVersionUID = 1L;

        private final JSplitPane mainPane;

        private final JTabbedPane libraryPane;

        private final DuffelBag duffelBag;

        private final JButton execButton;

        private Set<Object> things;

        private final CodeAnimationGlassPane glass;

        private final JTextField anythingText;

        private final JButton anythingButton;

        public DuffelBag getBag() {
            return this.duffelBag;
        }

        public CustomThingPalette insertPalette(String title) {
            final CustomThingPalette palette = new CustomThingPalette();
            final JScrollPane scrollPane = new JScrollPane(palette);
            scrollPane
                    .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            scrollPane
                    .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            libraryPane.add(title, scrollPane);

            // Updates the widths of the palettes if the container size changes
            libraryPane.addComponentListener(new ComponentAdapter() {
                /**
                 *
                 */
                @Override
                public void componentResized(ComponentEvent e) {
                    int w = scrollPane.getWidth()
                            - scrollPane.getVerticalScrollBar().getWidth();
                    palette.setPreferredWidth(w);
                }

            });

            return palette;
        }

        public InputDialog(JFrame parent, String title, String message, CodeAnimationGlassPane glass) throws IOException {
            super(parent, title);

            this.glass = glass;

            libraryPane = new JTabbedPane();

            things = new HashSet<>();
            BaseBlock.ANIMATION_QUEUE.clear();
            ImageIcon inputIcon = new ImageIcon(AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/icons/input.png"));
            duffelBag = new DuffelBag(things, inputIcon.getImage());
            ImageIcon execIcon = new ImageIcon(AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/icons/play2.png"));
            execButton = new JButton(execIcon);
            execButton.addActionListener((ActionEvent e) -> {
                launch(things);
            });

            JSplitPane centralPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    duffelBag, execButton);
            centralPanel.setBorder(null);
            centralPanel.setResizeWeight(1);
            centralPanel.setDividerSize(6);

            this.anythingText = new JTextField(10);
            this.anythingText.setFont(new Font(Font.MONOSPACED, Font.BOLD, 25));
            this.anythingText.setHorizontalAlignment(SwingConstants.CENTER);

            ImageIcon anythingIcon = new ImageIcon(AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/thing/icons/anything.png"));
            ImageIcon matrixIcon = new ImageIcon(AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/thing/icons/matrix.png"));
            ImageIcon addIcon = new ImageIcon(AssemblyWorkbench.class.getResource("/com/techducat/codeblocks/icons/add.png"));
            this.anythingButton = new JButton(addIcon);
            this.anythingButton.addActionListener((ActionEvent e) -> {
                Thing t = new Thing();
                t.setName(anythingText.getText());
                //TODO Extend regex to match numbers with decimal points
                if (anythingText.getText().matches("[-+]?\\d+\\s*,[-+]?\\d+\\s*,[-+]?\\d+\\s*")) {
                    Thing.setCategory(t, "Math");
                    String[] matrixElements = anythingText.getText().split(",");
                    double[] elements = new double[matrixElements.length];
                    for (int i = 0; i < matrixElements.length; i++) {
                        elements[i] = Double.parseDouble(matrixElements[i].trim());
                    }
                    t.setValue(elements);
                    t.setIcon(matrixIcon);
                } else {
                    Thing.setCategory(t, "Anything");
                    t.setIcon(anythingIcon);
                }
                double x = duffelBag.getX() + duffelBag.getWidth() / 2 - 10 * things.size();
                double y = duffelBag.getY() + duffelBag.getHeight() / 2 - 10 * things.size();
                t.setX(x);
                t.setY(y);
                t.setWidth(100);
                t.setHeight(100);
                things.add(t);
                LOGGER.log(Level.INFO, "Things: {0}", things);
                duffelBag.repaint();
                anythingText.setText("");
            });

            JPanel anythingPanel = new JPanel();
            anythingPanel.add(anythingText);
            anythingPanel.add(anythingButton);

            JSplitPane thingPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    libraryPane, anythingPanel);
            thingPanel.setBorder(null);
            thingPanel.setResizeWeight(1);
            thingPanel.setDividerSize(6);

            mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    thingPanel, centralPanel);
            mainPane.setDividerLocation(320);
            mainPane.setResizeWeight(0);
            mainPane.setDividerSize(6);
            mainPane.setBorder(null);

            add(mainPane, BorderLayout.CENTER);

            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            pack();
            setVisible(true);
            setSize(800, 700);
        }

        @Override
        public JRootPane createRootPane() {
            JRootPane dialogRootPane = new JRootPane();
            KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
            Action action = new AbstractAction() {

                private static final long serialVersionUID = 1L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    dispose();
                }
            };
            InputMap inputMap = dialogRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            inputMap.put(stroke, "ESCAPE");
            dialogRootPane.getActionMap().put("ESCAPE", action);
            return dialogRootPane;
        }

        class DialogActionListener implements ActionListener {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        }

        @SuppressWarnings("CallToPrintStackTrace")
        public final void launch(Set<Object> things) {
            // Clear all hanging cells
            cleanupGraph();
            glass.setNeedToRedispatch(false);
            glass.setVisible(true);
            executeButton.setEnabled(false);
            mxCell root = (mxCell) graph.getModel().getRoot();
            CustomMxCell actualRoot = (CustomMxCell) root.getChildAt(0).getChildAt(0);
            BaseBlock block = actualRoot.getBlock().findRoot();
            block.setState(new State());
            block.setInputs(things);

            try {
                Future[] fts = block.initiate();

                Object t;
                for (Future f : fts) {
                    try {
                        t = f.get();
                        LOGGER.log(Level.INFO, "Path completed => {0}", new Object[]{t});
                    } catch (InterruptedException | ExecutionException | NullPointerException ex) {
                        ex.printStackTrace();
                    }
                }

                AnimationRunner animation = new AnimationRunner(glass, executeButton, saveButton, graphComponent, actualRoot, this);
                animation.execute();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(AssemblyWorkbench.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    /**
     *
     * @author odada
     */
    public class DuffelBag extends JPanel {

        private final Set<Object> things;

        private final Image background;

        public DuffelBag(Set<Object> things, Image background) {
            this.things = things;
            this.background = background;
            this.setTransferHandler(new DnDDrop(frame, this, things));
            this.setBackground(new Color(0, 0, 0, 0));
            this.setDoubleBuffered(true);
            this.setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            things.stream().forEach((t) -> {
                Thing o = (Thing) t;

                g.drawImage(o.getIcon().getImage(), (int) o.getX(), (int) o.getY(), (int) o.getWidth(), (int) o.getHeight(), this);
                g.setColor(Color.black);
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
                g.drawString(o.getName(), (int) o.getX() + (int) (o.getWidth() / 2 - 7 * o.getName().length() / 2), (int) o.getY() + (int) o.getHeight() / 2);
            });
        }
    }

    public class DnDDrop extends TransferHandler implements DropTargetListener {

        private final DropTarget target;

        private final JFrame frameTemp;

        private final Set<Object> things;

        public DnDDrop(JFrame frame, JPanel drop, Set<Object> things) {
            frameTemp = frame;
            target = new DropTarget(drop, this);
            this.things = things;
        }

        @Override
        public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
            return super.canImport(comp, transferFlavors);
        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
//            LOGGER.log(Level.INFO, "dragEnter: {0}", target.getComponent());
            DropTarget d = (DropTarget) dtde.getSource();
            LOGGER.log(Level.INFO, "dragEnter: {0}", d.getComponent());
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
//            LOGGER.log(Level.INFO, "dragOver: {0}", target.getComponent());
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
//            JOptionPane.showMessageDialog(inputDialog,
//                    "Your selection should be dragged into the bag. Try again.",
//                    "Wrong Area",
//                    JOptionPane.ERROR_MESSAGE);
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
//            LOGGER.log(Level.INFO, "dropActionChanged: {0}", target.getComponent());
        }

        @Override
        @SuppressWarnings("CallToPrintStackTrace")
        public void drop(DropTargetDropEvent dtde) {
            try {
                frameTemp.setCursor(null);

                Transferable tr = dtde.getTransferable();

                mxGraphTransferable dropped = (mxGraphTransferable) tr.getTransferData(mxGraphTransferable.dataFlavor);
                if (dtde.isDataFlavorSupported(mxGraphTransferable.dataFlavor) || dtde.isDataFlavorSupported(DataFlavor.stringFlavor) || dtde.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                    CustomMxCell cell = (CustomMxCell) dropped.getCells()[0];
                    Thing thing = new Thing();
                    Thing.setCategory(thing, Thing.componentName);
                    String imagePath = cell.getStyle().replace("image;image=", "");
                    ImageIcon icon = new ImageIcon(AssemblyWorkbench.class.getResource(imagePath));

                    thing.setIcon(icon);
                    thing.setName((String) cell.getValue());

                    dtde.acceptDrop(DnDConstants.ACTION_COPY);

                    thing.setX(dtde.getLocation().getX());
                    thing.setY(dtde.getLocation().getY());
                    thing.setWidth(cell.getGeometry().getWidth());
                    thing.setHeight(cell.getGeometry().getHeight());

                    if (!things.contains(thing)) {
                        thing.setId(1L);
                    } else {
                        long idx = 1;
                        for (Object thg : things) {
                            if (thg.equals(thing)) {
                                idx++;
                            }
                        }
                        thing.setId(idx);
                    }
                    things.add(thing);
                    LOGGER.log(Level.INFO, "Things: {0}", things);

                    dtde.dropComplete(true);
                    target.getComponent().repaint();
                } else {
                    dtde.rejectDrop();
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                dtde.rejectDrop();
            }

        }
    }
}
