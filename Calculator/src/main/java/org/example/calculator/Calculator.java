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
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Calculator extends Application {

    private TextField display;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        // Create buttons for digits and operations
        Button[][] buttons = new Button[5][4];  // Changed array size to accommodate "AC"
        String[][] buttonLabels = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", ".", "=", "+"},
                {"√", "²", "←", "AC"}  // Ensure each row has the same length
        };

        // Create a GridPane for layout
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.getStyleClass().add("grid-pane");

        // Set ColumnConstraints to make buttons resizable
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(25);
        for (int i = 0; i < 4; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        // Add the display to the layout, spanning across all columns
        display = new TextField();
        display.setEditable(false);
        display.setMinSize(200, 30);
        display.setMaxSize(Double.MAX_VALUE, 100);
        GridPane.setColumnSpan(display, 4);

// Set Vgrow property to SOMETIMES, making the display take only the required vertical space
        GridPane.setVgrow(display, Priority.SOMETIMES);

        gridPane.add(display, 0, 0);



        // Add buttons to the gridPane with fade-in animation
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                buttons[i][j] = new Button(buttonLabels[i][j]);
                buttons[i][j].setMinSize(50, 50);
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Allow buttons to be resizable
                GridPane.setHgrow(buttons[i][j], Priority.ALWAYS); // Allow buttons to grow horizontally
                GridPane.setVgrow(buttons[i][j], Priority.ALWAYS); // Allow buttons to grow vertically
                GridPane.setFillWidth(buttons[i][j], true); // Allow buttons to fill the available width
                GridPane.setFillHeight(buttons[i][j], true); // Allow buttons to fill the available height
                GridPane.setHalignment(buttons[i][j], HPos.CENTER); // Center the button horizontally
                GridPane.setValignment(buttons[i][j], VPos.CENTER); // Center the button vertically
                gridPane.add(buttons[i][j], j, i + 1);  // Increase the row index by 1

                // Create a FadeTransition for each button
                FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), buttons[i][j]);
                fadeTransition.setFromValue(0);
                fadeTransition.setToValue(1);
                fadeTransition.play();
            }
        }



        // Set actions for the buttons
        setButtonActions(buttons);

        // Set up the scene
        Scene scene = new Scene(gridPane, 250, 350);  // Adjust the scene height
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/org/example/calculator/styles.css")
        ).toExternalForm());
        primaryStage.setScene(scene);

        display.getStyleClass().add("calculator-display");

        for (Button[] buttonRow : buttons) {
            for (Button button : buttonRow) {
                if (button != null && button != buttons[4][3] && button != buttons[4][2]) {
                    button.getStyleClass().add("calculator-button");
                } else if (button == buttons[4][3]) {
                    button.getStyleClass().add("AC-button");
                } else if (button == buttons[4][2]) {
                    button.getStyleClass().add("backspace-button");
                }
            }
        }

        // Make the scene resizable
        primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
            gridPane.setPrefWidth(newVal.doubleValue());
            display.setPrefWidth(newVal.doubleValue() - 20); // Adjust for padding
        });

        primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
            gridPane.setPrefHeight(newVal.doubleValue());
            display.setPrefHeight(newVal.doubleValue() - 40); // Adjust for padding
        });

        primaryStage.show();
    }


    private void setButtonActions(Button[][] buttons) {
        for (Button[] buttonRow : buttons) {
            for (Button button : buttonRow) {
                if (button != null) {  // Check if the button is not null
                    button.setOnAction(e -> handleButtonClick(button.getText()));
                }
            }
        }
    }


    private void handleButtonClick(String value) {
        // Handle button click and update the display
        if ("=".equals(value)) {
            calculateResult();
        } else if ("AC".equals(value)) {
            clearDisplay();
        } else if ("←".equals(value)) {
            handleBackspaceButtonClick();
        }
        else {
            display.appendText(value);
        }
    }

    private void calculateResult() {
        // Calculate the result and update the display
        try {
            String expression = display.getText();
            System.out.println("Expression: " + expression); // Debug print

            // Check if the expression contains the square root symbol
            if (expression.contains("√")) {
                double result = calculateSquareRoot(expression);
                display.setText(String.valueOf(result));
            }
            else if (expression.contains("²")){
                double result =  calculateSquare(expression);
                display.setText(String.valueOf(result));
            }

            else {
                // Evaluate the expression for other operations
                double result = eval(expression);
                System.out.println("Result: " + result); // Debug print
                display.setText(String.valueOf(result));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Debug print
            display.setText("Error");
        }
    }

    private double calculateSquareRoot(String expression) {
        // Remove the square root symbol and parse the remaining expression
        String expressionWithoutSqrt = expression.replace("√", "");
        double operand = eval(expressionWithoutSqrt);

        // Calculate the square root
        return Math.sqrt(operand);
    }

    private double calculateSquare(String expression) {
        // Remove the square root symbol and parse the remaining expression
        String expressionWithoutSq = expression.replace("²", "");
        double operand = eval(expressionWithoutSq);

        // Calculate the square root
        return Math.pow(operand,2);
    }


    private void handleBackspaceButtonClick() {
        // Handle backspace button click and update the display
        String currentText = display.getText();
        if (!currentText.isEmpty()) {
            display.setText(currentText.substring(0, currentText.length() - 1));
        }
    }

    private void clearDisplay() {
        // Clear the display
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
