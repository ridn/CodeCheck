/*
 * Author: Dan Niyazov 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cc.startup;

import cc.CodeCheckApp;
import static cc.CodeCheckProp.APP_TM_TEXT;
import static cc.CodeCheckProp.APP_VERSION;
import static cc.CodeCheckProp.NEW_DIALOG_TITLE_TEXT;
import static cc.CodeCheckProp.WELCOME_TO_TEXT;
import static cc.CodeCheckProp.WELCOME_VIEW_RECENTS_HEADER_TEXT;
import cc.filestore.CodeCheckFileStore;
import static cc.style.CodeCheckStyle.WELCOME_CLOSE_BUTTON;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_MAC;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_ROUND;
import static cc.style.CodeCheckStyle.WELCOME_PANEL_WIN;
import static cc.style.CodeCheckStyle.WELCOME_TITLE_BAR_MAC;
import static cc.style.CodeCheckStyle.WELCOME_TITLE_BAR_WIN;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_BANNER;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_BANNER_TITLE;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_PANEL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_CELL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_CELL_HOVER;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_HEADER;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL_MAC;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_RECENTS_PANEL_WIN;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_TM;
import static cc.style.CodeCheckStyle.WELCOME_VIEW_VERSION;
import static djf.settings.AppPropertyType.APP_CSS;
import static djf.settings.AppPropertyType.APP_PATH_CSS;
import static djf.settings.AppPropertyType.APP_TITLE;
import static djf.settings.AppPropertyType.WORK_FILE_EXT;
import static djf.settings.AppStartupConstants.APP_PROPERTIES_FILE_NAME;
import java.net.URL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import properties_manager.PropertiesManager;

/**
 *
 * @author danniyazov
 */
public class CodeCheckWelcomeView extends BorderPane {
    public static String OS = System.getProperty("os.name").toLowerCase();
    private static double xOrigin = 0;
    private static double yOrigin = 0;

    Stage primaryStage;
    private CodeCheckWelcomeViewController controller;
    private CodeCheckApp app;
    private VBox recentsPanel,welcomePanel;
    private Button closeWindowButton;
    private HBox titleBar;

    Label recentsHeaderLabel;
    Button newCodeCheckButton;
    Button recentCheckButtons[];
    Text welcomeText,titleText, trademarkText, versionText;
    
   public CodeCheckWelcomeView(CodeCheckApp currentApp){
        //showData = data;
        //primaryStage = stage;
        app = currentApp;
        
        
        //String title = (data.getShowTitle() == null || data.getShowTitle().isEmpty()) ? props.getProperty(UNTITLED_SHOW_TEXT) : data.getShowTitle(); 
        //primaryStage.setTitle(title);
        initWindow();
        initLayout();
        initControllers();        
    }
   private void initWindow() {
        primaryStage = app.getWelcomeStage();
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        //primaryStage.setX(bounds.getMinX());
        //primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth()/2);
        primaryStage.setHeight(bounds.getHeight()/2);
   }
    private void initLayout() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Rectangle rect = new Rectangle(primaryStage.getWidth(),primaryStage.getHeight());
        rect.setArcHeight(10.0);
        rect.setArcWidth(10.0);
        setClip(rect);
    
        //ADD CUSTOM 'TITLEBAR'
        closeWindowButton = new Button("x");
        closeWindowButton.setPadding(new Insets(0, 10, 5, 10));
        titleBar = new HBox(closeWindowButton);
        
        if(OS.contains("mac")){
            titleBar.setMaxWidth(300);
            titleBar.setAlignment(Pos.BASELINE_LEFT);
        }else{
            BorderPane.setAlignment(titleBar, Pos.BOTTOM_RIGHT);
            //titleBar.setMaxWidth(primaryStage.getWidth()-299);
            titleBar.setMaxWidth(300);
            titleBar.setAlignment(Pos.BASELINE_RIGHT);
            
        }
        setTop(titleBar);

        newCodeCheckButton = new Button(props.getProperty(NEW_DIALOG_TITLE_TEXT));
        newCodeCheckButton.setPadding(new Insets(5, 5, 5, 5));
        //BorderPane.setAlignment(newCodeCheckButton, Pos.BOTTOM_CENTER);

        recentsPanel = new VBox();
        recentsPanel.setSpacing(0);
        recentsPanel.setPadding(new Insets(20, 0, 0, 0));
        recentsPanel.setPrefWidth(300);

        recentsHeaderLabel = new Label(props.getProperty(WELCOME_VIEW_RECENTS_HEADER_TEXT));
        recentsHeaderLabel.setPadding(new Insets(5,50,18,20));
        recentsPanel.getChildren().add(recentsHeaderLabel);

        CodeCheckFileStore filestore = (CodeCheckFileStore)app.getFileComponent();
        int recentsCount = 0;
        for(String[] proj : filestore.getRecentsList()) {
            if(recentsCount < 5){
                recentsCount++;
                recentsPanel.getChildren().add(new ProjectCell(proj[0],proj[1]));
            }else{
                break;
            }
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        welcomeText = new Text(props.getProperty(WELCOME_TO_TEXT) + "\n"); 
        titleText = new Text(props.getProperty(APP_TITLE)); 
        trademarkText = new Text(props.getProperty(APP_TM_TEXT) + "\n");
        versionText = new Text(props.getProperty(APP_VERSION)); 
        TextFlow welcomeBanner = new TextFlow(welcomeText,titleText,trademarkText,versionText);
        
        welcomePanel = new VBox(welcomeBanner,spacer,newCodeCheckButton);
        VBox.setVgrow(welcomePanel, Priority.ALWAYS);
        welcomePanel.setAlignment(Pos.CENTER);
        VBox container = new VBox(welcomePanel);
        if(OS.contains("mac")){
            container.getStyleClass().add(WELCOME_PANEL_MAC);
        }else{
            container.getStyleClass().add(WELCOME_PANEL_WIN);            
        }
        setCenter(container);
        trademarkText.setTranslateY(-25);
        welcomeText.setTranslateX(-75);
        welcomeBanner.setTextAlignment(TextAlignment.CENTER);
        welcomeBanner.setLineSpacing(1);
        VBox.setMargin(welcomePanel, new Insets(22,22,100,22)); // optional

        setLeft(recentsPanel);
    }
   private void initControllers() {
        controller = new CodeCheckWelcomeViewController(app);

        newCodeCheckButton.setOnAction(e -> {
            controller.handleNewCodeCheckRequest();
        });
        primaryStage.sceneProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue != null){
                initStyle(newValue);
            }

        });
        closeWindowButton.setOnAction(e->{
            controller.handleCloseRequest();
        });
        setOnMousePressed(e-> {
                xOrigin = primaryStage.getX() - e.getScreenX();
                yOrigin = primaryStage.getY() - e.getScreenY();
        });
        setOnMouseDragged(e-> {
                primaryStage.setX(e.getScreenX() + xOrigin);
                primaryStage.setY(e.getScreenY() + yOrigin);
        });

   }
    private void initStyle(Scene primaryScene) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
	String stylesheet = props.getProperty(APP_PATH_CSS);
	stylesheet += props.getProperty(APP_CSS);
	URL stylesheetURL =  CodeCheckApp.class.getResource(stylesheet);
	String stylesheetPath = stylesheetURL.toExternalForm();

	primaryScene.getStylesheets().add(stylesheetPath);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryScene.setFill(Color.TRANSPARENT);
        setPadding(new Insets(7,7,7,7));
        //primaryScene.getRoot().setEffect(new DropShadow());
        
        if(OS.contains("mac")){
            //getStyleClass().add(WELCOME_PANEL_MAC);
            titleBar.getStyleClass().add(WELCOME_TITLE_BAR_MAC);
            recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL_MAC);
        }else{
            //getStyleClass().add(WELCOME_PANEL_WIN);
            titleBar.getStyleClass().add(WELCOME_TITLE_BAR_WIN);
            recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL_WIN);
            
        }
        getStyleClass().add(WELCOME_PANEL_ROUND);
        
        welcomePanel.getStyleClass().add(WELCOME_VIEW_PANEL);
        welcomeText.getStyleClass().add(WELCOME_VIEW_BANNER);
        titleText.getStyleClass().add(WELCOME_VIEW_BANNER_TITLE);
        trademarkText.getStyleClass().add(WELCOME_VIEW_TM);
        versionText.getStyleClass().add(WELCOME_VIEW_VERSION);
 
        recentsPanel.getStyleClass().add(WELCOME_VIEW_RECENTS_PANEL);
        recentsHeaderLabel.getStyleClass().add(WELCOME_VIEW_RECENTS_HEADER);
        closeWindowButton.getStyleClass().add(WELCOME_CLOSE_BUTTON);

    }
   public class ProjectCell extends TextFlow {
       Text title, path;

       public ProjectCell(String projectTitle, String projectPath) {
           title = new Text(projectTitle + "\n");
           path = new Text(projectPath.replaceFirst("[.][^.]+$", ""));
           initLayout();
           initController();
           initStyle();
       }
       private void initStyle() {
           getStyleClass().add(WELCOME_VIEW_RECENTS_CELL);
           this.setOnMouseEntered(e -> {
               getStyleClass().add(WELCOME_VIEW_RECENTS_CELL_HOVER);
           });
           this.setOnMouseExited(e -> {
               getStyleClass().remove(WELCOME_VIEW_RECENTS_CELL_HOVER);
           });
           

       }
       private void initController() {
           setOnMouseClicked(e -> {
               controller.handleRecentProjectLoadRequest(path.getText() + "." +  PropertiesManager.getPropertiesManager().getProperty(WORK_FILE_EXT));
           });
       }
       private void initLayout() {
           title.setFont(new Font(14));
           title.setFill(Color.BLACK);
           path.setFont(new Font(10)); 
           path.setFill(Color.GRAY);
           getChildren().addAll(title,path);
           setPadding(new Insets(4,10,4,15));
       }
   }
}
