/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import djf.components.AppDataComponent;
import djf.components.AppWorkspaceComponent;
import static djf.settings.AppPropertyType.VIEW_SHOW_ICON;
import static djf.settings.AppPropertyType.VIEW_SHOW_TOOLTIP;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWorkspaceView extends AppWorkspaceComponent{
    private CodeCheckApp app;
    private CodeCheckWorkspaceViewController controller;
    protected CodeCheckWorkspacePane currentWorkspacePane;
    protected CodeCheckWorkspacePane stepPanes[];
    protected HBox progressionToolbar;
    protected Button prevButton, nextButton, homeButton;
    protected Button renameButton, aboutButton;
    
    public CodeCheckWorkspaceView(CodeCheckApp initApp) {
        app = initApp;
        
        initLayout();
        initControllers();
        initControlBinding();
        initStyle();
    }
    private void initLayout() {
        
        progressionToolbar = new HBox();
        
        prevButton = app.getGUI().initChildButton(progressionToolbar, VIEW_SHOW_ICON.toString(),VIEW_SHOW_TOOLTIP.toString(), false);
        nextButton = app.getGUI().initChildButton(progressionToolbar, VIEW_SHOW_ICON.toString(),VIEW_SHOW_TOOLTIP.toString(), false);
        homeButton = app.getGUI().initChildButton(progressionToolbar, VIEW_SHOW_ICON.toString(),VIEW_SHOW_TOOLTIP.toString(), false);

        FlowPane fileToolbar =  (FlowPane)app.getGUI().getTopToolbarPane().getChildren().get(0);
        renameButton = app.getGUI().initChildButton(fileToolbar, VIEW_SHOW_ICON.toString(),VIEW_SHOW_TOOLTIP.toString(), false);
        aboutButton = app.getGUI().initChildButton(fileToolbar, VIEW_SHOW_ICON.toString(),VIEW_SHOW_TOOLTIP.toString(), false);

        app.getGUI().getTopToolbarPane().getChildren().add(progressionToolbar);

    }
    private void initControllers() {
        controller = new CodeCheckWorkspaceViewController();
        
    }
    private void initControlBinding() {
        
    }
    private void initStyle() {
        
    }
    void changeToWorkspace(int index) {
        
    }

    @Override
    public void resetWorkspace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadWorkspace(AppDataComponent dataComponent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
