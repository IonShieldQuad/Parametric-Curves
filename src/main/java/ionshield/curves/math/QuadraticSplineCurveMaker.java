package ionshield.curves.math;

import java.util.ArrayList;
import java.util.List;

public class QuadraticSplineCurveMaker implements CurveMaker {
    private static final int STEPS = 100;
    
    @Override
    public List<LineDouble> makeCurve(List<TangentPoint> points) {
        List<LineDouble> lines = new ArrayList<>();
        PointDouble prev = null;
    
        if (points.size() > 2) {
            for (int j = 0; j < points.size() - 2; j += 1) {
                TangentPoint p1 = points.get(j);
                TangentPoint p2 = points.get(j + 1);
                TangentPoint p3 = points.get(j + 2);
                prev = null;
                for (int i = 0; i <= STEPS; i++) {
                    double t = (double) i / STEPS;
        
                    double x = (t * t * 0.5) * p1.point.getX() + (0.75 - Math.pow(t - 0.5, 2)) * p2.point.getX() + 0.5 * Math.pow(1 - t, 2) * p3.point.getX();
                    double y = (t * t * 0.5) * p1.point.getY() + (0.75 - Math.pow(t - 0.5, 2)) * p2.point.getY() + 0.5 * Math.pow(1 - t, 2) * p3.point.getY();
        
                    PointDouble p = new PointDouble(x, y);
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
