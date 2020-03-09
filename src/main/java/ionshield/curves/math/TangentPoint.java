package ionshield.curves.math;

public class TangentPoint {
    public PointDouble point;
    public PointDouble tangent;
    
    public TangentPoint(PointDouble point) {
        this.point = point;
        this.tangent = new PointDouble(0, 0);
    }
    
    public TangentPoint(PointDouble point, PointDouble tangent) {
        this.point = point;
        this.tangent = tangent;
    }
    
    public PointDouble getTangentLocation(double scale) {
        return point.add(tangent.scale(scale));
    }
    
    public void setTangentFromLocation(PointDouble location, double scale) {
        tangent = location.add(point.scale(-1)).scale(scale);
    }
}
