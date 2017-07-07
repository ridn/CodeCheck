/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import static cc.style.CodeCheckStyle.WELCOME_PANEL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_HEADER;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL;
import static djf.settings.AppPropertyType.APP_CSS;
import static djf.settings.AppPropertyType.APP_PATH_CSS;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWelcomeView extends BorderPane {
    Stage primaryStage;
    private CodeCheckWelcomeViewController controller;
    private CodeCheckApp app;
    private VBox recentsPanel;
    Label recentsHeaderLabel;
    Button newCodeCheckButton;
    Button recentCheckButtons[];
    
   public CodeCheckWelcomeView(CodeCheckApp currentApp){
        //showData = data;
        //primaryStage = stage;
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        app = currentApp;
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

        newCodeCheckButton = new Button("Create New Code Check");
        newCodeCheckButton.setPadding(new Insets(5, 5, 5, 5));
        BorderPane.setMargin(newCodeCheckButton, new Insets(22,22,122,22)); // optional
        BorderPane.setAlignment(newCodeCheckButton, Pos.BOTTOM_CENTER);

        recentsPanel = new VBox();
        recentsPanel.setPadding(new Insets(50, 50, 50, 10));
        recentsPanel.setSpacing(10);
        recentsPanel.setPrefWidth(300);

        recentsHeaderLabel = new Label("Recent Work");

        recentsPanel.getChildren().add(recentsHeaderLabel);

        setCenter(newCodeCheckButton);

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

   }
    private void initStyle(Scene primaryScene) {
        //PropertiesManager props = PropertiesManager.getPropertiesManager();
	//String stylesheet = props.getProperty(APP_PATH_CSS);
	//stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource("style/cc_style.css");
	String stylesheetPath = stylesheetURL.toExternalForm();
	primaryScene.getStylesheets().add(stylesheetPath);
        getStyleClass().add(WELCOME_PANEL);
        
        recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL);
        recentsHeaderLabel.getStyleClass().add(WELCOME_VIEW_RECENTS_HEADER);

    }
}
