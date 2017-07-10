/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.data;

import djf.components.AppDataComponent;
import java.io.File;

/**
 *
 * @author danniyazov
 */
public class CodeCheckProjectData implements AppDataComponent{
    private File projectFile;
    private String projectPath, projectTitle;
    public void setFile(File file){
        projectFile = file;
    }
    public String getPath(){
        return projectFile.getAbsolutePath();
    }
    public String getTitle() {
        //return projectFile.getName();
        return projectFile.getName().replaceFirst("[.][^.]+$", "");

    }
    @Override
    public void resetData() {
        //null out all the data
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
