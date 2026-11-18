package com.example.pd4;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CalculatorEngine {

    public enum Operation { NONE, ADD, SUBTRACT, MULTIPLY, DIVIDE }

    public enum ErrorType { NONE, DIVIDE_BY_ZERO, INVALID_INPUT }

    private static final String DEFAULT_DISPLAY = "0";
    private static final String DECIMAL_POINT = ".";
    private static final String MINUS_SIGN = "-";
    private static final int MAX_INPUT_LENGTH = 15;
    private static final int RESULT_SCALE = 10;

    private String displayText = DEFAULT_DISPLAY;
    private double firstOperand = 0;
    private double memoryValue = 0;
    private Operation pendingOperation = Operation.NONE;
    private ErrorType errorType = ErrorType.NONE;
    private boolean isNewInput = true;

    public String getDisplayText() {
        return displayText;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public boolean hasError() {
        return errorType != ErrorType.NONE;
    }

    // ---- Arithmetic operations (Pascal case) ----

    public double Add(double left, double right) {
        return left + right;
    }

    public double Subtract(double left, double right) {
        return left - right;
    }

    public double Multiply(double left, double right) {
        return left * right;
    }

    public double Divide(double left, double right) {
        return left / right;
    }

    public double SquareRoot(double value) {
        return Math.sqrt(value);
    }

    // ---- Input ----

    public void enterDigit(String digit) {
        if (hasError()) {
            clearAll();
        }
        if (isNewInput) {
            displayText = digit;
            isNewInput = false;
        } else if (displayText.equals(DEFAULT_DISPLAY)) {
            displayText = digit;
        } else if (displayText.length() < MAX_INPUT_LENGTH) {
            displayText += digit;
        }
    }

    public void enterDecimalPoint() {
        if (hasError()) {
            clearAll();
        }
        if (isNewInput) {
            displayText = DEFAULT_DISPLAY + DECIMAL_POINT;
            isNewInput = false;
        } else if (!displayText.contains(DECIMAL_POINT)) {
            displayText += DECIMAL_POINT;
        }
    }

    public void backspace() {
        if (hasError() || isNewInput) {
            return;
        }
        displayText = displayText.substring(0, displayText.length() - 1);
        if (displayText.isEmpty() || displayText.equals(MINUS_SIGN)) {
            displayText = DEFAULT_DISPLAY;
        }
    }

    public void clearEntry() {
        if (hasError()) {
            clearAll();
            return;
        }
        displayText = DEFAULT_DISPLAY;
        isNewInput = true;
    }

    public void clearAll() {
        displayText = DEFAULT_DISPLAY;
        firstOperand = 0;
        pendingOperation = Operation.NONE;
        errorType = ErrorType.NONE;
        isNewInput = true;
    }

    public void toggleSign() {
        if (hasError() || displayText.equals(DEFAULT_DISPLAY)) {
            return;
        }
        if (displayText.startsWith(MINUS_SIGN)) {
            displayText = displayText.substring(1);
        } else {
            displayText = MINUS_SIGN + displayText;
        }
    }

    // ---- Operations ----

    public void setOperation(Operation operation) {
        if (hasError()) {
            return;
        }
        if (pendingOperation != Operation.NONE && isNewInput) {
            pendingOperation = operation;
            return;
        }
        double currentValue = parseDisplay();
        if (pendingOperation != Operation.NONE) {
            if (!applyPendingOperation(currentValue)) {
                return;
            }
        } else {
            firstOperand = currentValue;
        }
        pendingOperation = operation;
        isNewInput = true;
    }

    public void calculateResult() {
        if (hasError() || pendingOperation == Operation.NONE) {
            return;
        }
        if (applyPendingOperation(parseDisplay())) {
            pendingOperation = Operation.NONE;
            isNewInput = true;
        }
    }

    public void squareRoot() {
        if (hasError()) {
            return;
        }
        double value = parseDisplay();
        if (value < 0) {
            setError(ErrorType.INVALID_INPUT);
            return;
        }
        showValue(SquareRoot(value));
        isNewInput = true;
    }

    public void percent() {
        if (hasError()) {
            return;
        }
        showValue(parseDisplay() / 100);
        isNewInput = true;
    }

    public void reciprocal() {
        if (hasError()) {
            return;
        }
        double value = parseDisplay();
        if (value == 0) {
            setError(ErrorType.DIVIDE_BY_ZERO);
            return;
        }
        showValue(1 / value);
        isNewInput = true;
    }

    // ---- Memory ----

    public void memoryClear() {
        memoryValue = 0;
    }

    public void memoryRecall() {
        if (hasError()) {
            return;
        }
        showValue(memoryValue);
        isNewInput = true;
    }

    public void memoryStore() {
        if (hasError()) {
            return;
        }
        memoryValue = parseDisplay();
        isNewInput = true;
    }

    public void memoryAdd() {
        if (hasError()) {
            return;
        }
        memoryValue += parseDisplay();
        isNewInput = true;
    }

    public void memorySubtract() {
        if (hasError()) {
            return;
        }
        memoryValue -= parseDisplay();
        isNewInput = true;
    }

    // ---- Helpers ----

    private boolean applyPendingOperation(double secondOperand) {
        double result;
        switch (pendingOperation) {
            case ADD:
                result = Add(firstOperand, secondOperand);
                break;
            case SUBTRACT:
                result = Subtract(firstOperand, secondOperand);
                break;
            case MULTIPLY:
                result = Multiply(firstOperand, secondOperand);
                break;
            case DIVIDE:
                if (secondOperand == 0) {
                    setError(ErrorType.DIVIDE_BY_ZERO);
                    return false;
                }
                result = Divide(firstOperand, secondOperand);
                break;
            default:
                result = secondOperand;
                break;
        }
        firstOperand = result;
        return showValue(result);
    }

    private double parseDisplay() {
        return Double.parseDouble(displayText);
    }

    private boolean showValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            setError(ErrorType.INVALID_INPUT);
            return false;
        }
        displayText = formatNumber(value);
        return true;
    }

    private void setError(ErrorType newErrorType) {
        errorType = newErrorType;
        pendingOperation = Operation.NONE;
        firstOperand = 0;
        isNewInput = true;
    }

    private String formatNumber(double value) {
        return new BigDecimal(value)
                .setScale(RESULT_SCALE, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}