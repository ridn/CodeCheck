/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import cc.data.CodeCheckProjectData;
import cc.startup.CodeCheckWelcomeView;
import cc.workspace.CodeCheckWorkspaceView;
import djf.AppTemplate;
import java.util.Locale;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckApp extends AppTemplate {
    CodeCheckWelcomeView appWelcomeComponent;
    private Stage appStage;
    private Stage welcomeStage;
    
      @Override
    public void buildAppComponentsHook() {
        // CONSTRUCT ALL FOUR COMPONENTS. NOTE THAT FOR THIS APP
        // THE WORKSPACE NEEDS THE DATA COMPONENT TO EXIST ALREADY
        // WHEN IT IS CONSTRUCTED, SO BE CAREFUL OF THE ORDER
        
        //dataComponent = new SlideshowCreatorData(this);
        
        workspaceComponent = new CodeCheckWorkspaceView(this);
        //fileComponent = new SlideshowCreatorFiles(this);

    }
    
    /**
     * This is where program execution begins. Since this is a JavaFX app it
     * will simply call launch, which gets JavaFX rolling, resulting in sending
     * the properly initialized Stage (i.e. window) to the start method inherited
     * from AppTemplate, defined in the Desktop Java Framework.
     */
    public static void main(String[] args) {
	Locale.setDefault(Locale.US);
	launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        appStage = primaryStage;
        welcomeStage = new Stage();
        launchWelcomeView(welcomeStage);

    }
    public void launchWelcomeView(Stage stage) {
        //CodeCheckProjectData data = (SlideshowCreatorData)app.getDataComponent();
        //viewer.start(app.getGUI().getWindow());
        appWelcomeComponent = new CodeCheckWelcomeView(this);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(appWelcomeComponent));
        stage.show();

        //super.start(primaryStage);
    }
    public void handleWelcomeViewResponse(CodeCheckProjectData data) {
        welcomeStage.close();
        setDataComponent(data);
        super.start(appStage);

        
    }
    protected void setDataComponent(CodeCheckProjectData data) {
        dataComponent = data;
    }
    public Stage getWelcomeStage (){
        return welcomeStage;
    }
    
}
