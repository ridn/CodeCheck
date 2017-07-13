/*
 * Author: Dan Niyazov 
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
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import java.util.Locale;
import static javafx.application.Application.launch;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

        primaryStage.setOnCloseRequest(event -> {
            //TODO: READ FROM PROPS
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Quit");
            alert.setHeaderText("Close Code Check?");
            alert.setContentText("Are you sure you wish to close this application?");

            alert.showAndWait().ifPresent(result->{
                if(result.getButtonData().isCancelButton())
                    event.consume();
            });
        });
        appStage = primaryStage;
        welcomeStage = new Stage();
        launchWelcomeView(welcomeStage);

    }
    public void launchWelcomeView(Stage stage) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        boolean success = loadProperties(APP_PROPERTIES_FILE_NAME);

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
        String title = PropertiesManager.getPropertiesManager().getProperty(APP_TITLE);
        title += (data != null) ? (" - " + data.getTitle()) : "";
        appStage.setTitle(title);
        
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
