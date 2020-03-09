package ionshield.curves.math;

import java.util.ArrayList;
import java.util.List;

public class BezierCurveMaker implements CurveMaker {
    private static final int STEPS = 100;
    public static final Matrix M_B = Matrix.makeEmptyMatrix(4).fill(new double[][]{new double[]{-1, 3, -3, 1}, new double[]{3, -6, 3, 0}, new double[]{-3, 3, 0, 0}, new double[]{1, 0, 0, 0}});
    
    @Override
    public List<LineDouble> makeCurve(List<TangentPoint> points) {
        List<LineDouble> lines = new ArrayList<>();
        PointDouble prev = null;
    
        if (points.size() > 3) {
            for (int j = 0; j < points.size() - 3; j += 3) {
                TangentPoint p1 = points.get(j);
                TangentPoint p2 = points.get(j + 1);
                TangentPoint p3 = points.get(j + 2);
                TangentPoint p4 = points.get(j + 3);
    
                for (int i = 0; i <= STEPS; i++) {
                    double t = (double) i / STEPS;
                    Matrix tm = Matrix.makeEmptyMatrix(4, 1).fill(new double[][]{new double[]{t * t * t}, new double[]{t * t}, new double[]{t}, new double[]{1}});
                    Matrix gx = Matrix.makeEmptyMatrix(1, 4).fill(new double[][]{new double[]{p1.point.getX(), p2.point.getX(), p3.point.getX(), p4.point.getX()}});
        
                    Matrix gy = Matrix.makeEmptyMatrix(1, 4).fill(new double[][]{new double[]{p1.point.getY(), p2.point.getY(), p3.point.getY(), p4.point.getY()}});
                    
                    Matrix xm = tm.multiply(M_B).multiply(gx);
                    
                    Matrix ym = tm.multiply(M_B).multiply(gy);
        
                    PointDouble p = new PointDouble(xm.get(0, 0), ym.get(0, 0));
                    if (prev != null) {
                        lines.add(new LineDouble(prev, p));
                    }
                    prev = p;
                }
            }
        }
    
        return lines;
    }
}
