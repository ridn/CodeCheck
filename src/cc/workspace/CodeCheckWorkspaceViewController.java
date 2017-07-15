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
import cc.filestore.CodeCheckFileStore.CodeCheckFolder;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
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
    
    final private CodeCheckApp app;
    final private CodeCheckWorkspaceView workspace;
    private ReentrantLock stepProgressLock;
    
    public CodeCheckWorkspaceViewController(CodeCheckApp initApp,CodeCheckWorkspaceView view) {
        app = initApp;
        workspace = view;
        stepProgressLock = new ReentrantLock();

    }
    public void handleLoadRequest() {
        //TODO: SUPPORT WINDOWS SELECTION :SIGH:
        FileChooser fileChooser = new FileChooser();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        fileChooser.setInitialDirectory(new File(props.getProperty(APP_PATH_WORK)));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), "*." + props.getProperty(WORK_FILE_EXT), "*.cck.folder"));
        File selectedFile = fileChooser.showOpenDialog(app.getGUI().getWindow());
        if (selectedFile != null) {
            if(isValidCodeCheckFile(selectedFile.getAbsolutePath())){
                ((CodeCheckFileStore)app.getFileComponent()).loadProject(selectedFile);       
            }else{
                //TODO: THROW ERROR THIS IS A 'CORRUPT' CODE CHECK
                System.out.println("Corrupt code check file, did not open");
            }
        }

    }
    private boolean isValidCodeCheckFile(String path) {
        if(Files.isDirectory(Paths.get(path))){
            for(CodeCheckFolder folder : CodeCheckFolder.values()) {
                Path pathToCheck = Paths.get(path + File.separator +  folder.toString());
                if(!Files.exists(pathToCheck) || !Files.isDirectory(pathToCheck))
                    return false;
            }
            return true;
        }
        return false;
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
        //TODO: ASK CONFIRMATION TO DELETE
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        Path pathToRemove = (Path)currentPane.filesView.getSelectionModel().getSelectedItems().get(0);
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        dataManager.handleFileDeletion(pathToRemove);  
        currentPane.filesView.getItems().remove(pathToRemove);
        //handleRefreshRequest();

        
    }
    public void handleRefreshRequest(boolean clearFirst) {
        //NOW REFRESH THE LIST
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        int currentStep = Arrays.asList(workspace.stepPanes).indexOf(currentPane);
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        if(clearFirst)dataManager.refreshList(currentStep);
        currentPane.filesView.setItems(dataManager.getListing(currentStep));
        currentPane.filesView.refresh();
        
        if(++currentStep < workspace.stepPanes.length){
        //dataManager.refreshList(currentStep);
        workspace.stepPanes[currentStep].filesView.setItems(dataManager.getListing(currentStep));
        //workspace.stepPanes[currentStep].filesView.refresh();
        }
    }
    public void handleRefreshRequest() {
        //NOW REFRESH THE LIST
        handleRefreshRequest(false);
    }
    public void handleViewRequest() {
        //TODO: IMPLEMENT FILE VIEW REQUEST
        
    }
    public void handleStepActionRequest(int actionIndex) {
        Task<Void> task = new Task<Void>() {                    
            @Override
            protected Void call() throws Exception {
                try {
                    stepProgressLock.lock();
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
                    Platform.runLater(()-> {
                        handleRefreshRequest();
                    });
                    //Thread.sleep(10);
                    }
                    finally {
                        stepProgressLock.unlock();
                    }
                return null;
            }
         };
        Thread thread = new Thread(task);
        thread.start();            

    }
    public void updateProgressBar(double progress) {
        ((CodeCheckWorkspacePane)workspace.getWorkspace()).stepProgress.setProgress(progress);
        ((CodeCheckWorkspacePane)workspace.getWorkspace()).progressPerc.setText((int)(progress*100) + "%");
    }
    private void extractSubmissions() {
        unarchiveFilesForSection(CodeCheckFolder.SUBMISSIONS,".zip",false);

    }

    private void renameSubmissions() {
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        //ObservableList unzipList = dataManager.getListing(1);
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        ObservableList<Path> renameList = currentPane.filesView.getSelectionModel().getSelectedItems();
        final double stepSize = renameList.size();
        renameList.forEach((file) -> {
            try {
                int firstIndex = file.getFileName().toString().indexOf("_");
                if(firstIndex >= 0){
                    String newName = file.getFileName().toString().substring(++firstIndex, file.getFileName().toString().indexOf("_", firstIndex)) + ".zip";
                    Files.move(file, file.resolveSibling(newName));
                    Platform.runLater(()-> {
                        int old = currentPane.filesView.getItems().indexOf(file);
                        currentPane.filesView.getItems().set(old, file.resolveSibling(newName));
                        this.updateProgressBar((old+1)/stepSize);
                    });
                    Thread.sleep(10);

                    //TODO: READ MESSAGE FROM PROPS
                    //TODO: DONT PRINT AFTER EVERY ATTEMPT
                    printMessageToLog("Successfully renamed file:", MESSAGE_TYPE.MESSAGE_SUCCESS);
                    printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                }else{
                    //INVALID NAMING SCHEME
                    //ALREADY RENAMED?
                    //TODO: HANDLE INVALID NAME SCHEME
                }
            } catch (IOException ex) {
                //TODO: READ MESSAGE FROM PROPS
                //TODO: DONT PRINT AFTER EVERY ATTEMPT
                printMessageToLog("Failed to rename file:", MESSAGE_TYPE.MESSAGE_ERROR);
                printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                //Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //NOW REFRESH THE LIST
       // handleRefreshRequest();

    }
    private void unzipSubmissions() {
        unarchiveFilesForSection(CodeCheckFolder.PROJECTS,"all", true);
        
    }
    private void unarchiveFilesForSection(CodeCheckFolder section,String extension, boolean needsExtraParent) {
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        //ObservableList unzipList = dataManager.getListing(1);
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        ObservableList<Path> unzipList = currentPane.filesView.getSelectionModel().getSelectedItems();
        final double stepSize = unzipList.size();
        unzipList.forEach((file) -> {
            ZipInputStream input;
            OutputStream output;
            String sectionDir;
            try {
                sectionDir = section.toString()+File.separator;
                if(needsExtraParent){
                    sectionDir += file.getFileName().toString().replaceFirst("[.][^.]+$", "") + File.separator;
                    Path parent = file.getParent().resolveSibling(sectionDir);
                    if(Files.notExists(parent))Files.createDirectories(parent);
                }
                ZipFile zip = new ZipFile(file.toString());
                for(FileHeader header : (List<FileHeader>)zip.getFileHeaders()) {
                    if(extension.equals("all") || header.getFileName().endsWith(extension)){
                        
                        Path outfile = file.getParent().resolveSibling(sectionDir+header.getFileName());
                        if(header.isDirectory()) {
                            Files.createDirectories(outfile);
                            
                        }else{
                            if(Files.notExists(outfile.getParent())) {
                                Files.createDirectories(outfile.getParent());
                            }
                            input = zip.getInputStream(header);
                            output = new FileOutputStream(outfile.toFile());
                            int length = -1;
                            byte[] buffer = new  byte[4096];
                            while((length = input.read(buffer)) != -1)
                                output.write(buffer,0,length);
                            output.close();
                            input.close();
                        }
                    }
                }
                Platform.runLater(()-> {
                    int stepIndex = currentPane.filesView.getItems().indexOf(file);
                    this.updateProgressBar((stepIndex+1)/stepSize);
                });
                Thread.sleep(10);

            } catch (ZipException ex) {
                //TODO: HANDLE UNZIP ERROR
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        });
        /*        
        int paneToRefresh = section.ordinal();
        dataManager.refreshList(paneToRefresh);
        //workspace.stepPanes[paneToRefresh].filesView.getItems().clear();
        workspace.stepPanes[paneToRefresh].filesView.setItems(dataManager.getListing(paneToRefresh));
        workspace.stepPanes[paneToRefresh].filesView.refresh();
        */
    }
    private void extractSubmissionCode() {
        //TODO: IMPLEMENTS CODE EXTRACTION
        CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        ObservableList<Path> studentList = currentPane.filesView.getSelectionModel().getSelectedItems();
        final double stepSize = studentList.size();
        studentList.forEach((file) -> {
            //Files.find(file, 5, matcher, options);
            Stream<Path> matches;
            try {
                matches = Files.find(file,5,(path, basicFileAttributes) -> String.valueOf(path).endsWith(".java"));
                matches.forEach((path)->{
                    try {
                        Path outfile = file.getParent().resolveSibling(CodeCheckFolder.CODE.toString()+File.separator+file.getFileName());
                        if(Files.notExists(outfile))
                            Files.createDirectories(outfile);
                        Files.copy(path, outfile.resolve(path.getFileName()),REPLACE_EXISTING);
                        Platform.runLater(()-> {
                            int stepIndex = currentPane.filesView.getItems().indexOf(file);
                            this.updateProgressBar((stepIndex+1)/stepSize);
                        });

                    } catch (IOException ex) {
                        Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
            });
            } catch (IOException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        
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
        Platform.runLater( () -> {
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
        });
    }

}
