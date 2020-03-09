package ionshield.curves.math;

import java.util.List;

public interface CurveMaker {
    List<LineDouble> makeCurve(List<TangentPoint> points);
}
