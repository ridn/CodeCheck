/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.workspace;

import cc.CodeCheckApp;
import java.net.URL;
import java.util.Arrays;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
    public void printMessageToLog(String message,MESSAGE_TYPE type) {
        CodeCheckWorkspacePane activePane = (CodeCheckWorkspacePane)workspace.getWorkspace();
        Text logText = new Text(message + "\n"); 
        logText.setFont(new Font(15)); 
        
        switch(type){
            case MESSAGE_NORMAL:
                logText.setFill(Color.BLACK); 
                break;
            case MESSAGE_SUCCESS:
                logText.setFill(Color.GREEN); 
                break;
            case MESSAGE_ERROR:
                logText.setFill(Color.RED); 
                break;

        }
        activePane.actionLog.getChildren().add(logText);
    }

}
