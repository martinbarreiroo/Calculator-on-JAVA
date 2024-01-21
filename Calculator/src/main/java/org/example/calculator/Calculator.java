package org.example.calculator;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Calculator extends Application {

    private TextField display;
    private boolean enterPressed = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        // Create buttons for digits and operations
        Button[][] buttons = new Button[5][4];
        String[][] buttonLabels = {
                {"7", "8", "9", "←"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
                {"√", "²", "/", "AC"}
        };

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.getStyleClass().add("grid-pane");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(25);
        for (int i = 0; i < 4; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        display = new TextField();
        display.setEditable(false);
        display.setMinSize(200, 30);
        display.setMaxSize(Double.MAX_VALUE, 100);
        GridPane.setColumnSpan(display, 4);
        GridPane.setVgrow(display, Priority.SOMETIMES);
        gridPane.add(display, 0, 0);

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double fontSize = newVal.doubleValue() / 15;
            display.setStyle("-fx-font-size: " + fontSize + "px;");
            gridPane.setPrefWidth(newVal.doubleValue());
            display.setPrefWidth(newVal.doubleValue() - 20);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double fontSize = newVal.doubleValue() / 15;
            display.setStyle("-fx-font-size: " + fontSize + "px;");
            gridPane.setPrefHeight(newVal.doubleValue());
            display.setPrefHeight(newVal.doubleValue() - 40);
        });

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new Button(buttonLabels[i][j]);
                buttons[i][j].setMinSize(50, 50);
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setHgrow(buttons[i][j], Priority.ALWAYS);
                GridPane.setVgrow(buttons[i][j], Priority.ALWAYS);
                GridPane.setFillWidth(buttons[i][j], true);
                GridPane.setFillHeight(buttons[i][j], true);
                GridPane.setHalignment(buttons[i][j], HPos.CENTER);
                GridPane.setValignment(buttons[i][j], VPos.CENTER);
                gridPane.add(buttons[i][j], j, i + 1);

                FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), buttons[i][j]);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.play();
            }
        }

        setButtonActions(buttons);

        Scene scene = new Scene(gridPane, 250, 350);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/org/example/calculator/styles.css")
        ).toExternalForm());
        primaryStage.setScene(scene);

        display.getStyleClass().add("calculator-display");

        for (Button[] buttonRow : buttons) {
            for (Button button : buttonRow) {
                if (button != null && button != buttons[4][3] && button != buttons[0][3]) {
                    button.getStyleClass().add("calculator-button");
                } else if (button == buttons[4][3]) {
                    button.getStyleClass().add("AC-button");
                } else if (button == buttons[0][3]) {
                    button.getStyleClass().add("backspace-button");
                }
            }
        }

        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            gridPane.setPrefWidth(newVal.doubleValue());
            display.setPrefWidth(newVal.doubleValue() - 20);
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            gridPane.setPrefHeight(newVal.doubleValue());
            display.setPrefHeight(newVal.doubleValue() - 40);
        });

        // Add keyboard input handling
        display.setOnKeyPressed(this::handleKeyPressed);

        primaryStage.show();

        display.requestFocus();
    }

    private void setButtonActions(Button[][] buttons) {
        for (Button[] buttonRow : buttons) {
            for (Button button : buttonRow) {
                if (button != null) {
                    button.setOnAction(e -> handleButtonClick(button.getText()));
                }
            }
        }
    }

    private void handleButtonClick(String value) {
        if ("=".equals(value)) {
            calculateResult();
            enterPressed = false; // Reset the flag when the "=" button is pressed
        } else if ("AC".equals(value)) {
            clearDisplay();
        } else if ("←".equals(value)) {
            handleBackspaceButtonClick();
        } else {
            display.appendText(value);
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        String keyText = event.getText();

        if (!keyText.isEmpty()) {
            if (event.isShiftDown()) {
                handleShiftedKey(keyText);
            } else {
                handleButtonClick(keyText);
            }
            enterPressed = false; // Reset the flag when any other key is pressed
        } else {
            switch (event.getCode()) {
                case ENTER:
                    if (!enterPressed) {
                        calculateResult();
                        enterPressed = true; // Set the flag to true after the calculation
                    }
                    break;
                case BACK_SPACE:
                    handleBackspaceButtonClick();
                    break;
                // Add more cases as needed for other special keys
            }
        }
    }

    private void calculateResult() {
        try {
            String expression = display.getText();
            if (expression.contains("√")) {
                double result = calculateSquareRoot(expression);
                display.setText(String.valueOf(result));
            } else if (expression.contains("²")) {
                double result = calculateSquare(expression);
                display.setText(String.valueOf(result));
            } else {
                double result = eval(expression);
                display.setText(String.valueOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            display.setText("Error");
        }
    }

    private double calculateSquareRoot(String expression) {
        String expressionWithoutSqrt = expression.replace("√", "");
        double operand = eval(expressionWithoutSqrt);
        return Math.sqrt(operand);
    }

    private double calculateSquare(String expression) {
        String expressionWithoutSq = expression.replace("²", "");
        double operand = eval(expressionWithoutSq);
        return Math.pow(operand, 2);
    }

    private void handleBackspaceButtonClick() {
        String currentText = display.getText();
        if (!currentText.isEmpty()) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void handleShiftedKey(String keyText) {
        // Handle SHIFT + key combinations
        switch (keyText) {
            case "7":
                display.appendText("/");
                break;

            case "+":

                display.appendText("*");
                break;
            case "0":

                display.appendText("=");
                break;
        }
    }

    private void clearDisplay() {
        Platform.runLater(() -> display.setText(""));
    }

    // Helper method to evaluate mathematical expressions
    // Helper method to evaluate mathematical expressions
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        launch(args);
    }
}