/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import static cc.CodeCheckProp.*;
import static cc.style.CodeCheckStyle.LIST_VIEW;
import static cc.style.CodeCheckStyle.STEP_LIST_TITLE_LABEL;
import static cc.style.CodeCheckStyle.STEP_TITLE_LABEL;
import static cc.style.CodeCheckStyle.WORKSPACE_PANE;
import static cc.style.CodeCheckStyle.WORKSPACE_TOOLBAR_INNER;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
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
    private ScrollPane logScrollArea;
    HBox leftActionButtonsPane, stepActionButtonsPane;
    ListView filesView;
    Button removeButton, refreshButton,viewButton;
    TextFlow actionLog;
    Label stepTitleLabel, hintLabel, progressLabel, progressPerc, stepListTitle;
    ProgressBar stepProgress;
    
    public CodeCheckWorkspacePane(CodeCheckWorkspaceViewController initController) {
        controller = initController;
        initLayout();
        initControllers();
        initControlBinding();
        initStyle();
    }
    private void initLayout() {
        
        //LEFT SIDE OF WORKSPACE
        leftPaneSpace = new VBox();
        leftPaneSpace.setSpacing(10);
        leftPaneSpace.setPadding(new Insets(10, 10, 10, 10));
        stepTitleLabel = new Label();
        hintLabel = new Label();
        hintLabel.setWrapText(true);
        hintLabel.setPadding(new Insets(5, 5, 8, 5));
        VBox.setVgrow(hintLabel, Priority.ALWAYS);
        
        filesView = new ListView();
        filesView.setOrientation(Orientation.VERTICAL);
        filesView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        VBox.setVgrow(filesView, Priority.ALWAYS);

        stepListTitle = new Label();
        HBox.setHgrow(stepListTitle, Priority.ALWAYS);
        //stepListTitle.prefWidthProperty().bind(filesView.widthProperty());
        VBox listContainer = new VBox(stepListTitle,filesView);
        listContainer.getStyleClass().add(LIST_VIEW);

        //removeButton = new Button("remove");
        //refreshButton = new Button("refresh");
        //viewButton = new Button("view");
        leftActionButtonsPane = new HBox();//removeButton,refreshButton,viewButton);
        removeButton = controller.initChildButton(leftActionButtonsPane, REMOVE_BUTTON_ICON.toString(),REMOVE_BUTTON_TOOLTIP.toString(), true);
        refreshButton = controller.initChildButton(leftActionButtonsPane, REFRESH_BUTTON_ICON.toString(),REFRESH_BUTTON_TOOLTIP.toString(), false);
        viewButton = controller.initChildButton(leftActionButtonsPane, VIEW_BUTTON_ICON.toString(),VIEW_BUTTON_TOOLTIP.toString(), true);
        
        leftPaneSpace.getChildren().addAll(stepTitleLabel,hintLabel,listContainer,leftActionButtonsPane);

        //RIGHT SIDE OF WORKSPACE
        rightPaneSpace = new VBox();
        rightPaneSpace.setFillWidth(true);
        rightPaneSpace.setSpacing(10);
        rightPaneSpace.setPadding(new Insets(10, 10, 10, 10));

        progressLabel = new Label();
        progressPerc = new Label();
        stepProgress = new ProgressBar();
        stepProgress.setProgress(0);
        HBox progressBox = new HBox(progressLabel,stepProgress,progressPerc);
        progressBox.setSpacing(15);
        stepActionButtonsPane =  new HBox();
        logScrollArea = new ScrollPane();
        actionLog = new TextFlow();
        actionLog.setTextAlignment(TextAlignment.LEFT);
        actionLog.setPrefSize(300, 100);
        actionLog.setLineSpacing(5.0); 
        actionLog.setPadding(new Insets(10, 10, 10, 10));

        logScrollArea.setContent(actionLog);
        logScrollArea.setFitToWidth(true);
        logScrollArea.setFitToHeight(true);
        VBox.setVgrow(logScrollArea, Priority.ALWAYS);
        
        rightPaneSpace.getChildren().addAll(progressBox,stepActionButtonsPane,logScrollArea);

        HBox.setHgrow(leftPaneSpace, Priority.SOMETIMES);
        HBox.setHgrow(rightPaneSpace, Priority.ALWAYS);
        getChildren().addAll(leftPaneSpace,rightPaneSpace);   

        int paneCount = getChildren().size();
        leftPaneSpace.prefWidthProperty().bind(widthProperty().divide(paneCount));
        rightPaneSpace.prefWidthProperty().bind(widthProperty().divide(paneCount));


    }
    private void initControllers() {
        refreshButton.setOnAction(e -> {
            controller.handleRefreshRequest(true);
        });
        removeButton.setOnAction(e->{
            controller.handleRemoveRequest();
        });
        viewButton.setOnAction(e->{
            controller.handleViewRequest();
        });

    }
    private void initControlBinding() {
        BooleanBinding disableButton = Bindings.size(filesView.getSelectionModel().getSelectedItems()).isNotEqualTo(1);
        
        //refreshButton.disableProperty().bind(filesView.itemsProperty().isNull());
        viewButton.disableProperty().bind(disableButton);
        removeButton.disableProperty().bind(disableButton);
        //nextButton.disableProperty().bind(Bindings.size(oList).lessThan(oList.indexOf(getWorkspace())));
        
    }
    private void initStyle() {
        getStyleClass().add(WORKSPACE_PANE);
        stepTitleLabel.getStyleClass().add(STEP_TITLE_LABEL);
        progressLabel.getStyleClass().add(STEP_TITLE_LABEL);
        setPadding(new Insets(10, 10, 10, 10));
        leftActionButtonsPane.getStyleClass().add(WORKSPACE_TOOLBAR_INNER);
        stepActionButtonsPane.getStyleClass().add(WORKSPACE_TOOLBAR_INNER);
        stepListTitle.getStyleClass().add(STEP_LIST_TITLE_LABEL);

}

    public void setStepTitle(String title) {
        stepTitleLabel.setText(title);
    }
    public void setStepHint(String hint) {
        hintLabel.setText(hint);
    }
    public void setStepListTitle(String title) {
        stepListTitle.setText(title);
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
    public Node getExtras(int side, int index){
        int indexOffset;
        if(side == 0){
            indexOffset = leftPaneSpace.getChildren().indexOf(leftActionButtonsPane);
            return leftPaneSpace.getChildren().get(indexOffset+index+1);
        }else if(side == 1){
            indexOffset = leftPaneSpace.getChildren().indexOf(filesView);
            return leftPaneSpace.getChildren().get(indexOffset+index);            
        }
        return null;
    }
    public void scrollToBottom() {
            logScrollArea.layout();
            logScrollArea.setVvalue(1.0f);
    }
}
