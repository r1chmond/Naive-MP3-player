import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;


public class AudioPlayer extends Application {

    private Stage stage;
    private boolean isPlay = true;

    private Image playImg;
    private Image pauseImg;
    private ImageView playImgView;

    private Label lbltimePlayed;
    private Label lbltotalTime;

    private ArrayList<Media> tracks;
    private ListView<Track> trackListView;
    private ObservableList<Track> trackObservableList;

    private MediaPlayer player;
    private Media media;
    private Slider volumeCount;
    private Slider audioCount;

    private TrackReader reader;
    private int trackIndex;


    /**
     * Constructor
     */
    public AudioPlayer()
    {
        tracks = new ArrayList<>();
        trackIndex = 0;
        trackObservableList = FXCollections.observableArrayList();
        trackListView = new ListView<>(trackObservableList);
        reader = new TrackReader();
    }
    /**
     * Action to perform when the play
     * button is pressed
     */
    public void playBtnAction(ActionEvent event)
    {
        if(player == null){
            return;
        }
        if(isPlay){
            playImgView.setImage(pauseImg);
            isPlay = false;
            player.play();

        }
        else{
            playImgView.setImage(playImg);
            isPlay = true;
            player.pause();
        }
    }

    /**
     * The action performed when the next button
     * is clicked
     */
    public void nextBtnAction(ActionEvent event)
    {
        if(player == null){
            return;
        }
        else{
            player.stop();
        }
        if(trackIndex >= 0 && trackIndex < tracks.size() - 1){
            media = tracks.get(trackIndex + 1);
            player = new MediaPlayer(media);
            trackIndex++;
        }
        if(!isPlay) {
            player.play();
        }
        enableTrackSlider();
        player.volumeProperty().bind(volumeCount.valueProperty().divide(100));
    }

    /**
     * The action performed when the previous button
     * is clicked
     */
        public void prevBtnAction(ActionEvent event)
        {
            if(player == null){
                return;
            }
            else{
                player.stop();
            }
            if(trackIndex > 0){
                media = tracks.get(trackIndex - 1);
                player = new MediaPlayer(media);
                trackIndex--;
            }
            if(!isPlay) {
                player.play();
            }
            enableTrackSlider();
            player.volumeProperty().bind(volumeCount.valueProperty().divide(100));
        }

    /**
     * Opens file dialog when button is pressed
     * @param event mouse click event
     */
    public void addFileAction(ActionEvent event)
    {
        if(player != null){
            player.stop();
            playImgView.setImage(playImg);
            isPlay = true;
        }
        try {
            FileChooser fileChooser = new FileChooser();
           fileChooser.getExtensionFilters().addAll(
                   new FileChooser.ExtensionFilter("mp3", "*.mp3")
            );

            File file = fileChooser.showOpenDialog(stage);


            if (file != null) {
                URI uri = file.toURI();
                String path = uri.toString();
                media = new Media(path);
                player = new MediaPlayer(media);
                tracks.add(media);
                trackIndex = tracks.size() - 1;
                trackObservableList.add(reader.decodeDetails(file));
            }
            audioCount.setDisable(false);
            enableTrackSlider();
            player.volumeProperty().bind(volumeCount.valueProperty().divide(100));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * Links the player to the slider.
     * Sets the max duration of track.
     * Keeps reference of the current duration of track.
     */
    public void enableTrackSlider()
    {
        player.currentTimeProperty().addListener((observableValue, duration, t1) -> {
            if(!audioCount.isValueChanging()){
                double totalTime = player.getTotalDuration().toMillis();
                double currentTime = player.getCurrentTime().toMillis();
                audioCount.setMax(totalTime);
                audioCount.setValue(currentTime);
                lbltimePlayed.setText(getTimeString(currentTime));
                lbltotalTime.setText(getTimeString(totalTime));
            }
        });
    }

    /**
     * Closes program when selected
     */
    public void exitAction(ActionEvent event)
    {
        System.exit(0);
    }

    /**
     * Initialises track list
     * If an mp3 file is added then it
     * appears on the list
     */
    private void initialiseTrackList()
    {
        trackListView
                .setCellFactory(new Callback<>() {
                    @Override
                    public ListCell<Track> call(ListView<Track> trackListView) {

                        ListCell<Track> cell = new ListCell<>() {
                            @Override
                            public void updateItem(Track list, boolean empty) {
                                super.updateItem(list, empty);
                                if (list != null) {
                                    setText(list.getDetails());
                                }
                            }
                        };
                        cell.setOnMouseClicked(mouseEvent -> {

                            int index = trackListView.getSelectionModel().getSelectedIndex();
                            if(index < 0){
                                return;
                            }
                            playImgView.setImage(pauseImg);
                            isPlay = false;
                            if (player != null ) {
                                player.stop();
                                media = tracks.get(index);
                                player = new MediaPlayer(media);
                                player.play();
                                trackIndex = index;
                            }
                            enableTrackSlider();
                            player.volumeProperty().bind(volumeCount.valueProperty().divide(100));
                        });
                        return cell;
                    }
                });
    }

    /**
     * Main method
     * @param stage the main stage
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        VBox root = new VBox();
    createMenubar(root);
    initialiseTrackList();

        //--- top ---
        Button addBtn = new Button("Add mp3 file");
        addBtn.getStyleClass().add("addBtnStyle");
        addBtn.setOnAction(this::addFileAction);
        VBox topSection = new VBox(addBtn);
        topSection.setAlignment(Pos.CENTER);

        //--- centre ---
        Image logo = new Image("images/appicon.png");
       ImageView forLogo = new ImageView(logo);
       forLogo.setFitHeight(350);
       forLogo.setFitWidth(300);

    HBox centre = new HBox(forLogo);
    centre.setSpacing(25);
    trackListView.setPrefHeight(350);
    centre.getChildren().addAll(trackListView);
    centre.setAlignment(Pos.CENTER);
    centre.setPadding(new Insets(50, 0, 50, 0));

    //--- bottom ---
        VBox bottomSection = new VBox();

    HBox sliders = new HBox();

    // volume slider
   volumeCount = new Slider();
    volumeCount.setValue(50);
    volumeCount.setPrefWidth(50);
    sliders.setAlignment(Pos.CENTER);
    sliders.setPadding(new Insets(5, 0, 5, 150));
    Label volumeLbl = new Label("Volume ");
    volumeLbl.setPadding(new Insets(0,0,0, 65));

    // move while audio plays
    audioCount = new Slider();
    audioCount.setDisable(true);
    audioCount.setPrefWidth(300);
    audioCount.setMaxWidth(Region.USE_PREF_SIZE);
    audioCount.setMinWidth(30);
    audioCount.setMin(0);

    audioCount.setOnMousePressed(mouseEvent -> { // for seeking while music plays
        if(player != null) {
            double currentValue = audioCount.getValue();
            Duration duration = new Duration(currentValue);
            player.seek(duration);
        }
    });

    lbltimePlayed = new Label();
    lbltotalTime = new Label();
    lbltimePlayed.setPadding(new Insets(3));
    lbltotalTime.setPadding(new Insets(3));

    sliders.getChildren().addAll( lbltimePlayed,audioCount,lbltotalTime, volumeLbl, volumeCount);

    HBox controls = new HBox();
    Image prevImg = new Image("images/previcon.png");
    ImageView prevImgView = new ImageView(prevImg);
    prevImgView.setFitHeight(25);
    prevImgView.setFitWidth(30);
    Button prevBtn = new Button("", prevImgView);
    prevBtn.setOnAction(this::prevBtnAction);

   playImg = new Image("images/playicon.png");
    playImgView = new ImageView(playImg);
    playImgView.setFitHeight(25);
    playImgView.setFitWidth(30);
    Button playBtn = new Button("", playImgView);
    pauseImg = new Image("images/pause.png");
    playBtn.setOnAction(this::playBtnAction);

    Image nextImg = new Image("images/nexticon.png");
    ImageView nextImgView = new ImageView(nextImg);
    nextImgView.setFitHeight(25);
    nextImgView.setFitWidth(30);
    Button nextBtn = new Button("", nextImgView);
    nextBtn.setOnAction(this::nextBtnAction);

    controls.getChildren().addAll(prevBtn, playBtn, nextBtn);
    controls.setAlignment(Pos.BOTTOM_CENTER);
    controls.setSpacing(8);

    bottomSection.getChildren().addAll(sliders, controls);



    // -- main  content structure --

        BorderPane contentPane = new BorderPane(centre, topSection, null, bottomSection, null);
        BorderPane.setMargin(topSection, new Insets(20, 0, 5, 0));

    root.getChildren().add(contentPane);

    Scene scene = new Scene(root, 700, 650);
    scene.getStylesheets().addAll("playerStyleSheet.css");

    stage.getIcons().add(new Image("images/appicon.png"));
    stage.setTitle("My mp3 player");
    stage.setScene(scene);
    stage.show();
    }

    /**
     * Creates the menu bar
     * @param parent The root node
     */
    public void createMenubar(Pane parent)
    {
        MenuBar menuBar = new MenuBar();


        Menu fileMenu = new Menu("File");
        Menu helpMenu = new Menu("Help");

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(this::exitAction);
        fileMenu.getItems().add(exitItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);

        parent.getChildren().add(menuBar);
    }

    /**
     *
     * @param time
     * @return
     */
    private String getTimeString(double time)
    {
        time /= 1000;
        String seconds = formatTime(time % 60);

        time /= 60;
        String minutes = formatTime(time % 60);

        time /= 60;
        String hours = formatTime(time % 60);

        if (hours.equals("0")){
            return minutes + ":" + seconds;
        }
        else {
            return hours + ":" + minutes + ":" + seconds;
        }
    }

    /**
     * Casts the time into an integer
     * and changes it to a string
     * @param time
     * @return time The time(duration) of the track
     */
    private String formatTime(double time)
    {
        int t = (int)time;
       return String.valueOf(t);
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args)
    {
        launch();
    }
}
