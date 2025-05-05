package com.project.ide;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeCell;
import javafx.scene.layout.StackPane;

import java.io.File;

public class FileExplorerPane {
    private final TreeView<File> treeView;

    public FileExplorerPane(File rootDirectory, EditorTabPane editorTabs) {
        TreeItem<File> rootItem = createNode(rootDirectory);
        treeView = new TreeView<>(rootItem);
        treeView.getStyleClass().add("file-explorer");

        treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? "" : item.getName());
            }
        });

        treeView.setOnMouseClicked(event -> {
            TreeItem<File> selected = treeView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                File file = selected.getValue();
                if (file != null && file.isFile()) {
                    editorTabs.openFileInTab(file);
                }
            }
        });
    }

    private TreeItem<File> createNode(File file) {
        TreeItem<File> item = new TreeItem<>(file);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    item.getChildren().add(createNode(child));
                }
            }
        }
        return item;
    }

    public Node getNode() {
        return new StackPane(treeView);
    }
}
