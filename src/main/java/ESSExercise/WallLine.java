package ESSExercise;

import java.util.ArrayList;

/**
 * Combines information from wallPoint and WallSegment
 */

public class WallLine {
    private int type;
    private ArrayList<Double> point1;
    private ArrayList<Double> point2;
    private int mapId;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    public WallLine(){

    }

    public WallLine(int type, ArrayList<Double> point1, ArrayList<Double> point2) {
        this.type = type;
        this.point1 = point1;
        this.point2 = point2;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Double> getPoint1() {
        return point1;
    }

    public void setPoint1(ArrayList<Double> point1) {
        this.point1 = point1;
    }

    public ArrayList<Double> getPoint2() {
        return point2;
    }

    public void setPoint2(ArrayList<Double> point2) {
        this.point2 = point2;
    }

    @Override
    public String toString() {
        return "WallLine{" +
                "type=" + type +
                ", point1=" + point1 +
                ", point2=" + point2 +
                ", mapId=" + mapId +
                '}';
    }
}
