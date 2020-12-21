package Simulation;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import Project.Animal;
import Project.Grass;
import Project.Vector2d;
import Project.WorldMap;

public class Visualiser {

    private GraphicsContext map2D;

    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private XYChart.Series animalSeries;
    private XYChart.Series grassSeries;

    private final Text eraValueText = new Text("-");
    private final Text animalsValueText = new Text("-");
    private final Text grassesValueText = new Text("-");
    private final Text averageEnergyValueText = new Text("-");
    private final Text dominatingGenomeValueText = new Text("-");
    private final Text lifeExpectancyValueText = new Text("-");
    private final Text averageChildrenValueText = new Text("-");
    private final Text selectedGenomeValueText = new Text("-");
    private final Text selectedChildrenValueText = new Text("-");
    private final Text selectedDescendantsValueText = new Text("-");
    private final Text selectedEraOfDeathValueText = new Text("-");

    private final int squareSize;
    private final int mapWidth;
    private final int mapHeight;
    private final Simulation simulation;
    private final WorldMap map;
    private final SimulationParameters simulationParameters;

    private final Vector2d jungleTopLeft;
    private final Vector2d jungleBottomRight;

    private int selectedAnimalEraOfDeath = -1;

    public Visualiser(Simulation simulation, WorldMap map, Vector2d jungleTopLeft, Vector2d jungleBottomRight, SimulationParameters simulationParameters){
        this.simulation = simulation;
        this.map = map;
        this.simulationParameters = simulationParameters;
        this.mapWidth = simulationParameters.width;
        this.mapHeight = simulationParameters.height;
        this.squareSize = (int)(simulationParameters.windowHeight / mapHeight);

        this.jungleTopLeft = jungleTopLeft;
        this.jungleBottomRight = jungleBottomRight;

        initialize();
    }

    private void initialize(){
        Stage stage = new Stage();
        stage.setTitle("Simulation");

        Canvas canvas = new Canvas();
        canvas.setWidth(mapWidth * squareSize);
        canvas.setHeight(mapHeight * squareSize);
        map2D = canvas.getGraphicsContext2D();

        Button pauseButton = createPauseButton();

        TextField statisticsTextField = new TextField();
        Button statisticsButton = createStatisticsButton(statisticsTextField);

        TextField eraTextField = new TextField();
        Button eraJumpButton = createEraJumpButton(eraTextField);

        Button displayDominatingButton = createDisplayDominatingButton();

        EventHandler<MouseEvent> animalSelectHandler = e -> {
            Vector2d location = new Vector2d((int)(e.getSceneX() / squareSize), (int)(e.getSceneY() / squareSize));
            selectAnimalAt(location);
        };

        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, animalSelectHandler);

        Text eraTitleText = new Text("Era:");
        Text animalsTitleText = new Text("Animals:");
        Text grassesTitleText = new Text("Grasses:");
        Text averageEnergyTitleText = new Text("Average energy:");
        Text dominatingGenomeTitleText = new Text("Dominant genotype:");
        Text lifeExpectancyTitleText = new Text("Life expectancy:");
        Text averageChildrenTitleText = new Text("Average children:");
        Text selectedGenomeTitleText = new Text("Genotype:");
        Text selectedChildrenTitleText = new Text("Children:");
        Text selectedDescendantsTitleText = new Text("Descendants:");
        Text selectedEraOfDeathTitleText = new Text("Era of death:");
        Text eraJumpText = new Text("\nJump n eras forward:");
        Text statisticsText = new Text("\nSave average statistics from n eras:");

        Text generalInfoText = new Text(10, 20, "General information:\n");
        generalInfoText.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));
        Text selectedInfoText = new Text(10, 20, "Selected animal information:\n");
        selectedInfoText.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 16));

        VBox generalInfoBox = new VBox();
        generalInfoBox.getChildren().addAll(
                generalInfoText,
                eraTitleText, eraValueText,
                animalsTitleText, animalsValueText,
                grassesTitleText, grassesValueText,
                averageEnergyTitleText, averageEnergyValueText,
                dominatingGenomeTitleText, dominatingGenomeValueText,
                lifeExpectancyTitleText, lifeExpectancyValueText,
                averageChildrenTitleText, averageChildrenValueText,
                statisticsText, statisticsTextField, statisticsButton
        );

        VBox selectedInfoBox = new VBox();
        selectedInfoBox.getChildren().addAll(
                selectedInfoText,
                selectedGenomeTitleText, selectedGenomeValueText,
                selectedChildrenTitleText, selectedChildrenValueText,
                selectedDescendantsTitleText, selectedDescendantsValueText,
                selectedEraOfDeathTitleText, selectedEraOfDeathValueText,
                eraJumpText, eraTextField, eraJumpButton
        );

        HBox mainButtonsBox = new HBox(pauseButton, displayDominatingButton);
        HBox statisticsBox = new HBox(generalInfoBox, selectedInfoBox);
        VBox rightSideBox = new VBox(createLineChart(), mainButtonsBox, statisticsBox);
        HBox container = new HBox(canvas, rightSideBox);

        pauseButton.setPrefWidth(180);
        displayDominatingButton.setPrefWidth(180);

        eraJumpButton.setPrefWidth(50);
        eraTextField.setMaxWidth(50);

        statisticsButton.setPrefWidth(50);
        statisticsTextField.setMaxWidth(50);

        generalInfoBox.setPadding(new Insets(5, 25, 0, 25));
        selectedInfoBox.setPadding(new Insets(5, 25, 0, 25));
        generalInfoBox.setMinWidth(270);
        selectedInfoBox.setMinWidth(270);

        mainButtonsBox.setMinHeight(40);
        mainButtonsBox.setAlignment(Pos.CENTER);
        mainButtonsBox.setSpacing(25);

        mainButtonsBox.setBackground(new Background(new BackgroundFill(Color.valueOf("#cccccc"), CornerRadii.EMPTY, Insets.EMPTY)));
        container.setBackground(new Background(new BackgroundFill(Color.valueOf("#eeeeee"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(container);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private Button createPauseButton(){

        Button pauseButton = new Button("Pause");

        pauseButton.setOnAction(actionEvent -> {
            if(simulation.isPaused()){
                simulation.pause(false);
                pauseButton.setText("Pause");
            }else{
                simulation.pause(true);
                pauseButton.setText("Resume");
            }
        });

        return pauseButton;
    }

    private Button createEraJumpButton(TextField eraTextField){
        Button eraJumpButton = new Button("Jump");
        eraJumpButton.setOnAction(actionEvent -> {
            if(simulation.isPaused() && simulation.getSelectedAnimal() != null){
                int era = Integer.parseInt(eraTextField.getText());
                simulation.jumpEras(era, false);
            }
        });

        return eraJumpButton;
    }

    private Button createStatisticsButton(TextField eraTextField){
        Button statisticsButton = new Button("Save");
        statisticsButton.setOnAction(actionEvent -> {
            if(simulation.isPaused()){
                int era = Integer.parseInt(eraTextField.getText());
                simulation.jumpEras(era, true);
            }
        });

        return statisticsButton;
    }

    private Button createDisplayDominatingButton(){
        Button displayDominatingButton = new Button("Display dominating genotype");
        displayDominatingButton.setOnAction(actionEvent -> {
            if(simulation.isPaused()){
                displayDominatingAnimals();
            }
        });

        return displayDominatingButton;
    }

    public void display(){
        drawMap();
        drawChart();
        drawStatistics();
    }

    private void drawChart(){
        // Clear chart every 2500 eras to avoid lag
        if(simulation.getCurrentEra() % 2500 == 0){
            xAxis.setLowerBound(simulation.getCurrentEra());
            animalSeries.getData().clear();
            grassSeries.getData().clear();
        }

        xAxis.setUpperBound(simulation.getCurrentEra());
        animalSeries.getData().add(new XYChart.Data(simulation.getCurrentEra(), getAnimalsCount()));
        grassSeries.getData().add(new XYChart.Data(simulation.getCurrentEra(), getGrassesCount()));
    }

    private void drawStatistics(){
        eraValueText.setText(String.valueOf(simulation.getCurrentEra()));
        animalsValueText.setText(String.valueOf(getAnimalsCount()));
        grassesValueText.setText(String.valueOf(getGrassesCount()));

        if(getAnimalsCount() > 0){
            averageEnergyValueText.setText(String.valueOf((int)(simulation.getTotalEnergy() / getAnimalsCount())));
            dominatingGenomeValueText.setText(simulation.getDominatingGenome());
            averageChildrenValueText.setText(String.valueOf((int)(simulation.getTotalChildren() / getAnimalsCount())));
        }else{
            averageEnergyValueText.setText(String.valueOf(0));
            dominatingGenomeValueText.setText("-");
            averageChildrenValueText.setText(String.valueOf(0));
        }

        if(simulation.getDeadAnimalsCount() > 0)
            lifeExpectancyValueText.setText(String.valueOf((int)(simulation.getTotalErasLived() / simulation.getDeadAnimalsCount())));
    }

    private void drawMap(){
        // Map background
        map2D.setFill(Color.valueOf("#00aa00"));
        map2D.fillRect(0, 0, mapWidth * squareSize, mapHeight * squareSize);

        // Map jungle
        map2D.setFill(Color.valueOf("#004400"));
        map2D.fillRect(jungleTopLeft.x * squareSize, jungleTopLeft.y * squareSize, (jungleBottomRight.x - jungleTopLeft.x) * squareSize, (jungleBottomRight.y - jungleTopLeft.y) * squareSize);

        // Map grasses
        map2D.setFill(Color.valueOf("#00ff00"));
        for(Grass grass : simulation.getGrasses()){
            Vector2d position = grass.getPosition();
            map2D.fillRoundRect(position.x * squareSize, position.y * squareSize, squareSize, squareSize, squareSize / 2f, squareSize / 2f);
        }

        // Map animals
        for(Animal animal : simulation.getAnimals()){
            map2D.setFill(energyToColor(animal.getEnergy()));
            Vector2d position = animal.getPosition();
            map2D.fillOval(position.x * squareSize, position.y * squareSize, squareSize, squareSize);
        }
    }

    public void displaySelectedAnimal(){
        if(simulation.getSelectedAnimal() != null){
            if (simulation.getAnimals().contains(simulation.getSelectedAnimal())) {
                map2D.setStroke(Color.valueOf("#ff0000"));
                map2D.setLineWidth(squareSize / 3f);
                Vector2d position = simulation.getSelectedAnimal().getPosition();
                map2D.strokeOval(position.x * squareSize, position.y * squareSize, squareSize, squareSize);
            }

            String genomeText = simulation.getSelectedAnimal().getGenotype();

            selectedGenomeValueText.setText(genomeText);
            selectedChildrenValueText.setText(String.valueOf(simulation.getSelectedAnimal().getChildrenCount()));
            selectedDescendantsValueText.setText(String.valueOf(simulation.getSelectedAnimal().getDescendantsCount()));
            if(selectedAnimalEraOfDeath == -1)
                selectedEraOfDeathValueText.setText("-");
            else
                selectedEraOfDeathValueText.setText(String.valueOf(selectedAnimalEraOfDeath));
        }
    }

    public void selectedAnimalDeadAt(int era){
        if(selectedAnimalEraOfDeath == -1)
            selectedAnimalEraOfDeath = era;
    }

    private void displayDominatingAnimals(){
        String dominatingGenome = simulation.getDominatingGenome();

        for(Animal animal : simulation.getAnimals()){
            if(animal.getGenotype().equals(dominatingGenome)){
                map2D.setStroke(Color.valueOf("#ff0000"));
                map2D.setLineWidth(squareSize / 3f);
                Vector2d position = animal.getPosition();
                map2D.strokeOval(position.x * squareSize, position.y * squareSize, squareSize, squareSize);
            }
        }
    }

    private Color energyToColor(float energy){
        float energyRatio = energy / (simulationParameters.startEnergy);
        if(energyRatio > 1)
            return Color.color(1, 1, 1);
        if(energyRatio >= 0.5)
            return Color.color(0, 183 / 255f * energyRatio, energyRatio);

        return Color.color(energyRatio, 0, energyRatio);
    }

    private void selectAnimalAt(Vector2d position){
        simulation.setSelectedAnimal(map.animalAt(position));
        selectedAnimalEraOfDeath = -1;
        drawMap();
        displaySelectedAnimal();
    }

    private int getAnimalsCount(){
        return simulation.getAnimals().size();
    }

    private int getGrassesCount(){
        return simulation.getGrasses().size();
    }

    private LineChart<Number, Number> createLineChart(){

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        xAxis.setForceZeroInRange(false);
        xAxis.setLabel("Era");
        xAxis.setAnimated(false);
        xAxis.setAutoRanging(false);
        xAxis.setTickUnit(100);

        yAxis.setLabel("Count");
        yAxis.setAnimated(false);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        animalSeries = new XYChart.Series();
        grassSeries = new XYChart.Series();

        animalSeries.setName("Animals");
        grassSeries.setName("Grasses");

        lineChart.getData().add(animalSeries);
        lineChart.getData().add(grassSeries);

        lineChart.setCreateSymbols(false);

        return lineChart;
    }

}
