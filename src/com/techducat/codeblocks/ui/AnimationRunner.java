/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.techducat.codeblocks.ui;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.techducat.codeblocks.logic.AnimationContext;
import com.techducat.codeblocks.logic.BaseBlock;
import com.techducat.codeblocks.logic.Decision;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.SwingWorker;

/**
 *
 * @author odada
 */
public class AnimationRunner extends SwingWorker<Integer, List<AnimationContext>> {

    public static final int STROKE_WIDTH_NODE = 10;

    public static final int STROKE_WIDTH_EDGE = 15;

    public static ExecutorService ANIMATION_SERVICE = Executors.newFixedThreadPool(20);

    private static final Logger LOGGER = Logger.getLogger(AnimationRunner.class.getName());

    private final AssemblyWorkbench.CodeAnimationGlassPane glass;

    private final JButton executeButton;

    private final JButton saveButton;

    private final mxGraph graph;

    private final int averageFontWidth;

    private final Object[] cells;

    private final Graphics2D g;

    private int loopIndex;

    private final AssemblyWorkbench.InputDialog dialog;

    //TODO Generate a line of execution and store in a FIFO. The FIFO will be in an item in a parent FIFO
    public AnimationRunner(AssemblyWorkbench.CodeAnimationGlassPane glass, JButton executeButton, JButton saveButton, mxGraphComponent component, CustomMxCell rootCell, AssemblyWorkbench.InputDialog dialog) {
        this.glass = glass;
        this.executeButton = executeButton;
        this.saveButton = saveButton;
        this.graph = component.getGraph();
        this.dialog = dialog;

        //TODO First display rootBlock.retained
        mxCell actualRoot = (mxCell) rootCell.getParent();
        cells = graph.getChildCells(actualRoot);

//        Graphics2D g = (Graphics2D) component.getGraphics();
        g = (Graphics2D) glass.getGraphics();
        //12 is inner divider size * 2
        g.translate(executeButton.getParent().getWidth() + 12, executeButton.getHeight() / 2);

//        rootOuts = new ArrayList<>();
        int[] widths = g.getFontMetrics().getWidths();
        double totalWidth = 0.0;
        for (int i = 0; i < widths.length; i++) {
            totalWidth += widths[i];
        }
        averageFontWidth = (int) (totalWidth / (double) widths.length);

        LOGGER.log(Level.INFO, "BlockRunner:FontWidth: {0}", averageFontWidth);

    }

    public static mxCell block2Cell(long id, Object[] cells) {
        mxCell cell = null;
        Long longVal;
        for (Object cl : cells) {
            mxCell c = (mxCell) cl;
            longVal = Long.parseLong(c.getId());
            if (longVal.equals(id)) {
                cell = c;
                break;
            }
        }

        return cell;
    }

    private static int calcBaseY(int fontMetricHeight, double centerY, int listSize) {
        return (int) (centerY - (float) fontMetricHeight * (float) listSize / 2.0);
    }

    private static int calcBaseX(int fontMetricWidth, double centerX, int largestWidth) {
        return (int) (centerX - (float) fontMetricWidth * (float) largestWidth / 2.0);
    }

    private static int longestInList(Collection list) {
        int length = 0;
        for (Object o : list) {
            if (o.toString().length() > length) {
                length = o.toString().length();
            }
        }

        return length;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        LOGGER.log(Level.INFO, "BlockRunner:Animation: {0}", BaseBlock.ANIMATION_QUEUE);

        dialog.setVisible(false);
        dialog.dispose();
        loopIndex = 0;
        for (List<AnimationContext> contextPath : BaseBlock.ANIMATION_QUEUE) {
            publish(contextPath);
            loopIndex++;
            setProgress(loopIndex);
        }

        return loopIndex;
    }

    @Override
    protected void process(List<List<AnimationContext>> chunks) {
        for (List<AnimationContext> item : chunks) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AnimationRunner.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (AnimationContext context : item) {
                BaseBlock block = (BaseBlock) context.get("block");
                mxCell currentCell = AnimationRunner.block2Cell(block.getId(), cells);
                Collection<Object> in = (Collection<Object>) context.get("in");
                Collection<Object> out = new ArrayList<>();
                if (context.get("out") instanceof Collection) {
                    out.addAll((Collection<Object>) context.get("out"));
                } else {
                    out.add(context.get("out"));
                }

                Collection<Object> listing;
                if (block.isInDisplayed()) {
                    listing = in;
                } else {
                    listing = out;
                }

                // If block is Decision, figure out if YES or NO; then only get YES/NO edge and not getOutgoingEdges
                Object[] currentEdges = graph.getOutgoingEdges(currentCell);
                if (block instanceof Decision) {
                    String branchName = (String) context.get("branch");
                    mxCell branchEdge;
                    List<Object> branchEdges = new ArrayList<>();
                    for (Object edge : currentEdges) {
                        branchEdge = (mxCell) edge;
                        if (branchEdge.getValue().equals(branchName)) {
                            branchEdges.add(edge);
                            break;
                        }
                    }
                    currentEdges = branchEdges.toArray();
                }
                List<mxPoint> points;

                BasicStroke vertexStroke = new BasicStroke(STROKE_WIDTH_NODE);
                g.setStroke(vertexStroke);

                mxGeometry vertexGeom = graph.getModel().getGeometry(currentCell);

                g.setColor(Color.yellow);
                g.drawRect((int) vertexGeom.getX(), (int) vertexGeom.getY(), (int) vertexGeom.getWidth(), (int) vertexGeom.getHeight());
                g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
                int dy = 0;
                int longest = AnimationRunner.longestInList(listing);
                for (Object obj : listing) {
                    dy += g.getFontMetrics().getHeight();
                    if (out.contains(obj)) {
                        g.setColor(Color.red);
                    } else {
                        g.setColor(Color.black);
                    }
                    g.drawString(obj.toString(), AnimationRunner.calcBaseX(averageFontWidth, vertexGeom.getCenterX(), longest), AnimationRunner.calcBaseY(g.getFontMetrics().getHeight(), vertexGeom.getCenterY(), listing.size()) + dy);
                }
                try {
                    Thread.sleep(block.getDelay());
                } catch (InterruptedException ex) {
                    Logger.getLogger(AnimationRunner.class.getName()).log(Level.SEVERE, null, ex);
                }

                BasicStroke edgeStroke = new BasicStroke(STROKE_WIDTH_EDGE);
                g.setStroke(edgeStroke);

                mxCellState edgeState = null;
                for (Object e : currentEdges) {
                    edgeState = graph.getView().getState(e);
//                    states.add(edgeState);
                    points = edgeState.getAbsolutePoints();
                    mxPoint previousPoint = null;
                    g.setColor(Color.red);
                    for (mxPoint p : points) {//Translate using button width and height
                        if (previousPoint != null) {
//                    LOGGER.log(Level.INFO, "AnimationRunner:DrawLine: {0}=>{1}", new Object[]{previousPoint, p});
                            g.drawLine((int) (previousPoint.getX()), (int) (previousPoint.getY()), (int) (p.getX()), (int) (p.getY()));
                        }
                        previousPoint = p;
                        try {
                            Thread.sleep(block.getDelay() * 500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AnimationRunner.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                    try {
                        Thread.sleep(block.getDelay() * 1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AnimationRunner.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AnimationRunner.class.getName()).log(Level.SEVERE, null, ex);
            }
            glass.paintImmediately(glass.getBounds());
        }
    }

    @Override
    protected void done() {
        glass.setVisible(false);
        glass.setNeedToRedispatch(true);
        executeButton.setEnabled(true);
        saveButton.setEnabled(true);
    }

}
