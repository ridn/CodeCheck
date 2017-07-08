/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.*;
import static cc.style.CodeCheckStyle.WORKSPACE_TOOLBAR;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppPropertyType.APP_CSS;
import static djf.settings.AppPropertyType.APP_PATH_CSS;
import java.net.URL;
import java.util.Arrays;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWorkspaceView extends AppWorkspaceComponent{
    private CodeCheckApp app;
    private CodeCheckWorkspaceViewController controller;
    protected CodeCheckWorkspacePane stepPanes[];
    protected HBox progressionToolbar;
    protected Button prevButton, nextButton, homeButton;
    protected Button renameButton, aboutButton;
    
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

        progressionToolbar = new HBox();
        progressionToolbar.setPadding(new Insets(10, 10, 10, 10));
        
        homeButton = app.getGUI().initChildButton(progressionToolbar, HOME_BUTTON_ICON.toString(),HOME_BUTTON_TEXT.toString(), false);
        prevButton = app.getGUI().initChildButton(progressionToolbar, PREV_BUTTON_ICON.toString(),PREV_BUTTON_TEXT.toString(), false);
        nextButton = app.getGUI().initChildButton(progressionToolbar, NEXT_BUTTON_ICON.toString(),NEXT_BUTTON_TEXT.toString(), false);

        FlowPane fileToolbar =  (FlowPane)app.getGUI().getTopToolbarPane().getChildren().get(0);
        setupToolbarAsNeeded(fileToolbar);
        renameButton = app.getGUI().initChildButton(fileToolbar, RENAME_BUTTON_TEXT.toString(),RENAME_BUTTON_TEXT.toString(), false);
        aboutButton = app.getGUI().initChildButton(fileToolbar, ABOUT_BUTTON_TEXT.toString(),ABOUT_BUTTON_TEXT.toString(), false);
        
        stepPanes = new CodeCheckWorkspacePane[5];
        workspace = stepPanes[0];
        for(int i = 0; i < 5; i++) {
            stepPanes[i] = new CodeCheckWorkspacePane(controller);
            stepPanes[i].setStepNumber(i+1);
            stepPanes[i].setStepTitle(props.getProperty("STEP_"+ (i+1) +"_TITLE"));
            stepPanes[i].setStepHint(props.getProperty("STEP_"+ (i+1) +"_HINT"));
            stepPanes[i].setStepProgressLabel(props.getProperty("STEP_"+ (i+1) +"_PROGRESS"));
        }

        //USE THIS FOR BUTTONS WITHOUT ICONS
        homeButton.setText(props.getProperty(HOME_BUTTON_TEXT.toString()));
        prevButton.setText(props.getProperty(PREV_BUTTON_TEXT.toString()));
        nextButton.setText(props.getProperty(NEXT_BUTTON_TEXT.toString()));
        renameButton.setText(props.getProperty(RENAME_BUTTON_TEXT.toString()));
        aboutButton.setText(props.getProperty(ABOUT_BUTTON_TEXT.toString()));

        app.getGUI().getTopToolbarPane().getChildren().add(progressionToolbar);
        
        setWorkspace(stepPanes[0]);
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
        
    }
    private void initControlBinding() {
        
    }
    private void initStyle() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();
        app.getGUI().getTopToolbarPane().getStyleClass().add(WORKSPACE_TOOLBAR);

    }
    private void setupToolbarAsNeeded(FlowPane toolbar) {
        toolbar.getChildren().remove(2, 7);
    }
    void changeToWorkspace(int index) {
        //System.out.println(Arrays.asList(stepPanes).toString());
        //System.out.println("Switched to step "+ (index+1));       
        setWorkspace(stepPanes[index]);
        //System.out.println(workspaceActivated);
        app.getGUI().getAppPane().setCenter(workspace);
        
    }

    @Override
    public void resetWorkspace() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
