package ionshield.curves.core;


import com.bulenkov.darcula.DarculaLaf;
import ionshield.curves.graph.CurveDisplay;
import ionshield.curves.math.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicLookAndFeel;

public class MainWindow {
    private JPanel rootPanel;
    private JTextArea log;

    private JButton selectButton;
    private CurveDisplay graph;
    private JComboBox methodBox;
    private JButton addButton;
    private JButton removeButton;
    private JButton clearButton;
    private JTextField gridTextField;
    private JCheckBox snapToGridCheckBox;
    
    
    private static String TITLE = "Parametric-Curves";
    
    private MainWindow() {
        initComponents();
    }
    
    private void initComponents() {
        
        selectButton.addActionListener(e -> updateGraph());
        
        addButton.addActionListener(ev -> graph.setState(CurveDisplay.State.ADDITION));
        removeButton.addActionListener(ev -> graph.deleteSelected());
        clearButton.addActionListener(ev -> graph.clear());
    }
    
    
    private void updateGraph() {
    
        try {
            
            int gridRes = Integer.parseInt(gridTextField.getText());
            
            graph.setResolution(gridRes);
            
            int index = methodBox.getSelectedIndex();
            CurveMaker curveMaker = null;
            boolean showTangents = false;
            switch (index) {
                case 0:
                    showTangents = false;
                    break;
                case 1:
                    curveMaker = new LinearCurveMaker();
                    showTangents = false;
                    break;
                case 2:
                    curveMaker = new LagrangeCurveMaker();
                    showTangents = false;
                    break;
                case 3:
                    curveMaker = new HermiteCurveMaker();
                    showTangents = true;
                    break;
                case 4:
                    curveMaker = new BezierCurveMaker();
                    showTangents = false;
                    break;
                case 5:
                    curveMaker = new QuadraticSplineCurveMaker();
                    showTangents = false;
                    break;
                case 6:
                    curveMaker = new CubicSplineCurveMaker();
                    showTangents = false;
                    break;
                case 7:
                    curveMaker = new CRCurveMaker();
                    showTangents = false;
                    break;
                default:
                    throw new IllegalArgumentException("No such method");
            }
            graph.setCurveMaker(curveMaker);
            graph.setDisplayTangents(showTangents);
            graph.setSnapToGrid(snapToGridCheckBox.isSelected());
        } catch (IllegalArgumentException e) {
            log.append(e.getMessage() + "\n");
        }
        
        graph.repaint();
    }
    
    
    public static void main(String[] args) {
        BasicLookAndFeel darcula = new DarculaLaf();
        try {
            UIManager.setLookAndFeel(darcula);
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame(TITLE);
        MainWindow gui = new MainWindow();
        frame.setContentPane(gui.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
