package ionshield.curves.math;

import java.util.ArrayList;
import java.util.List;

public class LinearCurveMaker implements CurveMaker {
    @Override
    public List<LineDouble> makeCurve(List<TangentPoint> points) {
        List<LineDouble> lines = new ArrayList<>();
        
        for (int i = 0; i < points.size() - 1; i++) {
            lines.add(new LineDouble(points.get(i).point, points.get(i + 1).point));
        }
        
        return lines;
    }
}
