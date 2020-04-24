package ESSExercise;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Zoomausominaisuus ja seinien piirto lisätty.
 * Zoom/pan ei toimi täysin optimaalisesti
 *
 * @Pihla
 */

public class Main extends Application {

    private Project project;
    private int clickCount;

    @Override
    public void start(Stage primaryStage)
            throws Exception {

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);


        Menu menu = new Menu("File");
        MenuItem openProject = new MenuItem("Open Project");
        MenuItem quit = new MenuItem("Quit");
        menu.getItems().add(openProject);
        menu.getItems().add(quit);
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(menu);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setSpacing(10);

        hBox.setPadding((new Insets(20)));

        borderPane.setRight(hBox);

        ComboBox comboBox = new ComboBox();
        comboBox.setValue("No map selected");
        Label mapText = new Label("Map");
        Button addWall = new Button("Add Wall");
        ScrollPane scrollPane = new ScrollPane();
        StackPane stackPane = new StackPane();
        scrollPane.pannableProperty().set(true);
        scrollPane.setContent(stackPane);
        //scrollPane.prefViewportWidthProperty().bind(borderPane.widthProperty());
        //scrollPane.prefViewportHeightProperty().bind(borderPane.heightProperty());
        //removed scrollpane resizing --> messed too much with the zoom/wall drawing
        borderPane.setPrefSize(800, 700);
        scrollPane.setPrefSize(700, 500);
        scrollPane.setPadding(new Insets(20));


        borderPane.setCenter(new Group(scrollPane));
        borderPane.setTop(menuBar);


        stackPane.addEventFilter(ScrollEvent.ANY, event -> {
            Scale scale = new Scale();
            scale.setPivotX(event.getX());
            scale.setPivotY(event.getY());
            double zoomFactor = 1.20;
            double deltaY = event.getDeltaY();
            if (deltaY < 0) {
                zoomFactor = 2.0 - zoomFactor;

            }
            scale.setX(stackPane.getScaleX() * zoomFactor);
            scale.setY(stackPane.getScaleY() * zoomFactor);
            stackPane.getTransforms().add(scale);

        });

        openProject.setOnAction((e) -> chooseFile(e, primaryStage, stackPane, comboBox));

        quit.setOnAction((e) -> quitApplication(e));

        comboBox.setOnAction((e) -> setActionsToComboBox(e, comboBox, stackPane));

        addWall.setOnAction(e -> enableWallAdding(e, scrollPane, stackPane));

        hBox.getChildren().add(addWall);
        hBox.getChildren().add(mapText);
        hBox.getChildren().add(comboBox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ESS Exercise");
        primaryStage.show();

    }

    public void enableWallAdding(Event event, ScrollPane scrollPane, StackPane stackPane) {
        Canvas canvas = new Canvas();
        canvas.heightProperty().bind(scrollPane.heightProperty());
        canvas.widthProperty().bind(scrollPane.widthProperty());

        stackPane.getChildren().add(canvas);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setStroke(Color.BLUE);
        //graphicsContext.setLineWidth(3.0); enable to get 3 pxl lines

        ArrayList<Double> startPoint = new ArrayList<>();
        ArrayList<Double> endPoint = new ArrayList<>();

        clickCount = 1;
        canvas.setOnMouseClicked((e) -> {
            System.out.println(clickCount);
            if (clickCount % 2 == 0) {
                double endX = e.getX();
                double endY = e.getY();
                endPoint.add(endX);
                endPoint.add(endY);
                WallLine line = new WallLine(startPoint, endPoint); //drawn lines could be saved to file etc. Now lines are not saved --> drawn lines are lost if map is changed
                graphicsContext.strokeLine(line.getPoint1().get(0), line.getPoint1().get(1), line.getPoint2().get(0), line.getPoint2().get(1));
                endPoint.clear();
                startPoint.clear();
            }
            if (!(clickCount % 2 == 0)) {
                double startX = e.getX();
                double startY = e.getY();
                startPoint.add(startX);
                startPoint.add(startY);
            }
            clickCount++;
        });
    }


    public void setActionsToComboBox(Event event, ComboBox comboBox, StackPane stackPane) {
        Map selectedMap = (Map) comboBox.getSelectionModel().getSelectedItem();
        int mapID = selectedMap.getId();
        String mapFileName = "mapimage-" + String.valueOf(mapID) + ".png";
        Path mapsPath = drawWalls(project, mapID);
        Pane drawnMap = new Pane(mapsPath);
        showMapAndWalls(drawnMap, mapFileName, stackPane);

    }

    public void chooseFile(Event event, Stage primaryStage, StackPane stackPane, ComboBox comboBox) {

        System.out.println("Open Project clicked");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");

        File xmlFile = fileChooser.showOpenDialog(primaryStage);
        if (xmlFile != null) {
            XmlMapper xmlMapper = new XmlMapper();
            try {
                String xml = inputStreamToString(new FileInputStream(xmlFile));
                project = xmlMapper.readValue(xml, Project.class);
                int mapId = project.getMaps().get(0).getId(); //choose first map from the project to show as default
                Path mapPath = drawWalls(project, mapId);
                Pane drawing = new Pane(mapPath);

                String mapFileName = "mapimage-" + mapId + ".png";
                showMapAndWalls(drawing, mapFileName, stackPane);

                ObservableList<Map> maps = FXCollections.observableArrayList();
                maps.addAll(project.getMaps());
                comboBox.setItems(maps);
                comboBox.getSelectionModel().select(0);

            } catch (IOException ex) {
                ex.printStackTrace();
                stackPane.getChildren().add((new Label("There was an error in loading the project file!")));
            }
        }
    }

    public void quitApplication(Event event) {
        System.out.println("Quit clicked");
        Platform.exit();
        System.exit(0);
    }

    public void showMapAndWalls(Pane drawing, String mapFileName, StackPane stackPane) {
        Image mapsImage = new Image("file:" + mapFileName);
        ImageView mapsImageView = new ImageView(mapsImage);
        mapsImageView.preserveRatioProperty().set(true);
        mapsImageView.setImage(mapsImage);

        stackPane.getChildren().addAll(mapsImageView, drawing);
        System.out.println(stackPane.getChildren().toString());
    }

    public String inputStreamToString(InputStream is)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }
    }

    public Path drawWalls(Project project, int map) {

        List<WallSegment> wallSegments = project.getWallSegments();
        List<WallPoint> wallPoints = project.getWallPoints();

        ArrayList<WallPoint> selectedMapWallPoints = new ArrayList<>();

        for (WallPoint point : wallPoints) {
            if (point.getMapId() == map) {
                selectedMapWallPoints.add(point);
            }
        }

        HashMap<Integer, WallPoint> mappedById = new HashMap<>();
        for (int i = 0; i < selectedMapWallPoints.size(); i++) {
            WallPoint wp = selectedMapWallPoints.get(i);
            mappedById.put(wp.getId(), wp);
        }

        ArrayList<WallLine> lines = new ArrayList<>();

        for (WallSegment segment : wallSegments) {
            WallLine line = new WallLine();
            int p1Id = segment.getP1();
            int p2Id = segment.getP2();
            WallPoint p1 = mappedById.get(p1Id);
            if (p1 != null) {
                ArrayList<Double> xy1 = new ArrayList<>();
                xy1.add(p1.getX()); //Gives NullPointerException
                xy1.add(p1.getY());
                line.setPoint1(xy1);
                WallPoint p2 = mappedById.get(p2Id);
                ArrayList<Double> xy2 = new ArrayList<>();
                xy2.add(p2.getX());
                xy2.add(p2.getY());
                line.setPoint2(xy2);
            }

            line.setType(segment.getType());
            lines.add(line);
        }

        Path path = new Path();

        path.setStrokeWidth(1.0); // change to 3.0 for 3 pxl lines
        path.setStroke(Color.RED);
        path.setFill(null);

        for (WallLine drawline : lines) {
            if (drawline.getPoint1() != null) {
                MoveTo moveTo = new MoveTo(drawline.getPoint1().get(0), drawline.getPoint1().get(1));
                LineTo lineTo = new LineTo(drawline.getPoint2().get(0), drawline.getPoint2().get(1));
                path.getElements().add(moveTo);
                path.getElements().add(lineTo);
            }
        }
        return path;
    }

    public static void main(String[] args) {

        launch(Main.class);
    }

}

