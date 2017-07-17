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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
/**
 *
 * @author danniyazov
 */
public class CodeCheckFileViewer {
    static CodeCheckFileViewer singleton = null;

    Path path;
    Alert fileViewAlert;
    //String activeDir = "/\\";
    TextArea textArea;
    StringBuilder content;
    Alert alert;
    
    private CodeCheckFileViewer(){
        content = new StringBuilder();
        

        alert = new Alert(AlertType.INFORMATION);

        textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setPrefSize(600, 400);
        textArea.setPadding(Insets.EMPTY);

        //textArea.setMaxWidth(Double.MAX_VALUE);
        //textArea.setMaxHeight(Double.MAX_VALUE);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.getDialogPane().setExpanded(true);

    }
    public static CodeCheckFileViewer getFileViewer() {
        if(singleton == null)
            singleton = new CodeCheckFileViewer();
        return singleton;
    }
    public void viewFile(Path file){
        //TODO: READ FROM PROPS
        path = file;
        content.setLength(0);
        if(Files.isDirectory(path)){
            enumerateDirectory();
           alert.setContentText("Type: Directory");
        }
        else if(path.getFileName().toString().endsWith(".zip")){
            enumerateArchive();
            alert.setContentText("Type: Zip Archive");

        }
        textArea.setText(content.toString());
        alert.setTitle("File Viewer");
        alert.setHeaderText("Contents of " + path.getFileName().toString());
        alert.showAndWait();


    }
    private void enumerateDirectory() {
        try {
            Files.walk(path).forEach(file ->{
                try {
                    if(!Files.isHidden(file)){
                        int depth = file.getNameCount()-path.getNameCount()+1;
                        depth *= 3;
                        String str = String.format("%"+depth+"s",File.separator) + file.getFileName() + "\n";
                        content.append(str);
                        //System.out.println(str);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
                }

            });
        } catch (IOException ex) {
            Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void enumerateArchive() {
        try {
            ZipFile zip = new ZipFile(path.toString());
            
            zip.getFileHeaders().forEach( (e) -> {
                Path filePath = Paths.get(((FileHeader)e).getFileName());
                try {
                    if(!Files.isHidden(filePath))
                    {
                        
                        /*int depth = filePath.getNameCount();
                        depth *= 3;
                        String str = String.format("%"+depth+"s",File.separator) + filePath.getFileName();
                        System.out.println(str);*/
                        content.append(((FileHeader)e).getFileName());
                        content.append("\n");
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CodeCheckFileViewer.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
