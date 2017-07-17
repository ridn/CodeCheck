/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import cc.CodeCheckProp;
import static cc.CodeCheckProp.ABOUT_LABEL_TEXT;
import static cc.CodeCheckProp.APP_PATH_WORK;
import static cc.CodeCheckProp.APP_VERSION;
import static cc.CodeCheckProp.AUTHOR_TEXT;
import static cc.CodeCheckProp.CORRUPT_CHECK_HEADER;
import static cc.CodeCheckProp.CORRUPT_CHECK_MESSAGE;
import static cc.CodeCheckProp.DELETE_PROMPT_HEADER;
import static cc.CodeCheckProp.DELETE_PROMPT_MESSAGE;
import static cc.CodeCheckProp.LEGAL_NOTICE;
import static cc.CodeCheckProp.NO_SELECTION_MESSAGE;
import static cc.CodeCheckProp.NO_SELECTION_TITLE;
import static cc.CodeCheckProp.SUCCESS_EXTRACT_MESSAGE;
import static cc.CodeCheckProp.VERSION_TEXT;
import static cc.CodeCheckProp.ZIP_ERROR_MESSAGE;
import static cc.CodeCheckProp.ZIP_ERROR_TITLE;
import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import cc.filestore.CodeCheckFileStore.CodeCheckFolder;
import cc.viewer.CodeCheckFileViewer;
import cc.viewer.CodeCheckResultsViewer;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import djf.ui.AppMessageDialogSingleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.io.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import properties_manager.PropertiesManager;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspaceViewController {
    static enum MESSAGE_TYPE {
        MESSAGE_NORMAL,
        MESSAGE_SUCCESS,
        MESSAGE_ERROR,
        MESSAGE_URL
    }
    
    final private CodeCheckApp app;
    final private CodeCheckWorkspaceView workspace;
    private ReentrantLock stepProgressLock;
    private ArrayList<String> successMessages,failMessages;
    
    public CodeCheckWorkspaceViewController(CodeCheckApp initApp,CodeCheckWorkspaceView view) {
        app = initApp;
        workspace = view;
        stepProgressLock = new ReentrantLock();
        successMessages = new ArrayList<>();
        failMessages = new ArrayList<>();

    }
    public void handleLoadRequest() {
        File selectedFile;
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        if(CodeCheckApp.OS.contains("mac")){ 
            FileChooser fileChooser = new FileChooser();
            //fileChooser.setTitle();
            fileChooser.setInitialDirectory(new File(props.getProperty(APP_PATH_WORK)));
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(props.getProperty(WORK_FILE_EXT_DESC), "*." + props.getProperty(WORK_FILE_EXT), "*.cck.folder"));
            selectedFile = fileChooser.showOpenDialog(app.getGUI().getWindow());
        }else{
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setInitialDirectory(new File(props.getProperty(APP_PATH_WORK)));
            selectedFile = dirChooser.showDialog(app.getGUI().getWindow());
        }
        if (selectedFile != null) {
            if(isValidCodeCheckFile(selectedFile.getAbsolutePath())){
                ((CodeCheckFileStore)app.getFileComponent()).loadProject(selectedFile);       
            }else{
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle(props.getProperty(APP_TITLE));        
                alert.setHeaderText(props.getProperty(CORRUPT_CHECK_HEADER));
                alert.setContentText(props.getProperty(CORRUPT_CHECK_MESSAGE));
                alert.showAndWait().ifPresent(response -> {
                    handleLoadRequest();
                });
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
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        Path pathToRemove = (Path)currentPane.filesView.getSelectionModel().getSelectedItems().get(0);
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(props.getProperty(APP_TITLE));        
        alert.setHeaderText(props.getProperty(DELETE_PROMPT_HEADER)+pathToRemove.getFileName() );
        alert.setContentText(props.getProperty(DELETE_PROMPT_MESSAGE));
        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK){
                CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
                dataManager.handleFileDeletion(pathToRemove);  
                currentPane.filesView.getItems().remove(pathToRemove);                
            }
        });

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
        CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        Path file = (Path)currentPane.filesView.getSelectionModel().getSelectedItems().get(0);
        CodeCheckFileViewer.getFileViewer().viewFile(file);

        
    }
    public void handleStepActionRequest(int actionIndex) {
        Task<Void> task = new Task<Void>() {                    
            @Override
            protected Void call() throws Exception {
                try {
                    //TODO: FILE ACTIONS NOT WORKING ON WINDOWS?
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
                            workspace.setCheckResults(codeCheckWithResults());
                            break;
                        case VIEW_RESULTS:
                            launchViewerWithURL(workspace.codeCheckURL);
                            break;
                    }                    
                    Platform.runLater(()-> {
                        handleRefreshRequest();
                        printAllMessages();
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
        //ObservableList<Path> renameList = currentPane.filesView.getSelectionModel().getSelectedItems();
        ObservableList<Path> renameList = currentPane.filesView.getItems();
        final double stepSize = renameList.size();
        renameList.forEach((file) -> {
            try {
                int firstIndex = file.getFileName().toString().indexOf("_");
                if(firstIndex >= 0){
                    String newName = file.getFileName().toString().substring(++firstIndex, file.getFileName().toString().indexOf("_", firstIndex)) + ".zip";
                    Files.move(file, file.resolveSibling(newName));
                    Platform.runLater(()-> {
                        int old = currentPane.filesView.getItems().indexOf(file);
                        this.updateProgressBar((old+1)/stepSize);
                        currentPane.filesView.getItems().set(old, file.resolveSibling(newName));
                    });
                    Thread.sleep(10);

                    successMessages.add(file.getFileName().toString());
                    //printMessageToLog(PropertiesManager.getPropertiesManager().getProperty(SUCCESS_RENAME_MESSAGE), MESSAGE_TYPE.MESSAGE_SUCCESS);
                    //printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                }else{
                    //INVALID NAMING SCHEME
                    //ALREADY RENAMED?
                    //TODO: HANDLE INVALID NAME SCHEME
                    Platform.runLater(()-> {
                        int old = currentPane.filesView.getItems().indexOf(file);
                        this.updateProgressBar((old+1)/stepSize);
                    });
                    Thread.sleep(10);

                }
            } catch (IOException ex) {
                //printMessageToLog(PropertiesManager.getPropertiesManager().getProperty(FAIL_RENAME_MESSAGE), MESSAGE_TYPE.MESSAGE_ERROR);
                //printMessageToLog(file.getFileName().toString(), MESSAGE_TYPE.MESSAGE_NORMAL);
                failMessages.add(file.getFileName().toString());
                Platform.runLater(()-> {
                    int old = currentPane.filesView.getItems().indexOf(file);
                    this.updateProgressBar((old+1)/stepSize);
                });
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
                    if(!header.getFileName().endsWith(".txt") && !Files.isHidden(file.resolve(header.getFileName()))){
                        Path outfile;
                        if(extension.equals("all") || header.getFileName().endsWith(extension)){
                            outfile = file.getParent().resolveSibling(sectionDir+header.getFileName());
                        }else{
                            outfile = file.getParent().resolveSibling(sectionDir+"invalid"+File.separator+header.getFileName());
                            //System.out.println(outfile.getFileName());
                            if(!header.isDirectory())
                            failMessages.add("-" + header.getFileName());
                        }

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
                            if(!needsExtraParent && !header.isDirectory() && !outfile.toString().contains("invalid"))
                                successMessages.add("-" + header.getFileName());
                        }
                    }
                }
                Platform.runLater(()-> {
                    int stepIndex = unzipList.indexOf(file);
                    this.updateProgressBar((stepIndex+1)/stepSize);
                });
                Thread.sleep(10);
                if(needsExtraParent)successMessages.add("-" + file.getFileName().toString());
            } catch (Exception ex) {
                failMessages.add("-" + file.getFileName().toString());
                AppMessageDialogSingleton.getSingleton().show(PropertiesManager.getPropertiesManager().getProperty(ZIP_ERROR_TITLE),
                        PropertiesManager.getPropertiesManager().getProperty(ZIP_ERROR_MESSAGE) + file.getFileName());
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
    private String compileFileExtractorTypes() {
        StringBuilder comp = new StringBuilder(30);
        comp.append("glob:*.{");
        CodeCheckWorkspacePane extractPane = (CodeCheckWorkspacePane)workspace.stepPanes[3];
        Pane checkboxes = (Pane)extractPane.getExtras(0,1);
        for(Node box: checkboxes.getChildren()){
            if(checkboxes.getChildren().indexOf(box) == checkboxes.getChildren().size()-1){
                //CUSTOM CHECKBOX
                List container = ((Pane)box).getChildren();
                if(((CheckBox)container.get(0)).isSelected()){
                    String entry = ((TextField)container.get(1)).getText();
                    if(comp.indexOf("{") != comp.length()-1)
                        comp.append( ",");
                    entry = entry.replaceAll("\\.|\\s", "");
                    comp.append(entry);
                }
            }else{
                if(((CheckBox)box).isSelected()){
                    if(comp.indexOf("{") != comp.length()-1)
                        comp.append( ",");
                    comp.append(((CheckBox)box).getText().replaceAll("(\\.|\\s)", ""));
                }
            }
            
        };
        comp.append("}");
        return comp.toString();
    }
    private void extractSubmissionCode() {
        final String fileTypes = compileFileExtractorTypes();
        if(fileTypes.length() <= 9){
            Platform.runLater(()-> {
                AppMessageDialogSingleton.getSingleton().show(PropertiesManager.getPropertiesManager().getProperty(NO_SELECTION_TITLE),
                        PropertiesManager.getPropertiesManager().getProperty(NO_SELECTION_MESSAGE));
            });
        }else{
            CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();
            CodeCheckWorkspacePane currentPane = (CodeCheckWorkspacePane)workspace.getWorkspace();
            ObservableList<Path> studentList = currentPane.filesView.getSelectionModel().getSelectedItems();
            final double stepSize = studentList.size();
            studentList.forEach((file) -> {
                successMessages.add("-" + file.getFileName().toString());

                Stream<Path> matches;
                try {
                    PathMatcher matcher = FileSystems.getDefault().getPathMatcher(fileTypes);
                    matches = Files.find(file,5,(path, basicFileAttributes) ->   matcher.matches(path.getFileName()));//String.valueOf(path).endsWith(".java"));                
                    //if(matches.count() > 0)
                        matches.forEach((path)->{
                            try {
                                Path outfile = file.getParent().resolveSibling(CodeCheckFolder.CODE.toString()+File.separator+file.getFileName());
                                if(Files.notExists(outfile))
                                    Files.createDirectories(outfile);
                                Files.copy(path, outfile.resolve(path.getFileName()),REPLACE_EXISTING);
                                Platform.runLater(()-> {
                                    int stepIndex = studentList.indexOf(file);
                                    this.updateProgressBar((stepIndex+1)/stepSize);
                                });
                                successMessages.add("---" + path.getFileName().toString());
                            } catch (IOException ex) {
                                failMessages.add("---" + path.getFileName().toString());
                                //Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        });
                    /*else{
                        //NO FILES FOUND TO EXTRACT
                        //TODO: DISPLAY NO FILES MESSAGE
                    }*/
                } catch (IOException ex) {
                    Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }

        
    }
    private URL codeCheckWithResults() {
        try {
            URL url = new URL("https://ridn.me");
            for(int i = 0; i < 100; i++){
                double perc = (i+1)/100.0;
                Platform.runLater( () -> {
                    updateProgressBar(perc);
                });
            int sleepTime = ThreadLocalRandom.current().nextInt(10, 120 + 1);
            Thread.sleep(sleepTime);
            }
            printMessageToLog("Student Plagiarism results can be found at",MESSAGE_TYPE.MESSAGE_NORMAL);
            printMessageToLog(url.toString(),MESSAGE_TYPE.MESSAGE_URL);

            return url;
        } catch (MalformedURLException ex) {
            Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    private void launchViewerWithURL(URL url) {
        Platform.runLater( () -> {
            System.out.println("Launched viewer with url: " + url);
            Stage stage = new Stage();
            CodeCheckResultsViewer viewer = new CodeCheckResultsViewer(stage,url);
            stage.setScene(new Scene(viewer));
            stage.show();
        });
        
    }
    public Button initChildButton(Pane toolbar,String icon, String tooltip,boolean disabled) {

        return app.getGUI().initChildButton(toolbar, icon, tooltip, disabled);
    }
    private void printAllMessages() {
        //UGLY HAX YO
        int textIndex = (2*Arrays.asList(workspace.stepPanes).indexOf(workspace.getWorkspace())) + SUCCESS_EXTRACT_MESSAGE.ordinal();
        StringBuilder sb = new StringBuilder();
        
        if(!successMessages.isEmpty())
            printMessageToLog(PropertiesManager.getPropertiesManager().getProperty(CodeCheckProp.values()[textIndex]), MESSAGE_TYPE.MESSAGE_SUCCESS);
        for(String message : successMessages){
            sb.append(message+"\n");
        }
        printMessageToLog(sb.toString().trim(),MESSAGE_TYPE.MESSAGE_NORMAL);
        if(sb.length() > 0)sb.delete(0,sb.length()-1);
        if(!failMessages.isEmpty())
            printMessageToLog(PropertiesManager.getPropertiesManager().getProperty(CodeCheckProp.values()[textIndex+1]), MESSAGE_TYPE.MESSAGE_ERROR);
        for(String message : failMessages){
            sb.append(message+"\n");
        }
        printMessageToLog(sb.toString().trim(),MESSAGE_TYPE.MESSAGE_NORMAL);
        successMessages.clear();
        failMessages.clear();
    }
    private void printMessageToLog(String message,MESSAGE_TYPE type) {
        if(!message.isEmpty())
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
                case MESSAGE_URL:
                    logText.setFill(Color.BLUE);
                    logText.setUnderline(true);
                    //text.setStyle("-fx-text-inner-color: red;");
                    break;
            }
            activePane.actionLog.getChildren().addAll(logText);
            activePane.scrollToBottom();
        });
    }

}
