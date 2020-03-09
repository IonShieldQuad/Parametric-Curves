package ionshield.curves.math;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LagrangeCurveMaker implements CurveMaker {
    private static final int STEPS = 1000;
    
    @Override
    public List<LineDouble> makeCurve(List<TangentPoint> points) {
        List<LineDouble> lines = new ArrayList<>();
        PointDouble prev = null;
        
        if (!points.isEmpty()) {
            double minX = points.stream().map(c -> c.point).min(Comparator.comparing(PointDouble::getX)).orElse(points.get(0).point).getX();
            double maxX = points.stream().map(c -> c.point).max(Comparator.comparing(PointDouble::getX)).orElse(points.get(0).point).getX();
            
            double dx = (maxX - minX) / STEPS;
    
            for (int i = 0; i <= STEPS; i++) {
                double x = minX + dx * i;
                
                double y = 0;
                for (int j = 0; j < points.size(); j++) {
                    double p = points.get(j).point.getY();
                    for (int k = 0; k < points.size(); k++) {
                        if (k != j) {
                            p *= ((x - points.get(k).point.getX()) / (points.get(j).point.getX() - points.get(k).point.getX()));
                        }
                    }
                    y += p;
                }
                
                PointDouble point = new PointDouble(x, y);
                if (prev != null) {
                    lines.add(new LineDouble(prev, point));
                }
                prev = point;
            }
        }
        
        return lines;
    }
}
