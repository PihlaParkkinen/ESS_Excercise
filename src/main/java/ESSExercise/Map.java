package ESSExercise;

public class Map {
    private int id;
    private String name;
    private double pixelsPerMeter;

    public Map(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPixelsPerMeter() {
        return pixelsPerMeter;
    }

    public void setPixelsPerMeter(double pixelsPerMeter) {
        this.pixelsPerMeter = pixelsPerMeter;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
