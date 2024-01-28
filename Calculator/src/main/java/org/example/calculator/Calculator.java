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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Set;

public class Calculator extends Application {

    private TextField display;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Calculator");

        Button[][] buttons = new Button[4][5];
        String[][] buttonLabels = {
                {"7", "8", "9", "←", "AC"},
                {"4", "5", "6", "x", "/"},
                {"1", "2", "3", "-", "+"},
                {"0", ".", "√", "²", "="}
        };


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 13, 10, 13));
        gridPane.setVgap(6);
        gridPane.setHgap(6);
        gridPane.getStyleClass().add("grid-pane");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(35);
        for (int i = 0; i < 5; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        display = new TextField();
        display.setEditable(false);
        display.setMinSize(260, 30);
        display.setMaxSize(Double.MAX_VALUE, 80);
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

        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 5; j++) {
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

        Scene scene = new Scene(gridPane, 285, 340);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/org/example/calculator/styles.css")
        ).toExternalForm());
        primaryStage.setScene(scene);

        display.getStyleClass().add("calculator-display");

        for (Button[] buttonRow : buttons) {
            for (Button button : buttonRow) {
                if (button != null && button != buttons[0][3] && button != buttons[0][4]) {
                    button.getStyleClass().add("calculator-button");
                } else if (button == buttons[0][4]) {
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
        display.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                calculateResult();
                event.consume(); // Consume the event to prevent it from being processed further
            } else {
                handleKeyPressed(event);
            }
        });


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
        } else if ("AC".equals(value)) {
            clearDisplay();
        } else if ("←".equals(value)) {
            handleBackspaceButtonClick();
        } else {
            display.appendText(value);
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        System.out.println("Key Pressed: " + event.getCode());

        String keyText = event.getText();

        if (!keyText.isEmpty() && isValidKey(keyText)) {
            if (event.isShiftDown()) {
                handleShiftedKey(event);
            } else {
                handleButtonClick(keyText);
            }
        } else {
            switch (event.getCode()) {
                case ENTER:
                    calculateResult();
                    break;
                case BACK_SPACE:
                    handleBackspaceButtonClick();
                    break;
                // Add more cases as needed for other special keys
            }
        }
    }

    private boolean isValidKey(String keyText) {
        // Define the set of valid button texts
        Set<String> validButtons = Set.of(
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "+", "-", "x", "/", ".", "=", "√", "²", "AC", "←"
        );

        return validButtons.contains(keyText);
    }

    private void calculateResult() {
        try {
            String expression = display.getText();

            // Replace "x" with "*"
            expression = expression.replace("x", "*");

            double result;

            if (expression.contains("√")) {
                result = calculateSquareRoot(expression);
            } else if (expression.contains("²")) {
                result = calculateSquare(expression);
            } else {
                result = eval(expression);
            }

            // Round the result to three decimal places
            result = Math.round(result * 1000.0) / 1000.0;

            display.setText(String.valueOf(result));
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

    private void handleShiftedKey(KeyEvent event) {
        if (event.getCode() == KeyCode.DIGIT7 && event.isShiftDown()) {
            display.appendText("/");
        } else if (event.getCode() == KeyCode.EQUALS && event.isShiftDown()) {
            display.appendText("+");
        }
    }


    private void clearDisplay() {
        Platform.runLater(() -> display.setText(""));
    }

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