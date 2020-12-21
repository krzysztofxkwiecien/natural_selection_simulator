package Project;

import java.util.*;

public class Animal implements IMapElement {

    private Vector2d position;
    private MapDirection orientation;
    private final WorldMap map;

    private float energy;
    private int age = 0;
    private final String genotype;

    private ArrayList<Animal> children = new ArrayList<>();
    private ArrayList<IPositionChangeObserver> observers = new ArrayList<>();

    private Random random = new Random();

    public Animal(WorldMap map, Vector2d initialPosition, float startEnergy, String genotype){
        this.map = map;
        this.position = initialPosition;
        this.orientation = MapDirection.values()[random.nextInt(MapDirection.values().length)];
        this.energy = startEnergy;
        this.genotype = genotype;
    }

    public void addChild(Animal child){
        children.add(child);
    }

    public void clearChildren(){
        children.clear();
    }

    public int getChildrenCount(){
        return children.size();
    }

    public void fillSetWithDescendants(HashSet<Animal> descendants){

        for(Animal child : children)
            child.fillSetWithDescendants(descendants);
        descendants.add(this);

    }

    public int getDescendantsCount(){

        HashSet<Animal> descendants = new HashSet<>();
        fillSetWithDescendants(descendants);

        return descendants.size() - 1;

    }

    public String getGenotype(){
        return genotype;
    }

    public void addEnergy(float amount){
        energy += amount;
    }

    public void decreaseEnergy(float amount){
        energy -= amount;
        if(energy <= 0)
            die();
    }

    public float getEnergy(){
        return energy;
    }

    private void die(){
        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(this, position, null);
        }
    }

    public void incrementAge(){ age++; }

    public int getAge(){ return age; }

    public void rotateMove(){

        int dir = Character.getNumericValue(genotype.toCharArray()[random.nextInt(genotype.length())]);
        orientation = orientation.rotateBy(dir);

        Vector2d newPosition = position.add(orientation.toUnitVector());
        newPosition = map.toInBoundsPosition(newPosition);

        Vector2d oldPosition = position;
        position = newPosition;

        for(IPositionChangeObserver observer : observers){
            observer.positionChanged(this, oldPosition, newPosition);
        }
    }

    public void addObserver(IPositionChangeObserver observer){
        observers.add(observer);
    }

    public void removeObserver(IPositionChangeObserver observer){
        observers.remove(observer);
    }

    public Vector2d getPosition() {
        return position;
    }

    public static final Comparator<Animal> SortByEnergy = new Comparator<Animal>() {
        public int compare(Animal a, Animal b)
        {
            if(a.getEnergy() > b.getEnergy())
                return 1;
            else if(a.getEnergy() == b.getEnergy())
                return 0;
            return -1;
        }
    };
}
