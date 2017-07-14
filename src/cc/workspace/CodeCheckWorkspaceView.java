/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.*;
import cc.data.CodeCheckProjectData;
import static cc.style.CodeCheckStyle.CUSTOM_SOURCE_FIELD;
import static cc.style.CodeCheckStyle.STEP_TITLE_LABEL;
import static cc.style.CodeCheckStyle.WORKSPACE_TOOLBAR;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import java.nio.file.Path;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWorkspaceView extends AppWorkspaceComponent{
    public static boolean activateOnLoad = false;
    final private CodeCheckApp app;
    final private CodeCheckWorkspaceViewController controller;
    protected HBox progressionToolbar;
    protected Button prevButton, nextButton, homeButton;
    protected Button renameButton, aboutButton;
    CodeCheckWorkspacePane stepPanes[];
    
    public CodeCheckWorkspaceView(CodeCheckApp initApp) {
        app = initApp;
        controller = new CodeCheckWorkspaceViewController(app,this);
        
        initLayout();
        initControllers();
        initControlBinding();
        initStyle();
    }
    private void initLayout() {

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        FlowPane fileToolbar = app.getGUI().getFileToolbar();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox newTopToolbar = new HBox(fileToolbar,spacer);
        app.getGUI().getAppPane().setTop(newTopToolbar);

        progressionToolbar = new HBox();
        progressionToolbar.setPadding(new Insets(5, 5, 5, 5));
        progressionToolbar.setAlignment(Pos.BASELINE_RIGHT);

        //HERE'S A NASTY WAY TO RIGHT ALIGN A TOOLBAR IN FLOWPANE        
        //app.getGUI().getTopToolbarPane().getChildren().add(spacer);
        //spacer.prefWidthProperty().bind(app.getGUI().getTopToolbarPane().widthProperty().subtract(fileToolbar.widthProperty()).subtract(progressionToolbar.widthProperty()).subtract(20));
        
        homeButton = app.getGUI().initChildButton(progressionToolbar, HOME_BUTTON_ICON.toString(),HOME_BUTTON_TEXT.toString(), true);
        prevButton = app.getGUI().initChildButton(progressionToolbar, PREV_BUTTON_ICON.toString(),PREV_BUTTON_TEXT.toString(), true);
        nextButton = app.getGUI().initChildButton(progressionToolbar, NEXT_BUTTON_ICON.toString(),NEXT_BUTTON_TEXT.toString(), false);

        //CUSTOMIZE THE FILE TOOLBAR AS NEEDED
        setupToolbarAsNeeded(fileToolbar);
        renameButton = app.getGUI().initChildButton(fileToolbar, RENAME_BUTTON_ICON.toString(),RENAME_BUTTON_TEXT.toString(), false);
        aboutButton = app.getGUI().initChildButton(fileToolbar, ABOUT_BUTTON_ICON.toString(),ABOUT_BUTTON_TEXT.toString(), false);
        
        stepPanes = new CodeCheckWorkspacePane[5];
        workspace = stepPanes[0];
        for(int i = 0; i < stepPanes.length; i++) {
            stepPanes[i] = new CodeCheckWorkspacePane(controller);
            
            //STEP SPECIFIC LABELS
            stepPanes[i].setStepTitle(props.getProperty("STEP_"+ (i+1) +"_TITLE"));
            stepPanes[i].setStepHint(props.getProperty("STEP_"+ (i+1) +"_HINT"));
            stepPanes[i].setStepProgressLabel(props.getProperty("STEP_"+ (i+1) +"_PROGRESS"));
            
            //CONNECT TABLES TO THEIR DATA HERE
            stepPanes[i].filesView.setItems(((CodeCheckProjectData)app.getDataComponent()).getListing(i));
            stepPanes[i].filesView.setCellFactory((e->{

                  ListCell<Path> cell = new ListCell<Path>() {
                    @Override
                    public void updateItem(Path item, boolean empty) {
                      super.updateItem(item, empty);
                      setText((empty || item == null) ? null : item.getFileName().toString());
                    }
                  };
                  return cell;
            }));
            
            //ADD THE STEP SPECIFIC BUTTONS
            Button actionButtonOne = app.getGUI().initChildButton(stepPanes[i].stepActionButtonsPane, "STEP_"+(i+1)+"_BUTTON_1_TEXT","STEP_"+(i+1)+"_BUTTON_1_TEXT", false);
            actionButtonOne.setText(props.getProperty("STEP_"+(i+1)+"_BUTTON_1_TEXT"));
            if(i == 4) {
                //STEP 5 HAS 2 BUTTONS
                Button actionButtonTwo = app.getGUI().initChildButton(stepPanes[i].stepActionButtonsPane, "STEP_"+(i+1)+"_BUTTON_2_TEXT","STEP_"+(i+1)+"_BUTTON_2_TEXT", true);
                actionButtonTwo.setText(props.getProperty("STEP_"+(i+1)+"_BUTTON_2_TEXT"));
            }
            if(i == 3){
                //STEP 3 HAS AN EXTRA OPTIONS AREA
                
                //HERE'S THE LABEL
                Label fileTypeLabel = new Label(props.getProperty(FILE_TYPE_LABEL));
                fileTypeLabel.getStyleClass().add(STEP_TITLE_LABEL);
                stepPanes[i].addExtraContent(0,fileTypeLabel);
                
                //AND HERE'S THE CONTENT
                GridPane checkBoxPane = new GridPane();
                checkBoxPane.setHgap(50);
                checkBoxPane.setVgap(10);
                checkBoxPane.setPadding(new Insets(5, 5, 5, 5));                
                CheckBox[] sourceTypes = new CheckBox[5];
                
                for (int type = 0; type < sourceTypes.length-1; type++)               
                    sourceTypes[type] = new CheckBox(props.getProperty("FILE_TYPE_"+ (type+1)));
                
                //REGULAR OPTIONS
                checkBoxPane.add(sourceTypes[0], 0, 0);
                checkBoxPane.add(sourceTypes[1], 0, 1);
                checkBoxPane.add(sourceTypes[2], 1, 0);
                checkBoxPane.add(sourceTypes[3], 1, 1);
                
                //CUSTOM SOURCE OPTION
                TextField customSourceField = new TextField();
                customSourceField.setPromptText(props.getProperty(FILE_TYPE_CUSTOM));
                customSourceField.getStyleClass().add(CUSTOM_SOURCE_FIELD);
                sourceTypes[4] = new CheckBox();
                HBox customCheckbox = new HBox(sourceTypes[4],customSourceField);
                customCheckbox.setFillHeight(true);
                customCheckbox.setAlignment(Pos.CENTER_LEFT);
                GridPane.setColumnSpan(customCheckbox, 2);

                checkBoxPane.add(customCheckbox, 0, 2);
                
                stepPanes[i].addExtraContent(0,checkBoxPane);               
            }
        }

        //USE THIS FOR BUTTONS WITHOUT ICONS
        //homeButton.setText(props.getProperty(HOME_BUTTON_TEXT.toString()));
        //prevButton.setText(props.getProperty(PREV_BUTTON_TEXT.toString()));
        //nextButton.setText(props.getProperty(NEXT_BUTTON_TEXT.toString()));
        //renameButton.setText(props.getProperty(RENAME_BUTTON_TEXT.toString()));
        //aboutButton.setText(props.getProperty(ABOUT_BUTTON_TEXT.toString()));

        //app.getGUI().getTopToolbarPane().getChildren().add(progressionToolbar);
        newTopToolbar.getChildren().add(progressionToolbar);
        setWorkspace(stepPanes[0]);
        if(activateOnLoad)
            this.activateWorkspace(app.getGUI().getAppPane());


    }
    private void initControllers() {
        
        homeButton.setOnAction(e-> {
            controller.handleHomeStepRequest();
        });
        
        prevButton.setOnAction(e-> {
            controller.handlePrevStepRequest();
        });
        
        nextButton.setOnAction(e-> {
            controller.handleNextStepRequest();
        });
        aboutButton.setOnAction(e -> {
            controller.displayAboutDialog();
        });
        renameButton.setOnAction(e -> {
            controller.handleRenameProjectRequest();
        });
        for(int i = 0; i < stepPanes.length; i++) {
            Button actionButton = (Button)stepPanes[i].stepActionButtonsPane.getChildren().get(0);
            final int index = i;
            actionButton.setOnAction(e -> {
                controller.handleStepActionRequest(index);
            });
        }
        
    }
    private void initControlBinding() {
        //ObservableList oList = FXCollections.observableArrayList(stepPanes);
        
        progressionToolbar.disableProperty().bind(app.getGUI().getAppPane().centerProperty().isNull());
        renameButton.disableProperty().bind(app.getGUI().getAppPane().centerProperty().isNull());
        
        //homeButton.disableProperty().bind(Bindings.size(oList).multiply(0).isEqualTo(oList.indexOf(getWorkspace())));
        //prevButton.disableProperty().bind(Bindings.size(oList).multiply(0).isEqualTo(oList.indexOf(getWorkspace())));
        //nextButton.disableProperty().bind(Bindings.size(oList).lessThan(oList.indexOf(getWorkspace())));


    }
    private void initStyle() {
        //app.getGUI().getTopToolbarPane().getStyleClass().add(WORKSPACE_TOOLBAR);
        app.getGUI().getAppPane().getTop().getStyleClass().add(WORKSPACE_TOOLBAR);
        progressionToolbar.getStyleClass().add(WORKSPACE_TOOLBAR);

    }
    private void setupToolbarAsNeeded(FlowPane toolbar) {
        //WE NEED CUSTOM NEW/LOAD ACTIONS
        Button newButton = (Button)toolbar.getChildren().get(0);
        Button loadButton = (Button)toolbar.getChildren().get(1);
        newButton.setOnAction(e -> {
            controller.handleNewCheckRequest();
        });
        loadButton.setOnAction(e -> {
            controller.handleLoadRequest();
        });
        toolbar.getChildren().remove(2, 7);
    }
    void changeToWorkspace(int index) {
        setWorkspace(stepPanes[index]);
        app.getGUI().getAppPane().setCenter(workspace);
        if(index >= stepPanes.length-1){
            nextButton.setDisable(true);
        }else if(index <= 0) {
            homeButton.setDisable(true);
            prevButton.setDisable(true);
        }else if(index > 0){
            homeButton.setDisable(false);
            prevButton.setDisable(false);
        }
        if(index < stepPanes.length-1){
            nextButton.setDisable(false);
        }
        if(((CodeCheckWorkspacePane)workspace).filesView.getItems() == null){
            controller.handleRefreshRequest();
        }

        
    }

    @Override
    public void resetWorkspace() {
        setWorkspace(stepPanes[0]);
        app.getGUI().getAppPane().setCenter(workspace);
        for(CodeCheckWorkspacePane step : stepPanes){
            step.actionLog.getChildren().clear();
            if(step.filesView.getItems() != null )
                step.filesView.getItems().clear();
        }
    }

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        app.updateStageTitle();
        for(int i = 0; i < stepPanes.length; i++){
            ((CodeCheckProjectData)dataComponent).refreshList(i);
            stepPanes[i].filesView.setItems(((CodeCheckProjectData)dataComponent).getListing(i));
        }
        //fill in data from new data comp
    }
    
}
