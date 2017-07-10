/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc;

import cc.data.CodeCheckProjectData;
import cc.filestore.CodeCheckFileStore;
import cc.startup.CodeCheckWelcomeView;
import cc.workspace.CodeCheckWorkspaceView;
import djf.AppTemplate;
import static djf.settings.AppPropertyType.APP_TITLE;
import java.util.Locale;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
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
        
        if(dataComponent == null)
                dataComponent = new CodeCheckProjectData();
        
        workspaceComponent = new CodeCheckWorkspaceView(this);
        if(fileComponent == null)
            fileComponent = new CodeCheckFileStore(this);

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
        //WE NEED THIS NOW FOR PERSISTENT FILE STORE
        fileComponent = new CodeCheckFileStore(this);

        appWelcomeComponent = new CodeCheckWelcomeView(this);
        appWelcomeComponent.getStyleClass().add("shadow-pane");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(new Scene(appWelcomeComponent));
        stage.show();

    }
    public void handleWelcomeViewResponse(CodeCheckProjectData data) {
        if(data != null) CodeCheckWorkspaceView.activateOnLoad = true;
        welcomeStage.close();
        setDataComponent(data);
        super.start(appStage);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        appStage.setWidth(bounds.getWidth()/1.4);
        appStage.setHeight(bounds.getHeight()/1.4);
        appStage.centerOnScreen();
        appStage.setMinWidth(615);
        appStage.setMinHeight(380);
        appStage.setTitle(PropertiesManager.getPropertiesManager().getProperty(APP_TITLE) + " - " + data.getTitle());
        
    }
    private void setDataComponent(CodeCheckProjectData data) {
        dataComponent = data;
    }
    public Stage getWelcomeStage() {
        return welcomeStage;
    }
    public void updateStageTitle() {
        CodeCheckProjectData data = (CodeCheckProjectData)getDataComponent();
        appStage.setTitle(PropertiesManager.getPropertiesManager().getProperty(APP_TITLE) + " - " + data.getTitle());
    }
}
