/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.filestore;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.APP_PATH_WORK;
import static cc.CodeCheckProp.NEW_DIALOG_CONTENT_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_ERROR_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_HEADER_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_PROMPT_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_TITLE_TEXT;
import cc.data.CodeCheckProjectData;
import djf.components.AppDataComponent;
import djf.components.AppFileComponent;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import java.io.File;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckFileStore implements AppFileComponent{
    private CodeCheckApp app;
    public CodeCheckFileStore(CodeCheckApp initApp) {
        app = initApp;
    }
    
    public void handleNewRequest() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // The Java 8 way to get the response value (with lambda expression).
        
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
                String dirPath = props.getProperty(APP_PATH_WORK) + dialog.getEditor().textProperty().get().trim();
                //ADD CUSTOM FILE EXTENSION
                dirPath += "." + props.getProperty(WORK_FILE_EXT);
                File newCheck = new File(dirPath);
                // Check whether some conditions are fulfilled
                if (!newCheck.exists()) {
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
                    //THIS PROJECT/DIR EXISTS
                    dialog.setHeaderText(props.getProperty(NEW_DIALOG_ERROR_TEXT));
                    //dialog.getDialogPane().setHeader(btOk);
                    System.out.println("Project already exists");
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
            }else{
                //CREATE ALL SUB-DIRS
                String subDIRs[] = new String[4];
                subDIRs[0] = "blackboard";
                subDIRs[1] = "submissions";
                subDIRs[2] = "projects";
                subDIRs[3] = "code";
                
                for(String folder : subDIRs){
                    File subDIR = new File(newCheck.getPath()+ "/" + folder);
                    if (!subDIR.exists()) {
                        boolean success = subDIR.mkdir();
                        if(!success){
                            //WE FAILED TO CREATE A DIR
                        }
                    }
                }
                //CREATE NEW PROJECT
                //SET THE PROJECT TITLE AND OTHER NECESSARY DATA HERE
                CodeCheckProjectData newProjectData = new CodeCheckProjectData();
                newProjectData.setFile(newCheck);
                app.handleWelcomeViewResponse(newProjectData);
                
            }
        });
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
