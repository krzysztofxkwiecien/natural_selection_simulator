package Project;

import java.util.Objects;

public class Grass implements IMapElement {

    private Vector2d position;

    public final float nutrition;

    public Grass(Vector2d position, float nutrition){
        this.position = position;
        this.nutrition = nutrition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grass)) return false;
        Grass grass = (Grass) o;
        return Objects.equals(position, grass.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position);
    }

    public Vector2d getPosition() {
        return position;
    }

    public String toString() {
        return "*";
    }

}
