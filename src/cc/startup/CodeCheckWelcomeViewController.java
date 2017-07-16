/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import java.io.File;

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
       if(filestore.activeCheckFileChanged() && filestore.getActiveCheckFile() != null) {
           newProjectData.setFile(filestore.getActiveCheckFile());
           app.handleWelcomeViewResponse(newProjectData);
           filestore.addProjectToRecents(newProjectData);
       }else{
           //--TODO--: HANDLE DID NOT MAKE NEW PROJECT
       }
       
   }
   public void handleCloseRequest() {
        app.handleWelcomeViewResponse(null);
   }
   public void handleRecentProjectLoadRequest(String projectPath) {
        app.handleWelcomeViewResponse(initDataComponentForCheck(projectPath));

       ((CodeCheckFileStore)app.getFileComponent()).loadProject(new File(projectPath));
   }
   private CodeCheckProjectData initDataComponentForCheck(String filePath) {
       CodeCheckProjectData data = new CodeCheckProjectData();
       data.setFileFromPath(filePath);
       return data;
   }
}
