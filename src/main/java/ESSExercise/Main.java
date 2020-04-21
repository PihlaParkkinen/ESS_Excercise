package ESSExercise;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Baby's first JavaFx application
 * Pari huomiota:
 * Tässä ei olla vielä sotkeennuttu yhtään MVC asioihin eli toteutettu ilman hajua koko konseptista.
 * Zoomia en saanut toimimaan ja se alko vituttaa niin että päätin skipata koko homman.
 * Seinien piirtelystä on kommentoitu pois yritelmä, en vaan oo ihan varma onko tommoset sisäkkäiset eventhandlerit
 * ok vai ei. Joka tapauksessa se ei toimi vielä, canvas menee kyllä stackpanen päällimmäiseksi mutta piirto ei skulaa.
 * Tarjouluehdotus: suositellaan nautittavaksi kaljan kanssa.
 * @Pihla
 */



public class Main extends Application {

        private Project project;

    @Override
    public void start(Stage primaryStage) throws Exception{

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

        hBox.setPadding((new Insets(40)));


        borderPane.setRight(hBox);

        ComboBox comboBox = new ComboBox();
        comboBox.setValue("No map selected");
        Label mapText = new Label("Map");
        Button addWall = new Button("Add Wall");
        ScrollPane scrollPane = new ScrollPane();
        StackPane stackPane = new StackPane();

        scrollPane.pannableProperty().set(true);


        scrollPane.setContent(stackPane);
        borderPane.setPrefSize(800,700);
        scrollPane.setPrefSize(700,500);
        borderPane.setCenter(new Group(scrollPane));
        borderPane.setTop(menuBar);

      /* DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                scrollPane.setFitWidth(zoomProperty.get() * 4);
                mapImageView.setFitHeight(zoomProperty.get() * 3);
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

       */

        openProject.setOnAction(e -> {
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
                     int mapNro = 2; // initial view shows map 2 "simple" as default
                     Path mapPath = drawMap(project, mapNro);
                     Pane drawing = new Pane(mapPath);

                    String mapFileName = "mapimage-2.png";
                    showProject(drawing,mapFileName,stackPane);

                    ObservableList<Map> maps = FXCollections.observableArrayList();
                    maps.addAll(project.getMaps());
                    comboBox.setItems(maps);
                    comboBox.getSelectionModel().select(0);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        quit.setOnAction(e -> {
            System.out.println("Quit clicked");
            Platform.exit();
            System.exit(0);
        });


        comboBox.setOnAction(e -> {
            Map selectedMap = (Map) comboBox.getSelectionModel().getSelectedItem();
            int mapID = selectedMap.getId();
            String mapFileName = "mapimage-" + String.valueOf(mapID) + ".png";
            Path mapsPath = drawMap(project, mapID);
            Pane drawnMap = new Pane(mapsPath);
            showProject(drawnMap,mapFileName,stackPane);

        });

        /*addWall.setOnAction(e -> {
            Canvas canvas = new Canvas();
            GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
            graphicsContext.setFill(Color.BLUE);
            stackPane.getChildren().add(canvas);


            canvas.setOnMousePressed((event) -> {
                double startX = event.getX();
                double startY = event.getY();
                graphicsContext.moveTo(startX, startY);
            });
            canvas.setOnMouseClicked(event -> {
                double endX = event.getX();
                double startY = event.getY();
                graphicsContext.lineTo(endX,startY);
            });
        });*/

        hBox.getChildren().add(addWall);
        hBox.getChildren().add(mapText);
        hBox.getChildren().add(comboBox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("ESS Exercise");
        primaryStage.show();

    }

    public void showProject(Pane drawing, String mapFileName, StackPane stackPane){
        Image mapsImage = new Image("file:"+mapFileName);
        ImageView mapsImageView = new ImageView(mapsImage);
        mapsImageView.preserveRatioProperty().set(true);
        mapsImageView.setImage(mapsImage);

        stackPane.getChildren().addAll(mapsImageView,drawing);
    }

    public String inputStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }


    public Path drawMap(Project project, int map) {

        List<WallSegment> wallSegments = project.getWallSegments();
        List<WallPoint> allWallPoints = project.getWallPoints();

        ArrayList<WallPoint> map2WallPoints = new ArrayList<>();


        for (WallPoint point: allWallPoints){
            if(point.getMapId() == map){
                map2WallPoints.add(point);
            }
        }

        ArrayList<WallLine> lines = new ArrayList<>();
        for(WallSegment segment:wallSegments){
            int p1=segment.getP1();
            int p2 = segment.getP2();
            WallLine line = new WallLine();
            for(WallPoint point: map2WallPoints){
                line.setMapId(point.getMapId());
                if(p1==point.getId()){
                    ArrayList<Double> xy = new ArrayList<>();
                    xy.add(point.getX());
                    xy.add(point.getY());
                    line.setPoint1(xy);
                }
                if(p2==point.getId()){
                    ArrayList<Double> xy = new ArrayList<>();
                    xy.add(point.getX());
                    xy.add(point.getY());
                    line.setPoint2(xy);
                }

            }
            line.setType(segment.getType());
            lines.add(line);
        }


        Path path = new Path();

        path.setStrokeWidth(1.0); // change to 3.0 for 3 pxl lines?
        path.setStroke(Color.RED);
        path.setFill(null);


        for(WallLine drawline:lines){
            if(drawline.getPoint1()!=null) {
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

