package com.example.pd4;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int[] DIGIT_BUTTON_IDS = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
    };

    private TextView txtDisplay;
    private CalculatorEngine calculatorEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtDisplay = findViewById(R.id.txtDisplay);
        calculatorEngine = new CalculatorEngine();

        setupDigitButtons();
        setupOperationButtons();
        updateDisplay();
    }

    private void setupDigitButtons() {
        for (int buttonId : DIGIT_BUTTON_IDS) {
            Button btnDigit = findViewById(buttonId);
            btnDigit.setOnClickListener(view -> {
                calculatorEngine.enterDigit(btnDigit.getText().toString());
                updateDisplay();
            });
        }
    }

    private void setupOperationButtons() {
        setClickAction(R.id.btnDecimal, calculatorEngine::enterDecimalPoint);

        setClickAction(R.id.btnAdd,
                () -> calculatorEngine.setOperation(CalculatorEngine.Operation.ADD));
        setClickAction(R.id.btnSubtract,
                () -> calculatorEngine.setOperation(CalculatorEngine.Operation.SUBTRACT));
        setClickAction(R.id.btnMultiply,
                () -> calculatorEngine.setOperation(CalculatorEngine.Operation.MULTIPLY));
        setClickAction(R.id.btnDivide,
                () -> calculatorEngine.setOperation(CalculatorEngine.Operation.DIVIDE));
        setClickAction(R.id.btnEquals, calculatorEngine::calculateResult);

        setClickAction(R.id.btnSquareRoot, calculatorEngine::squareRoot);
        setClickAction(R.id.btnPercent, calculatorEngine::percent);
        setClickAction(R.id.btnReciprocal, calculatorEngine::reciprocal);

        setClickAction(R.id.btnNegate, calculatorEngine::toggleSign);
        setClickAction(R.id.btnBackspace, calculatorEngine::backspace);
        setClickAction(R.id.btnClearEntry, calculatorEngine::clearEntry);
        setClickAction(R.id.btnClear, calculatorEngine::clearAll);

        setClickAction(R.id.btnMemoryClear, calculatorEngine::memoryClear);
        setClickAction(R.id.btnMemoryRecall, calculatorEngine::memoryRecall);
        setClickAction(R.id.btnMemoryStore, calculatorEngine::memoryStore);
        setClickAction(R.id.btnMemoryAdd, calculatorEngine::memoryAdd);
        setClickAction(R.id.btnMemorySubtract, calculatorEngine::memorySubtract);
    }

    private void setClickAction(int buttonId, Runnable action) {
        findViewById(buttonId).setOnClickListener(view -> {
            action.run();
            updateDisplay();
        });
    }

    private void updateDisplay() {
        switch (calculatorEngine.getErrorType()) {
            case DIVIDE_BY_ZERO:
                txtDisplay.setText(R.string.error_divide_by_zero);
                break;
            case INVALID_INPUT:
                txtDisplay.setText(R.string.error_invalid_input);
                break;
            default:
                txtDisplay.setText(calculatorEngine.getDisplayText());
                break;
        }
    }
}