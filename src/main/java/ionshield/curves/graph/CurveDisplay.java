package ionshield.curves.graph;

import ionshield.curves.math.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class CurveDisplay extends JPanel {
    private static final int MARGIN_X = 50;
    private static final int MARGIN_Y = 50;
    private static final double EXTRA_AMOUNT = 0.0;
    private static final Color POINT_COLOR = new Color(0xffffff);
    private static final Color POINT_TANGENT_COLOR = new Color(0x22bbff);
    private static final Color POINT_SELECTED_COLOR = new Color(0xff0000);
    private static final Color LINE_COLOR = new Color(0x66ff22);
    private static final Color TANGENT_LINE_COLOR = new Color(0x6622ff);
    private static final int POINT_SIZE = 8;
    
    private double lowerX = 0;
    private double upperX = 100;
    private double lowerY = 0;
    private double upperY = 100;
    private double lowerZ = Double.NEGATIVE_INFINITY;
    private double upperZ = Double.POSITIVE_INFINITY;
    private int resolution = 10;
    
    private double selectRadius = 100;
    private boolean drawGrid = true;
    
    private CurveMaker curveMaker = null;
    
    private List<TangentPoint> points = new ArrayList<>();
    private List<LineDouble> lines = new ArrayList<>();
    
    private State state = State.DEFAULT;
    private TangentPoint selectedPoint = null;
    private boolean selectedTangent = false;
    
    private boolean displayTangents = false;
    private boolean snapToGrid = false;
    
    public CurveMaker getCurveMaker() {
        return curveMaker;
    }
    
    public void setCurveMaker(CurveMaker curveMaker) {
        this.curveMaker = curveMaker;
    }
    
    public enum State {
        DEFAULT,
        SELECTION,
        ADDITION
    }
    
    public CurveDisplay() {
        super();
        CurveDisplay that = this;
        
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PointDouble click = graphToValue(new PointDouble(e.getX(), e.getY()));
                PointDouble snapClick = graphToValue(snapToGrid(new PointDouble(e.getX(), e.getY())));
                switch (state) {
                    case DEFAULT:
                        Pair<TangentPoint, Boolean> found = selectClosestPoint(click);
                        if (found.a != null) {
                            state = State.SELECTION;
                            selectedPoint = found.a;
                            selectedTangent = found.b;
                            break;
                        }
                    case ADDITION:
                        points.add(new TangentPoint(snapToGrid ? snapClick : click));
                        state = State.DEFAULT;
                        break;
                    case SELECTION:
                        if (selectedTangent && displayTangents) {
                            selectedPoint.setTangentFromLocation(snapToGrid ? snapClick : click, 1);
                        }
                        else {
                            selectedPoint.point.setX((snapToGrid ? snapClick : click).getX());
                            selectedPoint.point.setY((snapToGrid ? snapClick : click).getY());
                        }
                        state = State.DEFAULT;
                }
                
                that.repaint();
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
        
            }
    
            @Override
            public void mouseReleased(MouseEvent e) {
        
            }
    
            @Override
            public void mouseEntered(MouseEvent e) {
        
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
        
            }
        });
    }
    
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        lines.clear();
        
        if (drawGrid) {
            drawGrid(g);
        }
        
        if (curveMaker != null) {
            lines.addAll(curveMaker.makeCurve(points));
        }
        if (lines != null) {
            drawLines(g);
        }
        if (points != null) {
            drawPoints(g);
        }
    }
    
    
    private void drawGrid(Graphics g) {
        g.setColor(getForeground());
        
        double dx = (getWidth() - MARGIN_X * 2) / (double)resolution;
        double dy = (getHeight() - MARGIN_Y * 2) / (double)resolution;
    
        for (int i = 0; i <= resolution; i++) {
            g.drawLine(MARGIN_X + (int)(dx * i), getHeight() - MARGIN_Y, MARGIN_X + (int)(dx * i), MARGIN_Y);
        }
    
        for (int i = 0; i <= resolution; i++) {
            g.drawLine(MARGIN_X, MARGIN_Y + (int)(dy * i), getWidth() - MARGIN_X, MARGIN_Y + (int)(dy * i));
        }
        
        g.drawLine(MARGIN_X, getHeight() - MARGIN_Y, getWidth() - MARGIN_X, getHeight() - MARGIN_Y);
        g.drawLine(MARGIN_X, MARGIN_Y + (int)(graphHeight() * (1 - EXTRA_AMOUNT)), getWidth() - MARGIN_X, MARGIN_Y + (int)(graphHeight() * (1 - EXTRA_AMOUNT)));
        g.drawLine(MARGIN_X, MARGIN_Y + (int)(graphHeight() * EXTRA_AMOUNT), getWidth() - MARGIN_X, MARGIN_Y + (int)(graphHeight() * EXTRA_AMOUNT));
        
        g.drawLine(MARGIN_X, getHeight() - MARGIN_Y, MARGIN_X, MARGIN_Y);
        g.drawLine(MARGIN_X + (int)(graphWidth() * EXTRA_AMOUNT), getHeight() - MARGIN_Y, MARGIN_X + (int)(graphWidth() * EXTRA_AMOUNT), MARGIN_Y);
        g.drawLine(MARGIN_X + (int)(graphWidth() * (1 - EXTRA_AMOUNT)), getHeight() - MARGIN_Y, MARGIN_X + (int)(graphWidth() * (1 - EXTRA_AMOUNT)), MARGIN_Y);
        
        /*g.drawString(Double.toString(lowerX()), MARGIN_X + (int)(graphWidth() * EXTRA_AMOUNT), getHeight() - MARGIN_Y / 2);
        g.drawString(Double.toString(upperX()), MARGIN_X + (int)(graphWidth() * (1 - EXTRA_AMOUNT)), getHeight() - MARGIN_Y / 2);
        g.drawString(Double.toString(lowerY()), MARGIN_X / 4, MARGIN_Y + (int)(graphHeight() * (1 - EXTRA_AMOUNT)));
        g.drawString(Double.toString(upperY()), MARGIN_X / 4, MARGIN_Y + (int)(graphHeight() * EXTRA_AMOUNT));*/
    }
    
    private void drawPoints(Graphics g) {
        g.setColor(POINT_COLOR);
        points.stream().map(p -> p.point).map(this::valueToGraph).forEach(p -> g.drawOval((int)Math.round(p.getX()) - POINT_SIZE / 2, (int)Math.round(p.getY()) - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE));
    
        if (displayTangents) {
            g.setColor(POINT_TANGENT_COLOR);
            points.stream().map(p -> p.getTangentLocation(1)).map(this::valueToGraph).forEach(p -> g.drawOval((int) Math.round(p.getX()) - POINT_SIZE / 2, (int) Math.round(p.getY()) - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE));
        }
        
        if (state == State.SELECTION && selectedPoint != null) {
            if (selectedTangent && displayTangents) {
                g.setColor(POINT_SELECTED_COLOR);
                PointDouble p = valueToGraph(selectedPoint.getTangentLocation(1));
                g.drawOval((int) Math.round(p.getX()) - POINT_SIZE / 2, (int) Math.round(p.getY()) - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
            }
            else {
                g.setColor(POINT_SELECTED_COLOR);
                PointDouble p = valueToGraph(selectedPoint.point);
                g.drawOval((int) Math.round(p.getX()) - POINT_SIZE / 2, (int) Math.round(p.getY()) - POINT_SIZE / 2, POINT_SIZE, POINT_SIZE);
            }
        }
    }
    
    private void drawLines(Graphics g) {
        g.setColor(LINE_COLOR);
        //lines.stream().map(l -> new LineDouble(valueToGraph(l.a), valueToGraph(l.b))).forEach(l -> g.drawLine((int)Math.round(l.a.getX()), (int)Math.round(l.a.getY()), (int)Math.round(l.b.getX()), (int)Math.round(l.b.getY())));
    
        lines.stream().map(l -> new LineDouble(valueToGraph(l.a), valueToGraph(l.b))).map(l -> new Line((int)Math.round(l.a.getX()), (int)Math.round(l.a.getY()), (int)Math.round(l.b.getX()), (int)Math.round(l.b.getY()))).forEach(l -> GraphUtils.drawLine(l, g, LINE_COLOR));
        
        if (displayTangents) {
            g.setColor(TANGENT_LINE_COLOR);
            //points.stream().map(p -> new LineDouble(valueToGraph(p.point), valueToGraph(p.getTangentLocation(1)))).forEach(l -> g.drawLine((int) Math.round(l.a.getX()), (int) Math.round(l.a.getY()), (int) Math.round(l.b.getX()), (int) Math.round(l.b.getY())));
            points.stream().map(p -> new LineDouble(valueToGraph(p.point), valueToGraph(p.getTangentLocation(1)))).map(l -> new Line((int)Math.round(l.a.getX()), (int)Math.round(l.a.getY()), (int)Math.round(l.b.getX()), (int)Math.round(l.b.getY()))).forEach(l -> GraphUtils.drawLine(l, g, TANGENT_LINE_COLOR));
        }
    }
    
    private Pair<TangentPoint, Boolean> selectClosestPoint(PointDouble location) {
        TangentPoint selected = null;
        boolean tangent = false;
        double min = Double.MAX_VALUE;
        for (TangentPoint point : points) {
            double dist = valueToGraph(location).add(valueToGraph(point.point).scale(-1)).lengthSquared();
            if (dist < min && dist < selectRadius * selectRadius) {
                min = dist;
                selected = point;
                tangent = false;
            }
        
            if (displayTangents) {
                dist = valueToGraph(location).add(valueToGraph(point.getTangentLocation(1)).scale(-1)).lengthSquared();
                if (dist <= min && dist < selectRadius * selectRadius) {
                    min = dist;
                    selected = point;
                    tangent = true;
                }
            }
        }
        return new Pair<>(selected, tangent);
    }
    
    private PointDouble snapToGrid(PointDouble point) {
        double x = point.getX();
        double y = point.getY();
        
        double cellX = graphWidth() / (double)resolution;
        double cellY = graphHeight() / (double)resolution;
        
        x = Math.round((x - MARGIN_X) / cellX) * cellX + MARGIN_X;
        y = Math.round((y - MARGIN_Y) / cellY) * cellY + MARGIN_Y;
        
        return new PointDouble(x, y);
    }
    
    public void deleteSelected() {
        if (state == State.SELECTION && selectedPoint != null) {
            points.remove(selectedPoint);
            state = State.DEFAULT;
        }
        repaint();
    }
    
    public void clear() {
        points.clear();
        state = State.DEFAULT;
        repaint();
    }
    
    private int graphWidth() {
        return getWidth() - 2 * MARGIN_X;
    }
    
    private int graphHeight() {
        return getHeight() - 2 * MARGIN_Y;
    }
    
    private double lowerX() {
        return lowerX;
    }
    
    private double upperX() {
        return upperX;
    }
    
    private double lowerY() {
        return lowerY;
    }
    
    private double upperY() {
        return upperY;
    }
    
    public void setLowerX(double lowerX) {
        this.lowerX = lowerX;
        
    }
    
    public void setUpperX(double upperX) {
        this.upperX = upperX;
        
    }
    
    public void setLowerY(double lowerY) {
        this.lowerY = lowerY;
        
    }
    
    public void setUpperY(double upperY) {
        this.upperY = upperY;
        
    }
    
    public State getState() {
        return state;
    }
    
    public void setState(State state) {
        this.state = state;
    }
    
    private PointDouble valueToGraph(PointDouble point) {
        double valX = (point.getX() - lowerX()) / (upperX() - lowerX());
        double valY = (point.getY() - lowerY()) / (upperY() - lowerY());
        return new PointDouble(MARGIN_X + (int)((graphWidth() * EXTRA_AMOUNT) * (1 - valX) + (graphWidth() * (1 - EXTRA_AMOUNT)) * valX), getHeight() - MARGIN_Y - (int)((graphHeight() * EXTRA_AMOUNT) * (1 - valY) + (graphHeight() * (1 - EXTRA_AMOUNT)) * valY));
    }
    
    private PointDouble graphToValue(PointDouble point) {
        double valX = (point.getX() - (MARGIN_X + (graphWidth() * EXTRA_AMOUNT))) / ((MARGIN_X + (graphWidth() * (1 - EXTRA_AMOUNT))) - (MARGIN_X + (graphWidth() * EXTRA_AMOUNT)));
        double valY = (point.getY() - (MARGIN_Y + (graphHeight() * (1 - EXTRA_AMOUNT)))) / ((MARGIN_Y + (graphHeight() * EXTRA_AMOUNT)) - (MARGIN_Y + (graphHeight() * (1 - EXTRA_AMOUNT))));
        return new PointDouble(lowerX() * (1 - valX) + upperX() * valX, lowerY() * (1 - valY) + upperY() * valY);
    }
    
    public List<TangentPoint> getPoints() {
        return points;
    }
    
    public void setPoints(List<TangentPoint> points) {
        this.points = points;
    }
    
    public List<LineDouble> getLines() {
        return lines;
    }
    
    public void setLines(List<LineDouble> lines) {
        this.lines = lines;
    }
    
    public static double interpolate(double a, double b, double alpha) {
        return b * alpha + a * (1 - alpha);
    }
    
    public static Color interpolate(Color c1, Color c2, double alpha) {
        double gamma = 2.2;
        int r = (int)Math.round(255 * Math.pow(Math.pow(c2.getRed() / 255.0, gamma) * alpha + Math.pow(c1.getRed() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        int g = (int)Math.round(255 * Math.pow(Math.pow(c2.getGreen() / 255.0, gamma) * alpha + Math.pow(c1.getGreen() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        int b = (int)Math.round(255 * Math.pow(Math.pow(c2.getBlue() / 255.0, gamma) * alpha + Math.pow(c1.getBlue() / 255.0, gamma) * (1 - alpha), 1 / gamma));
        
        return new Color(r, g, b);
    }
    
    public double lowerZ() {
        return lowerZ;
    }
    
    public void setLowerZ(double lowerZ) {
        this.lowerZ = lowerZ;
    }
    
    public double getUpperZ() {
        return upperZ;
    }
    
    public void setUpperZ(double upperZ) {
        this.upperZ = upperZ;
    }
    
    public double getResolution() {
        return resolution;
    }
    
    public void setResolution(int resolution) {
        this.resolution = resolution;
        
    }
    
    public boolean isDrawGrid() {
        return drawGrid;
    }
    
    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
    }
    
    public boolean isDisplayTangents() {
        return displayTangents;
    }
    
    public void setDisplayTangents(boolean displayTangents) {
        this.displayTangents = displayTangents;
    }
    
    public boolean isSnapToGrid() {
        return snapToGrid;
    }
    
    public void setSnapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }
}
