/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.data;

import cc.filestore.CodeCheckFileStore.CodeCheckFolder;
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
    private ObservableList bbSubmissionsList, studentSubmissionList, projectsList, codeList;
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
        //TODO: IMPLEMENT LIST RETREVIAL FOR REMAINING STEPS
        DirectoryStream.Filter<Path> filter = e-> {
                                return !Files.isHidden(e);
                        };
        if(projectFile != null)
            switch(step){
                case 0:
                    if(bbSubmissionsList == null){
                        filter = e-> {
                                return e.getFileName().toString().endsWith(".zip");
                        };
                        bbSubmissionsList = initListing(CodeCheckFolder.BLACKBOARD.toString(),filter);                    
                    }
                    return bbSubmissionsList;
                case 1:
                case 2:
                    if(studentSubmissionList == null){
                        studentSubmissionList = initListing(CodeCheckFolder.SUBMISSIONS.toString(),filter);
                    }
                    return studentSubmissionList;
                case 3:
                    filter = e-> {
                                return Files.isDirectory(e);
                    };
                    if(projectsList == null){                        
                        projectsList = initListing(CodeCheckFolder.PROJECTS.toString(),filter);
                    }
                    return projectsList;
                case 4:
                    filter = e-> {
                                return Files.isDirectory(e);
                    };
                    if(codeList == null){                        
                        codeList = initListing(CodeCheckFolder.CODE.toString(),filter);
                    }
                    return codeList;
            }
        
        return null;

    }
    private ObservableList<String> initListing(String path,DirectoryStream.Filter filter) {
        //TODO: IMPLEMENT STEP SPECIFIC FILTER
        ObservableList tmpCollection = FXCollections.observableArrayList();
        Path folder = Paths.get(projectFile.getAbsolutePath() + "/" + path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder,filter)) {            
            for (Path entry : stream) {
                tmpCollection.add(entry);
            }
        } catch (IOException ex) {
            // An I/O problem has occurred

        }
        return (tmpCollection.isEmpty()) ? null : tmpCollection;
    }
    public void refreshList(int step) {
        //TODO: IMPLEMENT REFRESH FOR REMAINING STEPS
         switch(step){
            case 0:
                bbSubmissionsList = null;
                bbSubmissionsList = getListing(step);                    
                break;
            case 1:
            case 2:
                studentSubmissionList = null;
                studentSubmissionList = getListing(step);
                break;
            case 3:
                projectsList = null;
                projectsList = getListing(step);
                break;
            case 4:
                codeList = null;
                codeList = getListing(step);
                break; 
        }
    }
    @Override
    public void resetData() {
        //TODO: IMPLEMENT DATA RESET
        //null out all the data
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public String toString() {
       return (projectFile != null) ? getTitle() : super.toString(); 
    }
    
}
