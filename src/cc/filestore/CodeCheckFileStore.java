/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.filestore;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.*;
import cc.data.CodeCheckProjectData;
import cc.workspace.CodeCheckWorkspaceView;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import properties_manager.PropertiesManager;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

/**
 *
 * @author danniyazov
 */
public class CodeCheckFileStore implements AppFileComponent{
    public enum CodeCheckFolder {
        BLACKBOARD,
        SUBMISSIONS,
        PROJECTS,
        CODE;
        public String toString() {
            return this.name().toLowerCase();
        }
    }
    static final String JSON_RECENTS = "recents";
    static final String JSON_PROJECT_TITLE = "title";
    static final String JSON_PROJECT_PATH = "path";
    private static ObservableList<String[]> recentProjectsArray;

    private CodeCheckApp app;
    private boolean checkFileChanged = false;
    File currentWorkCheck;
    public CodeCheckFileStore(CodeCheckApp initApp) {
        app = initApp;
        recentProjectsArray = FXCollections.observableArrayList();
        try {
            loadPersistentData();
        } catch (IOException ex) {
            //TODO: HANDLE LOAD DATA FAIL
            Logger.getLogger(CodeCheckFileStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //TODO: DONT REPEAT THIS
    public void handleNewRequest() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(props.getProperty(NEW_DIALOG_TITLE_TEXT));
        dialog.setHeaderText(props.getProperty(NEW_DIALOG_HEADER_TEXT));
        dialog.setContentText(props.getProperty(NEW_DIALOG_CONTENT_TEXT));
        dialog.getEditor().setPromptText(props.getProperty(NEW_DIALOG_PROMPT_TEXT));
        //dialog.getEditor()dialog.getEditor().requestFocus();
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, 
            event -> {
                //DO VALIDITY CHECKS HERE
                String projName = dialog.getEditor().textProperty().get().trim();
                String dirPath = props.getProperty(APP_PATH_WORK) + projName;
                //ADD CUSTOM FILE EXTENSION
                dirPath += "." + props.getProperty(WORK_FILE_EXT);
                File newCheck = new File(dirPath);
                // Check whether some conditions are fulfilled
                if (!projName.isEmpty() && !newCheck.exists()) {
                    //TRY TO MAKE THE PROJECT FOLDER
                    boolean successful = newCheck.mkdirs();
                    if(successful){
                        //CONTINUE TO BUTTON ACTION
                    }else{
                        //FAILED TO MAKE DIR
                        System.out.println("Failed to make project DIR");
                        //dialog.getDialogPane().getChildren().add.();
                        event.consume();
                    }
                }else{
                    if(projName.isEmpty()){
                        //TEXT IS BLANK OR EFFECTVELY BLANK!
                        dialog.setHeaderText(props.getProperty(NEW_DIALOG_EMPTY_TEXT));
                    }else{
                        //THIS PROJECT/DIR EXISTS
                        dialog.setHeaderText(props.getProperty(NEW_DIALOG_ERROR_TEXT));
                        //dialog.getDialogPane().setHeader(btOk);
                        System.out.println("Project already exists");
                    }
                    //((Pane)dialog.getDialogPane()).getChildren().add(0, projectError);
                    GridPane header = (GridPane)dialog.getDialogPane().getChildren().get(0);
                    ((Label)header.getChildren().get(0)).setTextFill(Color.RED);
                    event.consume();

                }
            }
        );
        dialog.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!dialog.getHeaderText().equals(props.getProperty(NEW_DIALOG_HEADER_TEXT))){
                dialog.setHeaderText(props.getProperty(NEW_DIALOG_HEADER_TEXT));
                    GridPane header = (GridPane)dialog.getDialogPane().getChildren().get(0);
                    ((Label)header.getChildren().get(0)).setTextFill(Color.BLACK);
   
            }
            if (!newValue.matches("[\\w\\d\\s]*")){
                dialog.getEditor().setText(newValue.replaceAll("[^\\w^\\d^\\s]", ""));
            }else if(newValue.length() > 30) {
                dialog.getEditor().setText(oldValue);
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            //DO MORE VALIDITY CHECKS HERE
            String dirPath = props.getProperty(APP_PATH_WORK) + response.trim();
            //ADD CUSTOM FILE EXTENSION
            dirPath += "." + props.getProperty(WORK_FILE_EXT);
            File newCheck = new File(dirPath);

            if (!newCheck.exists()) {
                System.out.println("Project doesn't exist?");
                //ERROR PROJECT DOESNT EXIST
                //TODO: HANDLE PROJECT DNE ERROR
            }else{
                //CREATE ALL SUB-DIRS
                for(CodeCheckFolder folder : CodeCheckFolder.values()) {                
                    File subDIR = new File(newCheck.getPath()+ File.separator + folder.toString());
                    if (!subDIR.exists()) {
                        boolean success = subDIR.mkdir();
                        if(!success){
                            //WE FAILED TO CREATE A DIR
                            //TODO: HANDLE SUB-DIR SETUP FAIL
                        }
                    }
                }
                //CREATE NEW PROJECT
                //SET THE PROJECT TITLE AND OTHER NECESSARY DATA HERE
                currentWorkCheck = newCheck;
                checkFileChanged = true;
            }
        });
        //NEW PROJECT CREATED/OPENED ADD TO TOP OF RECENTS
        //addProjectToRecents();
    }
    public void handleProjectRenameRequest() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(props.getProperty(RENAME_DIALOG_TITLE_TEXT));
        dialog.setHeaderText(props.getProperty(RENAME_DIALOG_HEADER_TEXT));
        dialog.setContentText(props.getProperty(RENAME_DIALOG_CONTENT_TEXT));
        dialog.getEditor().setPromptText(props.getProperty(RENAME_DIALOG_PROMPT_TEXT));

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, 
            event -> {
                //TODO: DO VALIDITY CHECKS HERE
                String projName = dialog.getEditor().textProperty().get().trim();
                String dirPath = props.getProperty(APP_PATH_WORK) + projName;
                //ADD CUSTOM FILE EXTENSION
                dirPath += "." + props.getProperty(WORK_FILE_EXT);
                File newCheck = new File(dirPath);                
                // Check whether some conditions are fulfilled
                if (!projName.isEmpty() && !newCheck.exists()) {
                    //TRY TO MAKE THE PROJECT FOLDER
                    boolean successful = currentWorkCheck.renameTo(newCheck);
                    if(successful){
                        //CONTINUE TO BUTTON ACTION
                        //PROJECT WAS RENAMED, UPDATE RECENTS
                        //SET THE PROJECT TITLE AND OTHER NECESSARY DATA HERE
                        currentWorkCheck = newCheck;
                        //System.out.println(newCheck.getPath());
                        //System.out.println(currentWorkCheck.getPath());
                        checkFileChanged = true;

                    }else{
                        //FAILED TO MAKE DIR
                        //TODO: HANDLE PROJECT RENAME FAILED
                        System.out.println("Failed to make project DIR");
                        //dialog.getDialogPane().getChildren().add.();
                        event.consume();
                    }
                }else{
                    if(projName.isEmpty()){
                        //TEXT IS BLANK OR EFFECTVELY BLANK!
                        dialog.setHeaderText(props.getProperty(NEW_DIALOG_EMPTY_TEXT));
                    }else{
                        //THIS PROJECT/DIR EXISTS
                        dialog.setHeaderText(props.getProperty(NEW_DIALOG_ERROR_TEXT));
                        //dialog.getDialogPane().setHeader(btOk);
                        System.out.println("Project already exists");
                    }
                    //((Pane)dialog.getDialogPane()).getChildren().add(0, projectError);
                    GridPane header = (GridPane)dialog.getDialogPane().getChildren().get(0);
                    ((Label)header.getChildren().get(0)).setTextFill(Color.RED);
                    event.consume();

                }
            }
        );
        dialog.getEditor().textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!dialog.getHeaderText().equals(props.getProperty(RENAME_DIALOG_HEADER_TEXT))){
                dialog.setHeaderText(props.getProperty(RENAME_DIALOG_HEADER_TEXT));
                    GridPane header = (GridPane)dialog.getDialogPane().getChildren().get(0);
                    ((Label)header.getChildren().get(0)).setTextFill(Color.BLACK);
   
            }
            if (!newValue.matches("[\\w\\d\\s]*")){
                dialog.getEditor().setText(newValue.replaceAll("[^\\w^\\d^\\s]", ""));
            }else if(newValue.length() > 30) {
                dialog.getEditor().setText(oldValue);
            }
        });

        dialog.showAndWait();
    }
    public void updateRecentProject(String title, String path){
        /*String[] projectRef = new String[2];
        projectRef[0] = title;
        projectRef[1] = path;
        */
        recentProjectsArray.remove(0);
        addProjectToRecents(title,path);
    }
    public void addProjectToRecents(CodeCheckProjectData data) {
        addProjectToRecents(data.getTitle(),data.getPath(), true);
    }
    public void addProjectToRecents(String title, String path) {
        addProjectToRecents(title,path, true);
        
    }
    public void addProjectToRecents(String title, String path, boolean atStart) {
        //check if its in the list and remove it first
        String[] projectRef = new String[2];
        projectRef[0] = title;
        projectRef[1] = path;
        recentProjectsArray.removeIf(proj -> proj[1].equals(projectRef[1]));
        if (atStart)
            recentProjectsArray.add(0, projectRef);
        else
            recentProjectsArray.add(projectRef);
            
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String filePath = props.getProperty(APP_PATH_WORK) + props.getProperty(APP_PERSISTENT_DATA);
        try {
            initPersistentData(filePath);
        } catch (FileNotFoundException ex) {
            //TODO: HANDLE MAKE PERSISTENT DATA FAIL
            Logger.getLogger(CodeCheckFileStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public File getActiveCheckFile() {
        checkFileChanged = false;
        return currentWorkCheck;
    }
    public boolean activeCheckFileChanged() {
        return checkFileChanged;
    }
    private void loadPersistentData() throws IOException {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        JsonObject json;
        String filePath = props.getProperty(APP_PATH_WORK) + props.getProperty(APP_PERSISTENT_DATA);
        File jsonFile = new File(filePath);
        if(jsonFile.exists()){
            json = loadJSONFile(filePath);
        }else{
            //CREATE PERSISTENT DATA
            json = initPersistentData(filePath);
        }

	// CLEAR THE OLD DATA OUT
	//CodeCheckProjectData dataManager = (CodeCheckProjectData)data;
        //dataManager.resetData();

        // NOW LOAD ALL THE DATA FROM THE json OBJECT
        
        JsonArray jsonRecentsArray = json.getJsonArray(JSON_RECENTS);
        for (int i =  0; i < jsonRecentsArray.size(); i++) {
            JsonObject jsonProject = jsonRecentsArray.getJsonObject(i);
            String fileName = jsonProject.getString(JSON_PROJECT_TITLE);
            String path = jsonProject.getString(JSON_PROJECT_PATH);
            addProjectToRecents(fileName, path, false);
        }
    }
  
    private JsonObject initPersistentData(String filePath) throws FileNotFoundException {
        // GET THE DATA
	CodeCheckProjectData dataManager = (CodeCheckProjectData)app.getDataComponent();

	// NOW BUILD THE SLIDES JSON OBJECTS TO SAVE
	JsonArrayBuilder recentsArrayBuilder = Json.createArrayBuilder();
	
	for (String[] recentProj : recentProjectsArray) {	    
	    JsonObject recentJson = Json.createObjectBuilder()
		    .add(JSON_PROJECT_TITLE, recentProj[0])
                    .add(JSON_PROJECT_PATH, recentProj[1]).build();
	    recentsArrayBuilder.add(recentJson);
	}
        
	JsonArray recentsArray = recentsArrayBuilder.build();
        
	// THEN PUT IT ALL TOGETHER IN A JsonObject
	JsonObject dataManagerJSO = Json.createObjectBuilder()
		.add(JSON_RECENTS, recentsArray)
		.build();
	
	// AND NOW OUTPUT IT TO A JSON FILE WITH PRETTY PRINTING
	Map<String, Object> properties = new HashMap<>(1);
	properties.put(JsonGenerator.PRETTY_PRINTING, true);
	JsonWriterFactory writerFactory = Json.createWriterFactory(properties);
	StringWriter sw = new StringWriter();
	JsonWriter jsonWriter = writerFactory.createWriter(sw);
	jsonWriter.writeObject(dataManagerJSO);
	jsonWriter.close();

	// INIT THE WRITER
	OutputStream os = new FileOutputStream(filePath);
	JsonWriter jsonFileWriter = Json.createWriter(os);
	jsonFileWriter.writeObject(dataManagerJSO);
	String prettyPrinted = sw.toString();
	PrintWriter pw = new PrintWriter(filePath);
	pw.write(prettyPrinted);
	pw.close();
        
        return dataManagerJSO;
    }
    // HELPER METHOD FOR LOADING DATA FROM A JSON FORMAT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
	InputStream is = new FileInputStream(jsonFilePath);
	JsonReader jsonReader = Json.createReader(is);
	JsonObject json = jsonReader.readObject();
	jsonReader.close();
	is.close();
	return json;
    }
    public ObservableList<String[]> getRecentsList() {
        return recentProjectsArray;
    }
    public void loadProject(File selectedFile) {
        //try {
                CodeCheckWorkspaceView workspace = (CodeCheckWorkspaceView)app.getWorkspaceComponent();
                app.getDataComponent().resetData();
                //TODO: TEMP SET DATA LIKE THIS, FILE COMP WILL DO THIS LATER?
                ((CodeCheckProjectData)app.getDataComponent()).setFile(selectedFile);
                workspace.resetWorkspace();
                workspace.reloadWorkspace(app.getDataComponent());
                workspace.activateWorkspace(app.getGUI().getAppPane());
                //NEW PROJECT OPENED ADD TO TOP OF RECENTS
                CodeCheckFileStore filestore = ((CodeCheckFileStore)app.getFileComponent());
                filestore.addProjectToRecents((CodeCheckProjectData)app.getDataComponent());
                currentWorkCheck = selectedFile;
                checkFileChanged = true;
            /*    app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());
                
            } catch (IOException ex) {
                Logger.getLogger(CodeCheckWorkspaceViewController.class.getName()).log(Level.SEVERE, null, ex);
            }*/
    }
    @Override
    public void saveData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void loadData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void exportData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void importData(AppDataComponent data, String filePath) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
