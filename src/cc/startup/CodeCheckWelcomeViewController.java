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
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
       app.handleWelcomeViewResponse(new CodeCheckProjectData());
   }
   public void handleCloseRequest() {
       
   }
   public void handleRecentProjectLoadRequest(String proectName) {
       
   }
   private CodeCheckProjectData initDataComponentForCheck(String filePath) {
       return new CodeCheckProjectData();
   }
}
