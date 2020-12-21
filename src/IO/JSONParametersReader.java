package IO;

import java.io.FileReader;
import java.io.IOException;

import Simulation.SimulationParameters;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONParametersReader {

    public SimulationParameters getParameters() throws IOException, ParseException, IllegalArgumentException {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("parameters.json"))
        {
            JSONObject json = (JSONObject) jsonParser.parse(reader);

            int windowHeight = ((Long)json.get("windowHeight")).intValue();
            int millisecondsPerEra = ((Long)json.get("millisecondsPerEra")).intValue();
            int width = ((Long)json.get("width")).intValue();
            int height = ((Long)json.get("height")).intValue();
            int startAnimals = ((Long)json.get("startAnimals")).intValue();
            int startEnergy = ((Long)json.get("startEnergy")).intValue();
            int moveEnergy = ((Long)json.get("moveEnergy")).intValue();
            int plantEnergy = ((Long)json.get("plantEnergy")).intValue();
            int jungleRatio = ((Long)json.get("jungleRatio")).intValue();
            int numberOfWindows = ((Long)json.get("numberOfWindows")).intValue();

            if(millisecondsPerEra <= 0 || width <= 0 || height <= 0 || startAnimals <= 0 || startEnergy <= 0 ||
                    moveEnergy <= 0 || plantEnergy <= 0 || jungleRatio <= 0 || numberOfWindows <= 0)
                throw new IllegalArgumentException("All arguments should be positive.");

            return new SimulationParameters(windowHeight, millisecondsPerEra, width, height, startAnimals, startEnergy, moveEnergy, plantEnergy, jungleRatio, numberOfWindows);

        }
    }

}
