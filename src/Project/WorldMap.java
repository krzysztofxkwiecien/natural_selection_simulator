package Project;

import java.util.*;

public class WorldMap implements IPositionChangeObserver {

    private final int mapWidth;
    private final int mapHeight;

    private final HashMap<Vector2d, PriorityQueue<Animal>> animals = new HashMap<>();
    private final HashMap<Vector2d, Grass> grasses = new HashMap<>();

    private final Random random = new Random();

    public WorldMap(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    public Vector2d randomUnoccupiedPosition(Vector2d topLeft, Vector2d bottomRight, int maxAttempts, boolean flipArea){

        // Try 'maxAttempts' times to find a random unoccupied position, if that fails
        // try to find any free position, if there are none - return null

        Vector2d position;
        int attempts = 0;

        if(!flipArea){
            do{
                position = new Vector2d(random.nextInt(bottomRight.x - topLeft.x) + topLeft.x, random.nextInt(bottomRight.y -topLeft.y) + topLeft.y);
                attempts++;
            }while((animals.containsKey(position) || grasses.containsKey(position)) && attempts < maxAttempts);

            if(attempts < maxAttempts)
                return position;

            attempts = 0;
            // If we couldn't find a random free position, we'll scan the whole area for a free position
            maxAttempts = (bottomRight.x - topLeft.x) * (bottomRight.y - topLeft.y);

            do{
                if(position.x == bottomRight.x-1){
                    if(position.y == bottomRight.y-1)
                        position = new Vector2d(topLeft.x, topLeft.y);
                    else
                        position = new Vector2d(topLeft.x, position.y+1);
                }
                else{
                    position = new Vector2d(position.x + 1, position.y);
                }

                attempts++;
            }while((animals.containsKey(position) || grasses.containsKey(position)) && attempts < maxAttempts);

        }
        else{
            do{
                position = new Vector2d(random.nextInt(mapWidth), random.nextInt(mapHeight));
                attempts++;
            }while((animals.containsKey(position) || grasses.containsKey(position) || (position.follows(topLeft) && position.precedes(bottomRight))) && attempts < maxAttempts);

            if(attempts < maxAttempts)
                return position;

            attempts = 0;
            // If we couldn't find a random free position, we'll scan the whole map for a free position,
            // but since the area is flipped, we need to avoid it
            maxAttempts = (mapWidth * mapHeight) - (bottomRight.x - topLeft.x) * (bottomRight.y - topLeft.y);

            do{
                if(position.x == mapWidth-1){
                    if(position.y == mapHeight-1)
                        position = new Vector2d(0, 0);
                    else
                        position = new Vector2d(0, position.y+1);
                }
                else if(position.x >= topLeft.x-1 && position.x <= bottomRight.x && position.y >= topLeft.y && position.y <= bottomRight.y){
                    position = new Vector2d(bottomRight.x + 1, position.y);
                }
                else{
                    position = new Vector2d(position.x + 1, position.y);
                }

                attempts++;
            }while((animals.containsKey(position) || grasses.containsKey(position)) && attempts < maxAttempts);

        }

        if(attempts < maxAttempts)
            return position;

        // There is no empty position
        return null;
    }

    public Vector2d randomNextTile(Vector2d startPosition){
        Vector2d position;

        int attempts = 0;
        do{
            position = new Vector2d(startPosition.x + random.nextInt(3) - 1, startPosition.y + random.nextInt(3) - 1);
            position = toInBoundsPosition(position);
            attempts++;
        }while((animals.containsKey(position) || grasses.containsKey(position)) && attempts < 10);

        if(attempts == 10){
            position = new Vector2d(startPosition.x + random.nextInt(3) - 1, startPosition.y + random.nextInt(3) - 1);
            position = toInBoundsPosition(position);
        }

        return position;
    }

    public boolean isOccupied(Vector2d position){
        return animals.get(position) != null || grasses.get(position) != null;
    }

    public Animal animalAt(Vector2d position){
        if(animals.get(position) == null)
            return null;
        return animals.get(position).peek();
    }

    public ArrayList<PriorityQueue<Animal>> getPotentialPartners(){

        ArrayList<PriorityQueue<Animal>> potentialPartners = new ArrayList<>();

        for(PriorityQueue<Animal> animalsOnField : animals.values()){
            if(animalsOnField.size() >= 2)
                potentialPartners.add(animalsOnField);
        }

        return potentialPartners;
    }

    public ArrayList<PriorityQueue<Animal>> getGrassEaters(){

        ArrayList<PriorityQueue<Animal>> grassEaters = new ArrayList<>();

        for(Vector2d position : animals.keySet()){
            if(grasses.containsKey(position)){
                grassEaters.add(animals.get(position));
            }
        }

        return grassEaters;
    }

    public void place(IMapElement mapElement){

        Vector2d position = mapElement.getPosition();

        if(mapElement.getClass() == Animal.class){
            Animal animal = (Animal) mapElement;
            if(animals.get(position) == null){
                animals.put(position, new PriorityQueue<Animal>(Animal.SortByEnergy));
            }
            animals.get(position).add(animal);
        }
        else{
            grasses.put(position, (Grass) mapElement);
        }
    }

    public void positionChanged(IMapElement mapElement, Vector2d oldPosition, Vector2d newPosition){

        if(mapElement.getClass() == Animal.class) {
            Animal animal = (Animal) mapElement;
            if(animals.get(oldPosition).size() == 1){
                animals.remove(oldPosition);
            }
            else{
                animals.get(oldPosition).remove(animal);
            }
        }
        else{
            grasses.remove(oldPosition);
        }

        if(newPosition != null)
            place(mapElement);
    }

    public Vector2d toInBoundsPosition(Vector2d position){
        if(position.x < 0)
            position = new Vector2d(mapWidth-1, position.y);
        if(position.x >= mapWidth)
            position = new Vector2d(0, position.y);
        if(position.y < 0)
            position = new Vector2d(position.x, mapHeight-1);
        if(position.y >= mapHeight)
            position = new Vector2d(position.x, 0);

        return position;
    }
}
