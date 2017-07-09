/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import static cc.style.CodeCheckStyle.WELCOME_CLOSE_BUTTON;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_MAC;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_ROUND;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_WIN;
import static cc.style.CodeCheckStyle.WELCOME_TITLE_BAR_MAC;
import static cc.style.CodeCheckStyle.WELCOME_TITLE_BAR_WIN;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_PANEL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_HEADER;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL_MAC;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL_WIN;
import static djf.settings.AppPropertyType.APP_CSS;
import static djf.settings.AppPropertyType.APP_PATH_CSS;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWelcomeView extends BorderPane {
    public static String OS = System.getProperty("os.name").toLowerCase();
    private static double xOrigin = 0;
    private static double yOrigin = 0;

    Stage primaryStage;
    private CodeCheckWelcomeViewController controller;
    private CodeCheckApp app;
    private VBox recentsPanel,welcomePanel;
    private Button closeWindowButton;
    private HBox titleBar;

    Label recentsHeaderLabel;
    Button newCodeCheckButton;
    Button recentCheckButtons[];
    
   public CodeCheckWelcomeView(CodeCheckApp currentApp){
        //showData = data;
        //primaryStage = stage;
        app = currentApp;
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        boolean success = app.loadProperties(APP_PROPERTIES_FILE_NAME);
        //DEAL WITH A FAILURE HERE
        
        
        //String title = (data.getShowTitle() == null || data.getShowTitle().isEmpty()) ? props.getProperty(UNTITLED_SHOW_TEXT) : data.getShowTitle(); 
        //primaryStage.setTitle(title);
        initWindow();
        initLayout();
        initControllers();        
    }
   private void initWindow() {
        primaryStage = app.getWelcomeStage();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        //primaryStage.setX(bounds.getMinX());
        //primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth()/2);
        primaryStage.setHeight(bounds.getHeight()/2);
   }
    private void initLayout() {
        Rectangle rect = new Rectangle(primaryStage.getWidth(),primaryStage.getHeight());
        rect.setArcHeight(10.0);
        rect.setArcWidth(10.0);
        setClip(rect);
    
        //ADD CUSTOM 'TITLEBAR'
        closeWindowButton = new Button("x");
        closeWindowButton.setPadding(new Insets(0, 10, 5, 10));
        titleBar = new HBox(closeWindowButton);
        
        if(OS.contains("mac")){
            titleBar.setMaxWidth(300);
            titleBar.setAlignment(Pos.BASELINE_LEFT);
        }else{
            BorderPane.setAlignment(titleBar, Pos.BOTTOM_RIGHT);
            //titleBar.setMaxWidth(primaryStage.getWidth()-299);
            titleBar.setMaxWidth(300);
            titleBar.setAlignment(Pos.BASELINE_RIGHT);
            
        }
        setTop(titleBar);

        newCodeCheckButton = new Button("Create New Code Check");
        newCodeCheckButton.setPadding(new Insets(5, 5, 5, 5));
        //BorderPane.setAlignment(newCodeCheckButton, Pos.BOTTOM_CENTER);

        recentsPanel = new VBox();
        recentsPanel.setPadding(new Insets(25, 50, 50, 20));
        recentsPanel.setSpacing(10);
        recentsPanel.setPrefWidth(300);

        recentsHeaderLabel = new Label("Recent Work");

        recentsPanel.getChildren().add(recentsHeaderLabel);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        welcomePanel = new VBox(spacer,newCodeCheckButton);
        VBox.setVgrow(welcomePanel, Priority.ALWAYS);
        welcomePanel.setAlignment(Pos.CENTER);
        VBox container = new VBox(welcomePanel);
        if(OS.contains("mac")){
            container.getStyleClass().add(WELCOME_PANEL_MAC);
        }else{
            container.getStyleClass().add(WELCOME_PANEL_WIN);            
        }
        setCenter(container);
        VBox.setMargin(welcomePanel, new Insets(22,22,100,22)); // optional

        setLeft(recentsPanel);
    }
   private void initControllers() {
        controller = new CodeCheckWelcomeViewController(app);

        newCodeCheckButton.setOnAction(e -> {
            controller.handleNewCodeCheckRequest();
        });
        primaryStage.sceneProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                initStyle(newValue);
            }

        });
        closeWindowButton.setOnAction(e->{
            controller.handleCloseRequest();
        });
        setOnMousePressed(e-> {
                xOrigin = primaryStage.getX() - e.getScreenX();
                yOrigin = primaryStage.getY() - e.getScreenY();
        });
        setOnMouseDragged(e-> {
                primaryStage.setX(e.getScreenX() + xOrigin);
                primaryStage.setY(e.getScreenY() + yOrigin);
        });

   }
    private void initStyle(Scene primaryScene) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();

	primaryScene.getStylesheets().add(stylesheetPath);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryScene.setFill(Color.TRANSPARENT);
        setPadding(new Insets(7,7,7,7));
        //primaryScene.getRoot().setEffect(new DropShadow());
        
        if(OS.contains("mac")){
            //getStyleClass().add(WELCOME_PANEL_MAC);
            titleBar.getStyleClass().add(WELCOME_TITLE_BAR_MAC);
            recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL_MAC);
        }else{
            //getStyleClass().add(WELCOME_PANEL_WIN);
            titleBar.getStyleClass().add(WELCOME_TITLE_BAR_WIN);
            recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL_WIN);
            
        }
        getStyleClass().add(WELCOME_PANEL_ROUND);
        
        welcomePanel.getStyleClass().add(WELCOME_VIEW_PANEL);
 
        recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL);
        recentsHeaderLabel.getStyleClass().add(WELCOME_VIEW_RECENTS_HEADER);
        closeWindowButton.getStyleClass().add(WELCOME_CLOSE_BUTTON);

    }
}
