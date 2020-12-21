package Project;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AnimalReproducer {

    private final WorldMap map;

    public AnimalReproducer(WorldMap map){
        this.map = map;
    }

    public Animal reproduce(Animal firstAnimal, Animal secondAnimal, Vector2d childPosition){

        Random random = new Random();
        int n = firstAnimal.getGenotype().length();

        // Get 2 random unique indexes within genome size and order them
        int firstCut, secondCut;

        firstCut = random.nextInt(n);
        do{
            secondCut = random.nextInt(n);
        }while(firstCut == secondCut);

        if(secondCut < firstCut){
            int tmp = secondCut;
            secondCut = firstCut;
            firstCut = tmp;
        }

        int[] genomeParts = {0, firstCut, secondCut};

        // Choose which animal will only pass one part of their genome
        // and then choose which of the 3 parts it will be

        Animal weakerAnimal;
        Animal strongerAnimal;
        if(random.nextInt(2) == 0){
            weakerAnimal = firstAnimal;
            strongerAnimal = secondAnimal;
        }
        else{
            weakerAnimal = secondAnimal;
            strongerAnimal = firstAnimal;
        }

        String weakerGenotype = weakerAnimal.getGenotype();
        String strongerGenotype = strongerAnimal.getGenotype();

        int weakerAnimalPart = random.nextInt(3);

        StringBuilder rawGenotype = new StringBuilder();

        for(int part = 0; part < 3; part++){
            if(part == weakerAnimalPart){
                if(part == 2)
                    rawGenotype.append(weakerGenotype.substring(genomeParts[part]));
                else
                    rawGenotype.append(weakerGenotype.substring(genomeParts[part], genomeParts[part + 1]));
            }
            else{
                if(part == 2)
                    rawGenotype.append(strongerGenotype.substring(genomeParts[part]));
                else
                    rawGenotype.append(strongerGenotype.substring(genomeParts[part], genomeParts[part + 1]));
            }
        }

        // Make sure all 8 genes are present in the genotype and order them
        String newGenotype = new GenotypeProducer().fromString(rawGenotype.toString());

        // Manage energy and create the child
        float childEnergy = 0;
        childEnergy += firstAnimal.getEnergy() / 4f;
        childEnergy += secondAnimal.getEnergy() / 4f;

        firstAnimal.decreaseEnergy(firstAnimal.getEnergy() / 4f);
        secondAnimal.decreaseEnergy(secondAnimal.getEnergy() / 4f);

        Animal child = new Animal(map, childPosition, childEnergy, newGenotype);

        firstAnimal.addChild(child);
        secondAnimal.addChild(child);

        return child;
    }
}
