/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.viewer;

import java.net.URL;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author danniyazov
 */
public class CodeCheckResultsViewer extends BorderPane{
    protected Stage primaryStage;
    private URL resultsURL;
    public CodeCheckResultsViewer(Stage stage, URL url) {
        primaryStage = stage;
        stage.initModality(Modality.APPLICATION_MODAL);
        resultsURL = url;
        initWindow();
        initLayout();
        

  }
    private void initWindow(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        primaryStage.centerOnScreen();
        primaryStage.setWidth(bounds.getWidth()/1.23);
        primaryStage.setHeight(bounds.getHeight()/1.23);
        
    }
    private void initLayout() {
        final WebView browser = new WebView();
        browser.prefHeightProperty().bind(primaryStage.heightProperty().subtract(40));
        browser.prefWidthProperty().bind(primaryStage.widthProperty().subtract(20));
        final WebEngine webEngine = browser.getEngine();
        setCenter(browser);        
        browser.getEngine().setOnAlert((WebEvent<String> wEvent) -> {
            System.out.println("Alert Event  -  Message:  " + wEvent.getData());
        });
        webEngine.load(resultsURL.toString());
    }
}
