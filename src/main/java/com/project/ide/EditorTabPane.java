package com.project.ide;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class EditorTabPane {
    private final TabPane tabPane = new TabPane();
    private final Stage stage;
    private final ConsolePane console;
    private final List<EditorPane> editorPanes = new ArrayList<>();
    private final Map<String, Tab> openFileTabs = new HashMap<>();

    public EditorTabPane(Stage stage, ConsolePane console) {
        this.stage = stage;
        this.console = console;

 
        addNewTab("Untitled");
    }

    public void addNewTab(String title) {
        EditorPane editor = new EditorPane(stage);
        editor.setConsole(console);

        if (tabPane.getTabs().size() >= 1) {
            editor.clearEditor();
        }

        Tab tab = new Tab(title, editor.getNode());
        tab.setClosable(true);

        editorPanes.add(editor);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void openFileInTab(File file) {
        String filePath = file.getAbsolutePath();

        if (openFileTabs.containsKey(filePath)) {
            Tab existingTab = openFileTabs.get(filePath);
            tabPane.getSelectionModel().select(existingTab);
            return;
        }

        EditorPane editor = new EditorPane(stage);
        editor.setConsole(console);
        editor.loadFile(file);

        Tab tab = new Tab(file.getName(), editor.getNode());
        tab.setClosable(true);

        tab.setOnClosed(e -> openFileTabs.remove(filePath));

        openFileTabs.put(filePath, tab);
        editorPanes.add(editor);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public void addTab(String title, EditorPane editor) {
        Tab tab = new Tab(title, editor.getNode());
        tab.setClosable(true);
        editorPanes.add(editor);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    public Node getNode() {
        return tabPane;
    }

    public EditorPane getCurrentEditor() {
        int index = tabPane.getSelectionModel().getSelectedIndex();
        return index >= 0 ? editorPanes.get(index) : null;
    }
}
