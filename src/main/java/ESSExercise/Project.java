package ESSExercise;

import java.util.List;

public class Project {
    private List<Map> maps;
    private List<WallPoint> wallPoints;
    private List<WallSegment> wallSegments;


    public Project(){

    }

    public List<Map> getMaps() {
        return maps;
    }

    public void setMaps(List<Map> maps) {
        this.maps = maps;
    }

    public List<WallPoint> getWallPoints() {
        return wallPoints;
    }

    public void setWallPoints(List<WallPoint> wallPoints) {
        this.wallPoints = wallPoints;
    }

    public List<WallSegment> getWallSegments() {
        return wallSegments;
    }

    public void setWallSegments(List<WallSegment> wallSegments) {
        this.wallSegments = wallSegments;
    }

    @Override
    public String toString() {
        return "Project{" +
                "maps=" + maps +
                ", wallPoints=" + wallPoints +
                ", wallSegments=" + wallSegments +
                '}';
    }
}
