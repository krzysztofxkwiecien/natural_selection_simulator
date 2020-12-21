package IO;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.stage.Stage;

public class ErrorLogger {

    public void log(String message){

        Stage stage = new Stage();
        stage.setTitle("Error");

        Label errorMsgLabel = new Label(message);

        Scene scene = new Scene(errorMsgLabel, 250, 70);
        errorMsgLabel.setAlignment(Pos.CENTER);

        stage.setScene(scene);
        stage.show();

    }

}
