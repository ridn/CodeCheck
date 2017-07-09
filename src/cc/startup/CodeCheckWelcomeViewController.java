/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import cc.data.CodeCheckProjectData;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Code Check Name");
        alert.setTitle("Create New Code Check");
        alert.setHeaderText("New Code Check");
        ButtonType buttonTypeOne = new ButtonType("Create");
        ButtonType buttonTypeTwo = new ButtonType("Cancel");

        alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOne) {
                //CREATE NEW PROJECT
                
                //DO VALIDITY CHECKS HERE
                app.handleWelcomeViewResponse(new CodeCheckProjectData());
                
            }else if(response == buttonTypeTwo) {
                //USER CANCELED
            }
        });
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
