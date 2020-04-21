package ESSExercise;

public class WallPoint {
    private int id;
    private int mapId;
    private double x;
    private double y;

    public WallPoint() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "WallPoint{" +
                "id=" + id +
                ", mapId=" + mapId +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
