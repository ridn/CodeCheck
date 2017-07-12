/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.data;

import djf.components.AppDataComponent;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author danniyazov
 */
public class CodeCheckProjectData implements AppDataComponent{
    private File projectFile;
    private String projectPath, projectTitle;
    private ObservableList bbSubmissionsList, studentSubmissionList;
    public void setFile(File file){
        projectFile = file;
    }
    public void setFileFromPath(String path) {
        File tmp = new File(path);
        if(tmp.exists())
            setFile(tmp);
        else
            System.out.println("FILE NOT FOUND ERROR");
    }
    public String getPath(){
        return projectFile.getAbsolutePath();
    }
    public String getTitle() {
        //return projectFile.getName();
        return projectFile.getName().replaceFirst("[.][^.]+$", "");

    }
    public ObservableList<String> getListing(int step) {
        switch(step){
            case 0:
                if(bbSubmissionsList == null){
                    bbSubmissionsList = initListing("blackboard");                    
                }
                return bbSubmissionsList;
            case 1:
                if(studentSubmissionList == null){
                    studentSubmissionList = initListing("submissions");
                }
                return studentSubmissionList;
        }
        
        return null;

    }
    private ObservableList<String> initListing(String path) {
        ObservableList tmpCollection = FXCollections.observableArrayList();
        Path folder = Paths.get(projectFile.getAbsolutePath() + "/" + path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder,"*.zip")) {            
            for (Path entry : stream) {
                tmpCollection.add(entry);
            }
        } catch (IOException ex) {
        // An I/O problem has occurred
            ex.printStackTrace();
        }
        return (tmpCollection.isEmpty()) ? null : tmpCollection;
    }
    public void refreshList(int step) {
         switch(step){
            case 0:
                bbSubmissionsList = null;
                bbSubmissionsList = getListing(step);                    
                break;
            case 1:
                studentSubmissionList = null;
                studentSubmissionList = getListing(step);
                break;
        }
    }
    @Override
    public void resetData() {
        //null out all the data
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public String toString() {
       return getTitle(); 
    }
    
}
