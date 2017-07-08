/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import java.net.URL;
import java.util.Arrays;

/**
 *
 * @author danniyazov
 */
class CodeCheckWorkspaceViewController {
    private CodeCheckApp app;
    private CodeCheckWorkspaceView workspace;
    
    public CodeCheckWorkspaceViewController(CodeCheckApp initApp,CodeCheckWorkspaceView view) {
        app = initApp;
        workspace = view;
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
        
    }
    public void handleRefreshRequest() {
        
    }
    public void handleViewRequest() {
        
    }
    public void handleStepActionRequest(String action) {
        
    }
    public void handleAboutButtonPressed() {
        
    }
    public void handleRenameProjectRequest() {
        
    }
    public void renameProject(String name) {
        
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
    private void printMessageToLog(String message) {
        
    }

}
