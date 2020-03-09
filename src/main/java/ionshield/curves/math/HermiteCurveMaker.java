package ionshield.curves.math;

import java.util.ArrayList;
import java.util.List;

public class HermiteCurveMaker implements CurveMaker {
    private static final int STEPS = 100;
    private static final double TANGENT_SCALE = 5.0;
    public static final Matrix M_H = Matrix.makeEmptyMatrix(4).fill(new double[][]{new double[]{2, -3, 0, 1}, new double[]{-2, 3, 0, 0}, new double[]{1, -2, 1, 0}, new double[]{1, -1, 0, 0}});
    
    @Override
    public List<LineDouble> makeCurve(List<TangentPoint> points) {
        List<LineDouble> lines = new ArrayList<>();
        PointDouble prev = null;
        
        if (points.size() > 1) {
            for (int j = 0; j < points.size() - 1; j++) {
                TangentPoint p1 = points.get(j);
                TangentPoint p4 = points.get(j + 1);
    
                for (int i = 0; i <= STEPS; i++) {
                    double t = (double) i / STEPS;
                    Matrix tm = Matrix.makeEmptyMatrix(4, 1).fill(new double[][]{new double[]{t * t * t}, new double[]{t * t}, new double[]{t}, new double[]{1}});
                    Matrix gx = Matrix.makeEmptyMatrix(1, 4).fill(new double[][]{new double[]{p1.point.getX(), p4.point.getX(), p1.tangent.scale(TANGENT_SCALE).getX(), p4.tangent.scale(TANGENT_SCALE).getX()}});
        
                    Matrix gy = Matrix.makeEmptyMatrix(1, 4).fill(new double[][]{new double[]{p1.point.getY(), p4.point.getY(), p1.tangent.scale(TANGENT_SCALE).getY(), p4.tangent.scale(TANGENT_SCALE).getY()}});
        
                    //Matrix cx = M_H.multiply(gx);
                    Matrix xm = tm.multiply(M_H).multiply(gx);
        
                    //Matrix cy = M_H.multiply(gy);
                    Matrix ym = tm.multiply(M_H).multiply(gy);
        
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
