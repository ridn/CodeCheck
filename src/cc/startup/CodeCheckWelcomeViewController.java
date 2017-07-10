/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.APP_PATH_WORK;
import static cc.CodeCheckProp.NEW_DIALOG_CONTENT_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_ERROR_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_HEADER_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_PROMPT_TEXT;
import static cc.CodeCheckProp.NEW_DIALOG_TITLE_TEXT;
import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppPropertyType.WORK_FILE_EXT_DESC;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWelcomeViewController  {
    CodeCheckApp app;
    
   CodeCheckProjectData selectedProjectData;
   public CodeCheckWelcomeViewController(CodeCheckApp currentApp) {
       app = currentApp;
   }

   public void handleNewCodeCheckRequest() {
       CodeCheckFileStore filestore = ((CodeCheckFileStore)app.getFileComponent());
       filestore.handleNewRequest();
       CodeCheckProjectData newProjectData = new CodeCheckProjectData();
       newProjectData.setFile(filestore.getActiveCheckFile());
       app.handleWelcomeViewResponse(newProjectData);

   }
   public void handleCloseRequest() {
        app.handleWelcomeViewResponse(null);

   }
   public void handleRecentProjectLoadRequest(String projectName) {
       
   }
   private CodeCheckProjectData initDataComponentForCheck(String filePath) {
       return new CodeCheckProjectData();
   }
}
