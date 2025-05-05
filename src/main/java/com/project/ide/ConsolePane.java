package com.project.ide;

import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public class ConsolePane {
    private final TextArea console = new TextArea();

    public ConsolePane() {
        console.setEditable(false);
        console.setPrefHeight(120);
        console.getStyleClass().add("console-area");

    }

    public void print(String text) {
        console.setText(text);
    }

    public Node getNode() {
        return new StackPane(console);
    }
}
