package Simulation;

import IO.ErrorLogger;
import javafx.application.Application;
import javafx.stage.Stage;
import IO.JSONParametersReader;

import java.security.spec.ECField;

public class SimulationLauncher extends Application {

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {

        try{
            JSONParametersReader jsonReader = new JSONParametersReader();
            SimulationParameters simulationParameters = jsonReader.getParameters();

            for(int i = 0; i < simulationParameters.numberOfWindows; i++){
                SimulationEngine simulationEngine = new SimulationEngine();
                simulationEngine.start(simulationParameters);
            }
        }
        catch(Exception e){
            ErrorLogger errorLogger = new ErrorLogger();
            errorLogger.log(e.getMessage());
        }

    }
}
