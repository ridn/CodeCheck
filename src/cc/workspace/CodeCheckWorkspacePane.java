/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.style.CodeCheckStyle.STEP_TITLE_LABEL;
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
import javafx.scene.layout.Priority;
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
    
    private CodeCheckWorkspaceViewController controller;
    private int paneIndex;
    private VBox leftPaneSpace, rightPaneSpace;
    private HBox leftActionButtonsPane, stepActionButtonsPane;
    ListView filesView;
    Button testButton, removeButton, refreshButton,viewButton;
    TextFlow actionLog;
    Label stepTitleLabel, hintLabel, progressLabel;
    Button stepActionButtons[];
    
    public CodeCheckWorkspacePane(CodeCheckWorkspaceViewController initController) {
        controller = initController;
        initLayout();
        initControllers();
        initStyle();
    }
    private void initLayout() {
        
        //LEFT SIDE OF WORKSPACE
        leftPaneSpace = new VBox();
        stepTitleLabel = new Label("Step Title: " + paneIndex);
        hintLabel = new Label("Step " + paneIndex+ " hint");
        testButton = new Button("test");
        removeButton = new Button("remove");
        refreshButton = new Button("refresh");
        viewButton = new Button("view");
        leftActionButtonsPane = new HBox(testButton,removeButton,refreshButton,viewButton);
        
        leftPaneSpace.getChildren().addAll(stepTitleLabel,hintLabel,leftActionButtonsPane);

        //RIGHT SIDE OF WORKSPACE
        rightPaneSpace = new VBox();
        rightPaneSpace.setFillWidth(true);

        progressLabel = new Label("Step " + paneIndex+ " progress");
        ScrollPane logScrollArea = new ScrollPane();
        actionLog = new TextFlow();
        actionLog.setTextAlignment(TextAlignment.LEFT);
        actionLog.setPrefSize(300, 100);
        actionLog.setLineSpacing(5.0); 

        logScrollArea.setContent(actionLog);
        logScrollArea.setFitToWidth(true);
        logScrollArea.setFitToHeight(true);
        rightPaneSpace.getChildren().addAll(progressLabel,logScrollArea);

        HBox.setHgrow(leftPaneSpace, Priority.ALWAYS);
        HBox.setHgrow(rightPaneSpace, Priority.ALWAYS);
        getChildren().addAll(leftPaneSpace,rightPaneSpace);   


    }
    private void initControllers() {
        testButton.setOnAction(e-> {
            controller.printMessageToLog("Test Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_NORMAL);
            controller.printMessageToLog("ERROR Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_ERROR);
            controller.printMessageToLog("SUCCESS Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_SUCCESS);
        });
    }
    private void initStyle() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();
        getStyleClass().add(WORKSPACE_PANE);
        stepTitleLabel.getStyleClass().add(STEP_TITLE_LABEL);
        progressLabel.getStyleClass().add(STEP_TITLE_LABEL);
        
}
    public void setStepNumber(int step){
        paneIndex = step;
        testButton.setText("Step " + paneIndex);
    }
}
