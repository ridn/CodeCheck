/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.ABOUT_LABEL_TEXT;
import static cc.CodeCheckProp.APP_PATH_WORK;
import static cc.CodeCheckProp.APP_VERSION;
import static cc.CodeCheckProp.AUTHOR_TEXT;
import static cc.CodeCheckProp.LEGAL_NOTICE;
import static cc.CodeCheckProp.VERSION_TEXT;
import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
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
            ((CodeCheckFileStore)app.getFileComponent()).loadProject(selectedFile);       
        }


    }
    public void handleNewCheckRequest() {
        //SAVE THE CURRENT STATE OF THIS CHECK THEN CREATE NEW
       CodeCheckFileStore filestore = ((CodeCheckFileStore)app.getFileComponent());
       filestore.handleNewRequest();
       if(filestore.activeCheckFileChanged() && filestore.getActiveCheckFile() != null) {
            //try {
                app.getDataComponent().resetData();
                //TEMP SET DATA LIKE THIS, FILE COMP WILL DO THIS LATER
                ((CodeCheckProjectData)app.getDataComponent()).setFile(filestore.getActiveCheckFile());
                workspace.resetWorkspace();
                workspace.reloadWorkspace(app.getDataComponent());
                workspace.activateWorkspace(app.getGUI().getAppPane());
                filestore.addProjectToRecents((CodeCheckProjectData)app.getDataComponent());
            /*    app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());

            } catch (IOException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }*/
       }


    }
    public void handleRenameProjectRequest() {
       CodeCheckFileStore filestore = ((CodeCheckFileStore)app.getFileComponent());
       filestore.handleProjectRenameRequest();
       if(filestore.activeCheckFileChanged() && filestore.getActiveCheckFile() != null) {
           //UPDATE RECENT PROJECT
           //System.out.println(filestore.getActiveCheckFile().getPath());
           CodeCheckProjectData data = ((CodeCheckProjectData)app.getDataComponent());
           data.setFile(filestore.getActiveCheckFile());
           filestore.updateRecentProject(data.getTitle(),data.getPath());
           workspace.reloadWorkspace(app.getDataComponent());
           workspace.activateWorkspace(app.getGUI().getAppPane());
        }
           
    }

    public void displayAboutDialog() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("");        
        alert.setHeaderText(props.getProperty(ABOUT_LABEL_TEXT));
        Text titleText = new Text(props.getProperty(APP_TITLE) + "\n\n");
        titleText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 16));        
        String aboutString = props.getProperty(VERSION_TEXT) + " " + props.getProperty(APP_VERSION);
        aboutString += "\n" + props.getProperty(AUTHOR_TEXT);
        aboutString += "\n" + props.getProperty(LEGAL_NOTICE);
        Text aboutText = new Text(aboutString);

        TextFlow aboutTextFlow = new TextFlow(titleText,aboutText);
        aboutTextFlow.setLineSpacing(4);
        aboutTextFlow.setPadding(new Insets(15,50,15,50));
        aboutTextFlow.setTextAlignment(TextAlignment.CENTER);
        alert.getDialogPane().setContent(aboutTextFlow);

        alert.showAndWait();
    }
    public void handlePrevStepRequest(){
        int index = Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace());

        workspace.changeToWorkspace(index-1);
        if(index-2 <= 0){
            workspace.prevButton.setDisable(true);
            workspace.aboutButton.setDisable(true);
        }else if(workspace.prevButton.isDisabled() && workspace.aboutButton.isDisabled()) {
            workspace.prevButton.setDisable(false);
            workspace.aboutButton.setDisable(false);

        }
    }
    public void handleNextStepRequest(){
        int index = Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace());

        workspace.changeToWorkspace(index+1);
        if(index+2 >= workspace.stepPanes.length)
            workspace.nextButton.setDisable(true);
        else if(workspace.nextButton.isDisabled())
            workspace.nextButton.setDisable(false);
        
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
    public Button initChildButton(Pane toolbar,String icon, String tooltip,boolean disabled) {

        return app.getGUI().initChildButton(toolbar, icon, tooltip, disabled);
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
