package Project;
import java.util.Objects;

public class Vector2d {
    public final int x;
    public final int y;

    public Vector2d(int x, int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return String.format("(%d,%d)", this.x, this.y);
    }

    public boolean precedes(Vector2d other){
        return (this.x < other.x && this.y < other.y);
    }

    public boolean follows(Vector2d other){
        return (this.x >= other.x && this.y >= other.y);
    }

    public Vector2d upperRight(Vector2d other){
        return new Vector2d(Math.max(other.x, this.x), Math.max(other.y, this.y));
    }

    public Vector2d lowerLeft(Vector2d other){
        return new Vector2d(Math.min(other.x, this.x), Math.min(other.y, this.y));
    }

    public Vector2d add(Vector2d other){
        return new Vector2d(this.x + other.x, this.y + other.y);
    }

    public Vector2d subtract(Vector2d other){
        return new Vector2d(this.x - other.x, this.y - other.y);
    }

    public boolean equals(Object other){
        if(other instanceof Vector2d){
            Vector2d that = (Vector2d)other;
            return (this.x == that.x && this.y == that.y);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    public Vector2d opposite(){
        return new Vector2d(-this.x, -this.y);
    }
}
