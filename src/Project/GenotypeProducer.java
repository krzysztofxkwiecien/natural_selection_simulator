package Project;

import java.util.Arrays;
import java.util.Random;

public class GenotypeProducer {

    private final int length = 32;
    private final Random random = new Random();

    public String random(){
        StringBuilder rawGenotype = new StringBuilder();
        for(int i = 0; i < length; i++){
            rawGenotype.append(random.nextInt(8));
        }

        return fixGenotype(rawGenotype.toString());
    }

    public String fromString(String rawGenotype){
        return fixGenotype(rawGenotype);
    }

    private String fixGenotype(String rawGenotype){

        char genotypeCharArr[] = rawGenotype.toCharArray();;

        boolean allGenesPresent;

        do{
            allGenesPresent = true;
            boolean[] found_gene = new boolean[8];
            for(int i = 0; i < length; i++){
                int gene = Character.getNumericValue(genotypeCharArr[i]);
                found_gene[gene] = true;
            }

            for(int i = 0; i < 8; i++){
                if(!found_gene[i]) {
                    allGenesPresent = false;
                    int geneToReplaceIndex = random.nextInt(length);
                    char gene = Character.forDigit(i, 10);
                    genotypeCharArr[geneToReplaceIndex] = gene;
                }
            }
        }while(!allGenesPresent);

        Arrays.sort(genotypeCharArr);
        String genotype = new String(genotypeCharArr);

        return genotype;
    }
}
