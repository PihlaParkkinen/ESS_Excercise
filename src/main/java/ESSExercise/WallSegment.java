package ESSExercise;

public class WallSegment {
    private int id;
    private int type;
    private int p1;
    private int p2;

    public WallSegment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    @Override
    public String toString() {
        return "WallSegment{" +
                "id=" + id +
                ", type=" + type +
                ", p1=" + p1 +
                ", p2=" + p2 +
                '}';
    }
}