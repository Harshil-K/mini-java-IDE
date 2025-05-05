package com.project.ide;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    private static final String LIGHT_THEME = "/com/project/ide/lightMode.css";
    private static final String DARK_THEME = "/com/project/ide/darkMode.css";

    private final BorderPane root = new BorderPane();

    @Override
    public void start(Stage stage) {
        ConsolePane console = new ConsolePane();
        EditorTabPane editorTabs = new EditorTabPane(stage, console);
        FileExplorerPane fileExplorer = new FileExplorerPane(new File("src"), editorTabs);


        Button newTabButton = new Button("New Tab");
        Button openFileButton = new Button("Open File");
        Button openFolderButton = new Button("Open Folder");
        Button saveButton = new Button("Save");
        Button runButton = new Button("Run");
        Button terminalButton = new Button("Terminal");
        ToggleButton themeToggle = new ToggleButton("Dark Mode");


        newTabButton.setOnAction(e -> editorTabs.addNewTab("Untitled"));
        openFileButton.setOnAction(e -> editorTabs.getCurrentEditor().openFile());
        openFolderButton.setOnAction(e -> editorTabs.getCurrentEditor().openFolder(editorTabs));
        saveButton.setOnAction(e -> editorTabs.getCurrentEditor().saveFile());
        runButton.setOnAction(e -> editorTabs.getCurrentEditor().runCode());

        terminalButton.setOnAction(e -> {
                try {
                    String projectDir = System.getProperty("user.dir");
                    new ProcessBuilder("cmd.exe", "/c", "start", "wt", "-w", "0", "nt", "-d", projectDir).start();
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        });

        themeToggle.setOnAction(e -> {
            Scene scene = stage.getScene();
            if (themeToggle.isSelected()) {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource(LIGHT_THEME)).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(DARK_THEME)).toExternalForm());
                themeToggle.setText("Light Mode");
            } else {
                scene.getStylesheets().remove(Objects.requireNonNull(getClass().getResource(DARK_THEME)).toExternalForm());
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(LIGHT_THEME)).toExternalForm());
                themeToggle.setText("Dark Mode");
            }
        });

        HBox topBar = new HBox(20, newTabButton, openFileButton, openFolderButton, saveButton, runButton, themeToggle, terminalButton);

        SplitPane verticalSplitPane = new SplitPane();
        verticalSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        verticalSplitPane.getItems().addAll(editorTabs.getNode(), console.getNode());
        verticalSplitPane.setDividerPositions(0.8);


        SplitPane horizontalSplitPane = new SplitPane();
        horizontalSplitPane.getItems().addAll(fileExplorer.getNode(), verticalSplitPane);
        horizontalSplitPane.setDividerPositions(0.25);

        root.setTop(topBar);
        root.setCenter(horizontalSplitPane);

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(LIGHT_THEME)).toExternalForm());

        scene.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case N -> {
                        editorTabs.addNewTab("Untitled");
                        event.consume();
                    }
                    case W -> {
                        event.consume();
                    }
                }
            }
        });

        stage.setScene(scene);
        stage.setTitle("Java IDE");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
