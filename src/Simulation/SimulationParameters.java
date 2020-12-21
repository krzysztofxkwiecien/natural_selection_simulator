package Simulation;

public class SimulationParameters {

    public final int windowHeight;
    public final int millisecondsPerEra;
    public final int width;
    public final int height;
    public final int startAnimals;
    public final int startEnergy;
    public final int moveEnergy;
    public final int plantEnergy;
    public final int jungleRatio;
    public final int numberOfWindows;

    public SimulationParameters(int windowHeight, int millisecondsPerEra, int width, int height, int startAnimals, int startEnergy, int moveEnergy, int plantEnergy, int jungleRatio, int numberOfWindows) {
        this.windowHeight = windowHeight;
        this.millisecondsPerEra = millisecondsPerEra;
        this.width = width;
        this.height = height;
        this.startAnimals = startAnimals;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        this.numberOfWindows = numberOfWindows;
    }
}
