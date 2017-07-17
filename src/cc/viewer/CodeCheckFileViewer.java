/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.viewer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
/**
 *
 * @author danniyazov
 */
public class CodeCheckFileViewer {
    Path path;
    Stage primaryStage;
    String activeDir = "/\\";
    
    public CodeCheckFileViewer(Stage stage,Path initPath){
        path = initPath;
        primaryStage = stage;
        if(Files.isDirectory(path))
            enumerateDirectory();
        else if(path.getFileName().toString().endsWith(".zip"))
            enumerateArchive();
    }
    private void enumerateDirectory() {
        try {
            Files.walk(path).forEach(file ->{
                int depth = file.getNameCount()-path.getNameCount()+1;
                depth *= 3;
                String str= String.format("%"+depth+"s",File.separator) + file.getFileName();
                System.out.println(str);

            });
        } catch (IOException ex) {
            Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void enumerateArchive() {
        try {
            ZipFile zip = new ZipFile(path.toString());
            
            zip.getFileHeaders().forEach( (e) -> {
                /*Path filePath = Paths.get(((FileHeader)e).getFileName());
                try {
                    if(!Files.isHidden(filePath))
                    {
                        
                        int depth = filePath.getNameCount();
                        depth *= 3;
                        String str = String.format("%"+depth+"s",File.separator) + filePath.getFileName();
                        System.out.println(str);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                System.out.println(((FileHeader)e).getFileName());
            });
            /*zip.stream().forEach(file->{                

                try {
                    Path filePath = Paths.get(file.getName());
                    if(!Files.isHidden(filePath))
                    {
                        int depth = filePath.getNameCount()-path.getNameCount()+1;
                        depth *= 3;
                        String str= String.format("%"+depth+"s"," ") + "/" + filePath.getFileName();
                        System.out.println(str);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });*/
        } catch (ZipException ex) {
            Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
