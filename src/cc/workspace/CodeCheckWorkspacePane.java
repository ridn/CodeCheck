/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.style.CodeCheckStyle.WORKSPACE_PANE;
import static djf.settings.AppPropertyType.APP_CSS;
import static djf.settings.AppPropertyType.APP_PATH_CSS;
import java.net.URL;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspacePane extends HBox{
    private static enum MESSAGE_TYPE {
        MESSAGE_NORMAL,
        MESSAGE_SUCCESS,
        MESSAGE_ERROR
    }
    private CodeCheckWorkspaceViewController controller;
    private int paneIndex;
    private VBox leftPaneSpace, rightPaneSpace;
    private ListView filesView;
    private Button testButton, removeButton, refreshButton,viewButton;
    private TextFlow actionLog;
    private Label stepTitleLabel, hintLabel, progressLabel;
    private Button stepActionButtons[];
    private HBox leftActionButtonsPane, stepActionButtonsPane;
    
    public CodeCheckWorkspacePane(CodeCheckWorkspaceViewController initController) {
        controller = initController;
        initLayout();
        initControllers();
        initStyle();
    }
    private void initLayout() {
        leftPaneSpace = new VBox();
        testButton = new Button("test");
        removeButton = new Button("remove");
        leftActionButtonsPane = new HBox(testButton,removeButton);
        leftPaneSpace.getChildren().add(leftActionButtonsPane);

        rightPaneSpace = new VBox();

        ScrollPane logScrollArea = new ScrollPane();
        actionLog = new TextFlow(); 

        actionLog.setTextAlignment(TextAlignment.LEFT);             
        actionLog.setPrefSize(300, 100);        
        actionLog.setLineSpacing(5.0); 

        logScrollArea.setContent(actionLog);    
        rightPaneSpace.getChildren().add(logScrollArea);
        logScrollArea.setFitToWidth(true);
        logScrollArea.setFitToHeight(true);
        getChildren().addAll(leftPaneSpace,rightPaneSpace);   


    }
    private void initControllers() {
        testButton.setOnAction(e-> {
            printMessageToLog("Test Message",MESSAGE_TYPE.MESSAGE_NORMAL);
            printMessageToLog("ERROR Message",MESSAGE_TYPE.MESSAGE_ERROR);
            printMessageToLog("SUCCESS Message",MESSAGE_TYPE.MESSAGE_SUCCESS);
        });
    }
    private void initStyle() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();
        getStyleClass().add(WORKSPACE_PANE);
        
}
    public void setStepNumber(int step){
        paneIndex = step;
        testButton.setText("Step " + paneIndex);
    }
    public void printMessageToLog(String message, MESSAGE_TYPE type) {
        Text logText = new Text(message + "\n"); 
        logText.setFont(new Font(15)); 
        
        switch(type){
            case MESSAGE_NORMAL:
                logText.setFill(Color.BLACK); 
                break;
            case MESSAGE_SUCCESS:
                logText.setFill(Color.GREEN); 
                break;
            case MESSAGE_ERROR:
                logText.setFill(Color.RED); 
                break;

        }
        actionLog.getChildren().add(logText);

    }
}
