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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspacePane extends BorderPane{
    private CodeCheckWorkspaceViewController controller;
    private Button testButton;
    private int paneIndex;
    
    public CodeCheckWorkspacePane(CodeCheckWorkspaceViewController initController) {
        controller = initController;
        initLayout();
    }
    private void initLayout() {
        testButton = new Button("test");
        setCenter(testButton);
        //this.getChildren().add(testButton);

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
}
