/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.APP_PATH_WORK;
import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspaceViewController {
    static enum MESSAGE_TYPE {
        MESSAGE_NORMAL,
        MESSAGE_SUCCESS,
        MESSAGE_ERROR
    }
    
    private CodeCheckApp app;
    private CodeCheckWorkspaceView workspace;
    
    public CodeCheckWorkspaceViewController(CodeCheckApp initApp,CodeCheckWorkspaceView view) {
        app = initApp;
        workspace = view;
    }
    public void handleLoadRequest() {
        FileChooser fileChooser = new FileChooser();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        fileChooser.setInitialDirectory(new File(props.getProperty(APP_PATH_WORK)));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), "*." + props.getProperty(WORK_FILE_EXT)));
        File selectedFile = fileChooser.showOpenDialog(app.getGUI().getWindow());
        if (selectedFile != null) {
            //try {
                app.getDataComponent().resetData();
                //TEMP SET DATA LIKE THIS, FILE COMP WILL DO THIS LATER
                ((CodeCheckProjectData)app.getDataComponent()).setFile(selectedFile);
                workspace.resetWorkspace();
                workspace.reloadWorkspace(app.getDataComponent());
                workspace.activateWorkspace(app.getGUI().getAppPane());
            /*    app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());
                
            } catch (IOException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }*/

        }

    }
    public void handleNewCheckRequest() {
        //SAVE THE CURRENT STATE OF THIS CHECK THEN CREATE NEW
       ((CodeCheckFileStore)app.getFileComponent()).handleNewRequest();

    }
    public void handlePrevStepRequest(){
        int index = Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace());

        workspace.changeToWorkspace(index-1);
    }
    public void handleNextStepRequest(){
        int index = Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace());

        workspace.changeToWorkspace(index+1);
        
    }
    public void handleHomeStepRequest() {
        workspace.changeToWorkspace(0);

    }
    public void handleRemoveRequest() {
        
    }
    public void handleRefreshRequest() {
        
    }
    public void handleViewRequest() {
        
    }
    public void handleStepActionRequest(String action) {
        
    }
    public void handleAboutButtonPressed() {
        
    }
    public void handleRenameProjectRequest() {
        
    }
    public void renameProject(String name) {
        
    }
    public void updateProgressBar() {
        
    }
    private void extractSubmissions() {
        
    }
    private void renameSubmissions() {
        
    }
    private void unzipSubmissions() {
        
    }
    private void extractSubmissionCode() {
        
    }
    private URL codeCheckWithResults() {
        return null;
    }
    private void launchViewerWithURL(URL url) {
        
    }
    public void printMessageToLog(String message,MESSAGE_TYPE type) {
        /*
        TextField text = new TextField(message);
        text.setEditable(false);
        text.setBackground(Background.EMPTY);
        text.setFocusTraversable(false);
        */
        CodeCheckWorkspacePane activePane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        Text logText = new Text(message + "\n"); 
        //logText.setFont(new Font(15)); 
        
        switch(type){
            case MESSAGE_NORMAL:
                logText.setFill(Color.BLACK); 
                //text.setStyle("-fx-text-inner-color: black;");
                break;
            case MESSAGE_SUCCESS:
                logText.setFill(Color.GREEN); 
                //text.setStyle("-fx-text-inner-color: green;");
                break;
            case MESSAGE_ERROR:
                logText.setFill(Color.RED); 
                //text.setStyle("-fx-text-inner-color: red;");
                break;

        }
        activePane.actionLog.getChildren().addAll(logText);
    }

}
