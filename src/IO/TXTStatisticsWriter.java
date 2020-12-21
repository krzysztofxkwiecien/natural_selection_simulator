package IO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class TXTStatisticsWriter{

    public void write(int fromEra, int toEra, long animals, long grasses, ArrayList<String> genomes, long energies, long lifetimes, long deadAnimals, long children) {

        int eras = toEra - fromEra;
        String dominantGenotype = getDominatingGenome(genomes);

        try {
            File myObj = new File("statistics.txt");
            myObj.createNewFile();
        }catch(IOException e){
            System.out.println("Error creating the file.");
        }

        try {
            FileWriter myWriter = new FileWriter("statistics.txt");
            myWriter.write("Statistics from era " + fromEra + " to " + toEra + "\n");
            myWriter.write("Average animals in all eras: " + (int)(animals / eras) + "\n");
            myWriter.write("Average grasses in all eras: " + (int)(grasses / eras) + "\n");
            myWriter.write("Dominant genotype in all eras: " + dominantGenotype + "\n");
            myWriter.write("Average energy for living animals in all eras: " + (int)(energies / eras) + "\n");
            myWriter.write("Average lifespan for dead animals in all eras: " + (int)(lifetimes / deadAnimals) + "\n");
            myWriter.write("Average children for living animals in all eras: " + (int)(children / eras) + "\n");
            myWriter.close();
        }catch(IOException e){
            System.out.println("Error writing to file");
        }
    }

    private static String getDominatingGenome(ArrayList<String> genomes){

        if(genomes.size() == 0)
            return "";

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
}

