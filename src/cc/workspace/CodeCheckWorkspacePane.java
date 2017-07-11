/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import static cc.CodeCheckProp.*;
import static cc.style.CodeCheckStyle.STEP_TITLE_LABEL;
import static cc.style.CodeCheckStyle.WORKSPACE_PANE;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspacePane extends HBox{
    
    private CodeCheckWorkspaceViewController controller;
    private int paneIndex;
    private VBox leftPaneSpace, rightPaneSpace;
    HBox leftActionButtonsPane, stepActionButtonsPane;
    ListView filesView;
    Button testButton, removeButton, refreshButton,viewButton;
    TextFlow actionLog;
    Label stepTitleLabel, hintLabel, progressLabel;
    
    public CodeCheckWorkspacePane(CodeCheckWorkspaceViewController initController) {
        controller = initController;
        initLayout();
        initControllers();
        initStyle();
    }
    private void initLayout() {
        
        //LEFT SIDE OF WORKSPACE
        leftPaneSpace = new VBox();
        leftPaneSpace.setSpacing(10);
        leftPaneSpace.setPadding(new Insets(10, 10, 10, 10));
        stepTitleLabel = new Label("Step Title: " + paneIndex);
        hintLabel = new Label("");
        hintLabel.setWrapText(true);
        hintLabel.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(hintLabel, Priority.ALWAYS);
        
        filesView = new ListView();
        filesView.setOrientation(Orientation.VERTICAL);
        VBox.setVgrow(filesView, Priority.ALWAYS);

        testButton = new Button("test");

        //removeButton = new Button("remove");
        //refreshButton = new Button("refresh");
        //viewButton = new Button("view");
        leftActionButtonsPane = new HBox(testButton);//,removeButton,refreshButton,viewButton);
        removeButton = controller.initChildButton(leftActionButtonsPane, REMOVE_BUTTON_ICON.toString(),REMOVE_BUTTON_TOOLTIP.toString(), true);
        refreshButton = controller.initChildButton(leftActionButtonsPane, REFRESH_BUTTON_ICON.toString(),REFRESH_BUTTON_TOOLTIP.toString(), true);
        viewButton = controller.initChildButton(leftActionButtonsPane, VIEW_BUTTON_ICON.toString(),VIEW_BUTTON_TOOLTIP.toString(), true);
        
        leftPaneSpace.getChildren().addAll(stepTitleLabel,hintLabel,filesView,leftActionButtonsPane);

        //RIGHT SIDE OF WORKSPACE
        rightPaneSpace = new VBox();
        rightPaneSpace.setFillWidth(true);
        rightPaneSpace.setSpacing(10);
        rightPaneSpace.setPadding(new Insets(10, 10, 10, 10));

        progressLabel = new Label("Step " + paneIndex+ " progress");
        stepActionButtonsPane =  new HBox();
        ScrollPane logScrollArea = new ScrollPane();
        actionLog = new TextFlow();
        actionLog.setTextAlignment(TextAlignment.LEFT);
        actionLog.setPrefSize(300, 100);
        actionLog.setLineSpacing(5.0); 
        actionLog.setPadding(new Insets(10, 10, 10, 10));

        logScrollArea.setContent(actionLog);
        logScrollArea.setFitToWidth(true);
        logScrollArea.setFitToHeight(true);
        VBox.setVgrow(logScrollArea, Priority.ALWAYS);
        
        rightPaneSpace.getChildren().addAll(progressLabel,stepActionButtonsPane,logScrollArea);

        HBox.setHgrow(leftPaneSpace, Priority.ALWAYS);
        HBox.setHgrow(rightPaneSpace, Priority.ALWAYS);
        getChildren().addAll(leftPaneSpace,rightPaneSpace);   

        int btnCount = getChildren().size();
        leftPaneSpace.prefWidthProperty().bind(widthProperty().divide(btnCount));
        rightPaneSpace.prefWidthProperty().bind(widthProperty().divide(btnCount));


    }
    private void initControllers() {
        testButton.setOnAction(e-> {
            controller.printMessageToLog("Test Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_NORMAL);
            controller.printMessageToLog("ERROR Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_ERROR);
            controller.printMessageToLog("SUCCESS Message",CodeCheckWorkspaceViewController.MESSAGE_TYPE.MESSAGE_SUCCESS);
        });
    }
    private void initStyle() {
        getStyleClass().add(WORKSPACE_PANE);
        stepTitleLabel.getStyleClass().add(STEP_TITLE_LABEL);
        progressLabel.getStyleClass().add(STEP_TITLE_LABEL);
        setPadding(new Insets(10, 10, 10, 10));
}
    public void setStepNumber(int step){
        paneIndex = step;
        testButton.setText("Step " + paneIndex);
    }
    public void setStepTitle(String title) {
        stepTitleLabel.setText(title);
    }
    public void setStepHint(String hint) {
        hintLabel.setText(hint);
    }
    public void setStepProgressLabel(String progress) {
        progressLabel.setText(progress);
    }
    public void addExtraContent(int side,Node node) {
        //WE DONT NEED BOTH BUT WHY NOT
        if(side == 0) {
            //LEFT SIDE
            leftPaneSpace.getChildren().add(node);
        }else if (side == 1){
            //RIGHT SIDE
            rightPaneSpace.getChildren().add(node);
        }
    }
}
