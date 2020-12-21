package Simulation;


import IO.TXTStatisticsWriter;
import javafx.event.ActionEvent;
import Project.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Simulation {

    private int currentEra = 0;
    private boolean simulationPaused = false;

    private final WorldMap map;

    private final Vector2d jungleTopLeft;
    private final Vector2d jungleBottomRight;

    private final int mapWidth;
    private final int mapHeight;
    private final float plantEnergy;
    private final float startEnergy;
    private final int moveEnergy;
    private final int jungleRatio;

    private final ArrayList<Animal> animals = new ArrayList<>();
    private final ArrayList<Grass> grasses = new ArrayList<>();
    private Animal selectedAnimal = null;
    private final AnimalReproducer animalReproducer;
    private final ArrayList<String> genomesAllEras = new ArrayList<>();

    private float totalEnergy = 0;
    private int totalErasLived = 0;
    private int deadAnimalsCount = 0;

    private long totalAnimalsAllEras = 0;
    private long totalGrassesAllEras = 0;
    private long totalEnergyAllEras = 0;
    private long totalErasLivedAllEras = 0;
    private long deadAnimalsAllEras = 0;
    private long totalChildrenAllEras = 0;

    private final Visualiser visualiser;
    private final TXTStatisticsWriter TXTWriter;

    public Simulation(SimulationParameters simulationParameters){
        mapWidth = simulationParameters.width;
        mapHeight = simulationParameters.height;
        plantEnergy = simulationParameters.plantEnergy;
        startEnergy = simulationParameters.startEnergy;
        moveEnergy = simulationParameters.moveEnergy;
        jungleRatio = simulationParameters.jungleRatio;

        map = new WorldMap(mapWidth, mapHeight);

        jungleTopLeft = new Vector2d((int)(mapWidth / 2 - mapWidth / (jungleRatio * 2)), (int)(mapHeight / 2 - mapHeight / (jungleRatio * 2)));
        jungleBottomRight = jungleTopLeft.add(new Vector2d((int)(mapWidth/jungleRatio), (int)(mapHeight/jungleRatio)));

        visualiser = new Visualiser(this, map, jungleTopLeft, jungleBottomRight, simulationParameters);

        animalReproducer = new AnimalReproducer(map);
        TXTWriter = new TXTStatisticsWriter();

        generateAnimals(simulationParameters.startAnimals);
    }

    void run(ActionEvent actionEvent) {
        if(!simulationPaused){
            currentEra++;
            update();
            visualiser.display();
        }
    }

    private void update(){

        deenergizeAnimals();
        moveAnimals();
        consumeGrass();
        multiplyAnimals();
        generateGrasses();
    }

    private void deenergizeAnimals(){
        totalEnergy = 0;
        for(Animal animal : new ArrayList<>(animals)){
            animal.incrementAge();
            animal.decreaseEnergy(moveEnergy);
            if(animal.getEnergy() <= 0){
                animals.remove(animal);
                deadAnimalsCount++;
                totalErasLived += animal.getAge();
                continue;
            }
            totalEnergy += animal.getEnergy();
        }

    }

    private void moveAnimals(){
        for(Animal animal : animals){
            animal.rotateMove();
        }
    }

    private void consumeGrass(){
        ArrayList<PriorityQueue<Animal>> grassEaters = map.getGrassEaters();

        if(grassEaters.isEmpty())
            return;

        for(PriorityQueue<Animal> contestants : grassEaters) {

            Vector2d position = contestants.peek().getPosition();
            Grass grass = new Grass(position, plantEnergy);
            grasses.remove(grass);

            ArrayList<Animal> eatingAnimals = new ArrayList<>();
            float highestEnergy = contestants.peek().getEnergy();
            for(Animal animal : contestants){
                if(animal.getEnergy() < highestEnergy)
                    break;
                eatingAnimals.add(animal);
            }

            for(Animal animal : eatingAnimals){
                animal.addEnergy(plantEnergy / eatingAnimals.size());
            }

            map.positionChanged(grass, position, null);
        }

    }

    private void multiplyAnimals(){
        ArrayList<PriorityQueue<Animal>> potentialPartners = map.getPotentialPartners();
        float requiredEnergy = startEnergy / 2;

        for(PriorityQueue<Animal> potentialPair : potentialPartners){

            Animal firstParent = potentialPair.poll();
            Animal secondParent = potentialPair.poll();

            potentialPair.add(firstParent);
            potentialPair.add(secondParent);

            if(secondParent.getEnergy() < requiredEnergy){
                continue;
            }


            Vector2d childPosition = map.randomNextTile(firstParent.getPosition());

            Animal child = animalReproducer.reproduce(firstParent, secondParent, childPosition);

            child.addObserver(map);
            animals.add(child);
            map.place(child);
        }
    }

    private void generateGrasses(){

        int maxAttempts = (int) ((mapWidth * mapHeight)/(jungleRatio * jungleRatio) * 0.7);
        Vector2d position = map.randomUnoccupiedPosition(jungleTopLeft, jungleBottomRight, maxAttempts, false);

        if(position != null){
            Grass grass = new Grass(position, plantEnergy);
            grasses.add(grass);
            map.place(grass);
        }

        maxAttempts = (int) (((mapWidth * mapHeight) - (mapWidth * mapHeight)/(jungleRatio * jungleRatio)) * 0.7);
        position = map.randomUnoccupiedPosition(jungleTopLeft, jungleBottomRight, maxAttempts, true);

        if(position != null){
            Grass grass = new Grass(position, plantEnergy);
            grasses.add(grass);
            map.place(grass);
        }
    }

    private void updateTotalStatistics(){
        totalAnimalsAllEras += animals.size();
        totalGrassesAllEras += grasses.size();
        deadAnimalsAllEras += deadAnimalsCount;
        totalErasLivedAllEras += totalErasLived;
        totalEnergyAllEras += totalEnergy / animals.size();
        totalChildrenAllEras += getTotalChildren() / animals.size();

        for(Animal animal : animals)
            genomesAllEras.add(animal.getGenotype());
    }

    private void generateAnimals(int count){

        GenotypeProducer genotypeProducer = new GenotypeProducer();
        for(int i = 0; i< count; i++){

            Vector2d position = map.randomUnoccupiedPosition(new Vector2d(0, 0), new Vector2d(mapWidth, mapHeight), 10, false);
            Animal animal = new Animal(map, position, startEnergy, genotypeProducer.random());
            animal.addObserver(map);
            animals.add(animal);
            map.place(animal);

        }
    }

    public int getCurrentEra(){
        return currentEra;
    }

    public ArrayList<Animal> getAnimals(){
        return animals;
    }

    public ArrayList<Grass> getGrasses(){
        return grasses;
    }

    public String getDominatingGenome(){

        if(animals.size() == 0)
            return "";

        ArrayList<String> genomes = new ArrayList<>();
        for(Animal animal : animals){
            genomes.add(animal.getGenotype());
        }

        Collections.sort(genomes);

        int currentCount = 1;
        int dominatingCount = 1;
        String dominatingGenome = genomes.get(0);

        for(int i = 1; i < genomes.size(); i++){
            if(genomes.get(i).equals(genomes.get(i-1))){
                currentCount++;
            }
            else {
                if(currentCount > dominatingCount){
                    dominatingGenome = genomes.get(i-1);
                    dominatingCount = currentCount;
                }
                currentCount = 1;
            }
        }

        return dominatingGenome;
    }

    public Animal getSelectedAnimal(){
        return selectedAnimal;
    }

    public void setSelectedAnimal(Animal selectedAnimal){
        this.selectedAnimal = selectedAnimal;
        if(selectedAnimal != null)
            selectedAnimal.clearChildren();
    }

    public float getTotalEnergy(){
        return totalEnergy;
    }

    public float getTotalErasLived(){
        return totalErasLived;
    }

    public int getDeadAnimalsCount(){
        return deadAnimalsCount;
    }

    public int getTotalChildren(){
        int totalChildren = 0;
        for(Animal animal : animals)
            totalChildren += animal.getChildrenCount();
        return totalChildren;
    }

    public boolean isPaused(){
        return simulationPaused;
    }

    public void pause(boolean toPause){
        simulationPaused = toPause;
    }

    public void jumpEras(int eras, boolean saveStatistics){

        int targetEra = currentEra + eras;

        if(saveStatistics)
            clearTotalStatistic();
        if(selectedAnimal != null)
            selectedAnimal.clearChildren();

        while(currentEra < targetEra){
            currentEra++;

            if(selectedAnimal != null && !animals.contains(selectedAnimal)) {
                visualiser.selectedAnimalDeadAt(currentEra);
            }

            update();
            if(saveStatistics)
                updateTotalStatistics();
        }

        visualiser.display();
        visualiser.displaySelectedAnimal();

        if(saveStatistics)
            TXTWriter.write(
                    currentEra - eras, currentEra, totalAnimalsAllEras,
                    totalGrassesAllEras, genomesAllEras, totalEnergyAllEras,
                    totalErasLivedAllEras, deadAnimalsAllEras, totalChildrenAllEras);
    }

    private void clearTotalStatistic(){

        for(Animal animal : animals)
            animal.clearChildren();

        genomesAllEras.clear();

        totalAnimalsAllEras = 0;
        totalGrassesAllEras = 0;
        totalEnergyAllEras = 0;
        totalErasLivedAllEras = 0;
        deadAnimalsAllEras = 0;
        totalChildrenAllEras = 0;
    }
}
