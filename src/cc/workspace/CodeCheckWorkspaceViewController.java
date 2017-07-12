/*
 * Author: Dan Niyazov 
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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javafx.collections.ObservableList;
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
           //TODO: READ DATA FROM FILE USING DATA COMP?
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
    }
    public void handleNextStepRequest(){
        int index = Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace());
        workspace.changeToWorkspace(index+1);
        
    }
    public void handleHomeStepRequest() {
        workspace.changeToWorkspace(0);

    }
    public void handleRemoveRequest() {
        //TODO: IMPLEMENT REMOVE FILE
        
    }
    public void handleRefreshRequest() {
        //TODO: IMPLEMENT REFRESH LIST VIEW
        
    }
    public void handleViewRequest() {
        //TODO: IMPLEMENT FILE VIEW
        
    }
    public void handleStepActionRequest(int actionIndex) {
        switch(CodeCheckStepActions.values()[actionIndex]) {
            case EXTRACT_SUBMISSIONS:
                extractSubmissions();
                break;
            case RENAME_SUBMISSIONS:
                renameSubmissions();
                break;
            case UNZIP_SUBMISSIONS:
                unzipSubmissions();
                break;
            case EXTRACT_CODE:
                extractSubmissionCode();
                break;
            case CODE_CHECK:
                break;
            case VIEW_RESULTS:
                break;
        }
            renameSubmissions();
        
    }
    public void updateProgressBar() {
        //TODO: IMPLEMENT PROGRESS BAR UPDATES
        
    }
    private void extractSubmissions() {
        //TODO: IMPLEMENT SUBMISSION EXTRACTION

    }

    private void renameSubmissions() {
        //TODO: DO ASYNC IN NEW THREAD
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        //ObservableList unzipList = dataManager.getListing(1);
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        ObservableList<Path> renameList = currentPane.filesView.getSelectionModel().getSelectedItems();
        renameList.forEach((file) -> {
            try {
                int firstIndex = file.getFileName().toString().indexOf("_");
                if(firstIndex >= 0){
                    String newName = file.getFileName().toString().substring(++firstIndex, file.getFileName().toString().indexOf("_", firstIndex)) + ".zip";
                    Files.move(file, file.resolveSibling(newName));
                    printMessageToLog("Successfully renamed file:", MESSAGE_TYPE.MESSAGE_SUCCESS);
                    printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                }else{
                    //INVALID NAMING SCHEME
                    //ALREADY RENAMED?
                    //TODO HANDLE INVALID NAME SCHEME
                }
            } catch (IOException ex) {
                //TODO DONT PRINT AFTER EVERY ATTEMPT
                printMessageToLog("Failed to rename file:", MESSAGE_TYPE.MESSAGE_ERROR);
                printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                //Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //NOW REFRESH THE LIST
        dataManager.refreshList(1);
        currentPane.filesView.setItems(dataManager.getListing(1));
        currentPane.filesView.refresh();

    }
    private void unzipSubmissions() {
        //TODO: IMPLEMENTS UNZIP
    }
    private void extractSubmissionCode() {
        //TODO: IMPLEMENTS CODE EXTRACTION
        
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
