package com.project.ide;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class EditorPane {
    private final VBox layout = new VBox();
    private final CodeArea codeArea = new CodeArea();

    private final Stage primaryStage;
    private ConsolePane console;
    private File currentFile = null;

    // private final TextField searchField = new TextField();
    // private final TextField replaceField = new TextField();


    public EditorPane(Stage stage) {
        this.primaryStage = stage;

        codeArea.setPrefHeight(500);
        codeArea.setParagraphGraphicFactory(org.fxmisc.richtext.LineNumberFactory.get(codeArea));

        codeArea.textProperty().addListener((obs, oldText, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });


        codeArea.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case ENTER -> handleAutoIndent();
                default -> {}
            }
        });

        layout.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S -> {
                        saveFile();
                        event.consume();
                    }
                    case O -> {
                        openFile();
                        event.consume();
                    }
                    case R -> {
                        runCode();
                        event.consume();
                    }
                    
                }
            }
        });        


        codeArea.replaceText("""
        public class Main {
            public static void main(String[] args) {
                System.out.println("Hello World!");
            }
        }
        """);

        codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText()));

        try {
            codeArea.getStyleClass().add("code-area");
        
        } catch (Exception e) {
            System.err.println("Failed to load syntax.css: " + e.getMessage());
        }

        codeArea.setParagraphGraphicFactory(line -> {
            var lineNumber = new javafx.scene.control.Label(String.valueOf(line + 1));
            lineNumber.getStyleClass().add("line-number");
            return lineNumber;
        });

        // Label searchLabel = new Label("Find:");
        // Label replaceLabel = new Label("Replace:");
        // Button replaceBtn = new Button("Replace All");

        // replaceBtn.setOnAction(e -> findAndReplace());

        // HBox searchBar = new HBox(10, searchLabel, searchField, replaceLabel, replaceField, replaceBtn);

        layout.getChildren().addAll(codeArea);   

        // searchField.textProperty().addListener((obs, oldText, newText) -> {
        //     highlightMatches(newText);
        // });
        
        // replaceField.textProperty().addListener((obs, oldText, newText) -> {
        //     highlightMatches(searchField.getText());
        // });
        

    }

    protected void runCode() {
        try {
            console.print("");
    
            String code = codeArea.getText();
            File tempFile = new File("Main.java");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(code);
            }
    
            Process compile = new ProcessBuilder("javac", "Main.java").start();
            BufferedReader compileErrors = new BufferedReader(new InputStreamReader(compile.getErrorStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = compileErrors.readLine()) != null) {
                output.append("Compile time Error: ").append(line).append("\n");
            }
    
            int compileExitCode = compile.waitFor();
            if (compileExitCode != 0) {
                console.print(output.toString());
                return;
            }
    
            Process run = new ProcessBuilder("java", "-cp", ".", "Main").start();
            BufferedReader runOut = new BufferedReader(new InputStreamReader(run.getInputStream()));
            BufferedReader runErr = new BufferedReader(new InputStreamReader(run.getErrorStream()));
    
            while ((line = runOut.readLine()) != null) {
                output.append(line).append("\n");
            }
            while ((line = runErr.readLine()) != null) {
                output.append("Runtime Error: ").append(line).append("\n");
            }
    
            console.print(output.toString());
    
        } catch (Exception e) {
            console.print("Exception: " + e.getMessage());
        }
    }
    

    protected void saveFile() {
        if (currentFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));
            currentFile = fileChooser.showSaveDialog(primaryStage);
        }

        if (currentFile != null) {
            try (FileWriter writer = new FileWriter(currentFile)) {
                writer.write(codeArea.getText());
            } catch (IOException e) {
                console.print("Failed to save file: " + e.getMessage());
            }
        }
    }

    protected void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Java Files", "*.java"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                codeArea.replaceText(content.toString());
                currentFile = file;
            } catch (IOException e) {
                console.print("Failed to open file: " + e.getMessage());
            }
        }
    }

    protected void openFolder(EditorTabPane editorTabs) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Folder");
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
    
        if (selectedDirectory != null && selectedDirectory.isDirectory()) {
            List<File> allFiles = new ArrayList<>();
            collectAllFiles(selectedDirectory, allFiles);
    
            for (File file : allFiles) {
                editorTabs.openFileInTab(file);
            }
        }
    }

    private void collectAllFiles(File dir, List<File> filesList) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    collectAllFiles(file, filesList);
                } else {
                    filesList.add(file);
                }
            }
        }
    }
 
    
    public void setConsole(ConsolePane console) {
        this.console = console;
    }

    public Node getNode() {
        return layout;
    }

    public void loadFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            codeArea.replaceText(content.toString());
            this.currentFile = file;
        } catch (IOException e) {
            console.print("Failed to open file: " + e.getMessage());
        }
    }


    private static final String[] KEYWORDS = new String[] {
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
        "volatile", "while", "System"
    };
    
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*|/\\*(.|\\R)*?\\*/";
    
    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
      + "|(?<STRING>" + STRING_PATTERN + ")"
      + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );
    
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                matcher.group("STRING") != null ? "string" :
                matcher.group("COMMENT") != null ? "comment" :
                "plain-text";
    
            spansBuilder.add(Collections.singleton("plain-text"), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("plain-text"), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void clearEditor() {
        codeArea.clear();
    }
    
    private void handleAutoIndent() {
        int caretPos = codeArea.getCaretPosition();
        int currentParagraph = codeArea.getCurrentParagraph();
        String currentLine = codeArea.getParagraph(currentParagraph - 1).getText();
    
        String indent = getIndentFromLine(currentLine);
    
        
        if (currentLine.trim().endsWith("{")) {
            indent += "    ";
        }
    
        codeArea.insertText(caretPos, indent);
    }
    
    private String getIndentFromLine(String line) {
        StringBuilder indent = new StringBuilder();
        for (char c : line.toCharArray()) {
            if (c == ' ' || c == '\t') {
                indent.append(c);
            } else {
                break;
            }
        }
        return indent.toString();
    }

    // private void findAndReplace() {
    //     String searchText = searchField.getText();
    //     String replacement = replaceField.getText();
    
    //     if (searchText.isEmpty()) return;
    
    //     Platform.runLater(() -> {
    //         String text = codeArea.getText();
    //         if (!text.contains(searchText)) return;
    
    //         String updated = text.replace(searchText, replacement);
    //         codeArea.replaceText(updated);
    
    //         Platform.runLater(() -> highlightMatches(replacement));
    //     });
    // }
    


    // private void highlightMatches(String searchText) {
    //     String text = codeArea.getText();
    //     StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
    
    //     int lastIndex = 0;
    //     int index;
    //     while ((index = text.indexOf(searchText, lastIndex)) >= 0) {
    //         spansBuilder.add(Collections.singleton("plain-text"), index - lastIndex);
    //         spansBuilder.add(Collections.singleton("search-highlight"), searchText.length());
    //         lastIndex = index + searchText.length();
    //     }
    
    //     spansBuilder.add(Collections.singleton("plain-text"), text.length() - lastIndex);
    //     codeArea.setStyleSpans(0, spansBuilder.create());
    // }
  
    
}
