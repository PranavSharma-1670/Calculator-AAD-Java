package com.example.calculatorforsubmission;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.*;
import java.util.Stack;
import javax.xml.xpath.XPathExpression;

public class MainActivity extends AppCompatActivity {
    private TextView screen;
    private String display="";
    private EditText inputtext;
    private TextView displaytext;
    private String currentOperator="";
    private double result;
    private Boolean use = false;

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

        Button deletevar = (Button) findViewById(R.id.Delete);
        deletevar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                deletenumber();
            }
        });

        screen = (TextView) findViewById(R.id.input_box);
        screen.setText(display);
        inputtext = findViewById(R.id.input_box);
        displaytext = findViewById(R.id.result_box);
    }

    private void appendToLast(String str) {
        this.inputtext.getText().append(str);
    }

    public void onClickNumber(View v) {
        if (use == true){
            String text = displaytext.getText().toString();
            inputtext.setText(text);
            displaytext.setText("");
            use = false;
        }
        Button b = (Button) v;
        display += b.getText();
        appendToLast(display);
        display = "";
    }

    public void onClickOperator(View v) {
        if (use == true){
            String text = displaytext.getText().toString();
            inputtext.setText(text);
            displaytext.setText("");
            use = false;
        }
        Button b = (Button) v;
        if (b.getText().equals("÷")){
            display += "/";
        }
        else{display += b.getText();}
        int n = b.getText().length();
        char last = b.getText().charAt(n-1);

        if (endsWithOperator()) {
            replace(display);
        }
        else {
            appendToLast(display);
        }
        currentOperator = b.getText().toString();
        display = "";
        }

    public void onClearButton(View v) {
        inputtext.getText().clear();
        displaytext.setText("");
    }

    public void deletenumber(){
        this.inputtext.getText().delete(getinput().length() - 1, getinput().length());
    }

    private String getinput() {
        return this.inputtext.getText().toString();
    }

    private boolean endsWithOperator() {
        return getinput().endsWith("+") ||
                getinput().endsWith("-") ||
                getinput().endsWith("x") ||
                getinput().endsWith("/");
    }

    private void replace(String str) {
        inputtext.getText().replace(getinput().length() - 1, getinput().length(), str);
    }

    public static double
    evaluateExpression(String expression)
    {
        char[] tokens = expression.toCharArray();

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;

            if ((tokens[i] >= '0' && tokens[i] <= '9')
                    || tokens[i] == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < tokens.length
                        && (Character.isDigit(tokens[i])
                        || tokens[i] == '.')) {
                    sb.append(tokens[i]);
                    i++;
                }

                values.push(
                        Double.parseDouble(sb.toString()));
                i--;
            }
            else if (tokens[i] == '(') {

                operators.push(tokens[i]);
            }
            else if (tokens[i] == ')') {

                while (operators.peek() != '(') {
                    values.push(applyOperator(
                            operators.pop(), values.pop(),
                            values.pop()));
                }
                operators.pop(); // Pop the '('
            }
            else if (tokens[i] == '+' || tokens[i] == '-'
                    || tokens[i] == '*'
                    || tokens[i] == '/') {

                while (!operators.isEmpty()
                        && hasPrecedence(tokens[i],
                        operators.peek())) {
                    values.push(applyOperator(
                            operators.pop(), values.pop(),
                            values.pop()));
                }

                operators.push(tokens[i]);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(),
                    values.pop(),
                    values.pop()));
        }

        return values.pop();
    }

    private static boolean hasPrecedence(char operator1,
                                         char operator2)
    {
        if (operator2 == '(' || operator2 == ')')
            return false;
        return (operator1 != '*' && operator1 != '/')
                || (operator2 != '+' && operator2 != '-');
    }

    private static double applyOperator(char operator,
                                        double b, double a)
    {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0)
                    throw new ArithmeticException(
                            "Cannot divide by zero");
                return a / b;
        }
        return 0;
    }

    public void equalresult(View v) {
        String input = getinput();
        if (!endsWithOperator()) {
            if (input.contains("x")) {
                input = input.replaceAll("x", "*");
            }

            result = evaluateExpression(input);
            System.out.println("Result: " + result);

            displaytext.setText(String.valueOf(result));
            use = true;
        }
        else {
            displaytext.setText("");
        }

    }
}