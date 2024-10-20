// Package declaration should be first
package com.example.eqe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextView resultText;
    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        resultText = findViewById(R.id.result_text);
        scanButton = findViewById(R.id.scan_button);

        // Set click listener for the scan button
        scanButton.setOnClickListener(view -> {
            IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
            integrator.setOrientationLocked(true);  // Lock to portrait mode
            integrator.setPrompt("Scan a QR code containing a polynomial");
            integrator.initiateScan();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Get scanned content (polynomial expression)
                String scannedContent = result.getContents();
                // Parse and evaluate the polynomial
                evaluatePolynomial(scannedContent, 2);  // Example x = 2
            } else {
                Toast.makeText(this, "Scan failed. No content found!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Method to evaluate a polynomial
    private void evaluatePolynomial(String polynomial, int x) {
        try {
            // Parse the polynomial and evaluate
            double result = parseAndEvaluate(polynomial, x);
            resultText.setText("For x = " + x + ", the result is: " + result);
            Toast.makeText(this, "Result: " + result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // Handle invalid input or errors
            resultText.setText("Invalid polynomial: " + polynomial);
            Toast.makeText(this, "Error: Invalid polynomial expression!", Toast.LENGTH_LONG).show();
        }
    }

    // Method to parse and evaluate the polynomial expression
    private double parseAndEvaluate(String polynomial, int x) throws Exception {
        // Regular expression to match polynomial terms (e.g., 2x^3, -5x, +4)
        String regex = "([+-]?\\d*)x\\^?(\\d*)|([+-]?\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(polynomial.replace(" ", ""));

        double result = 0.0;

        while (matcher.find()) {
            if (matcher.group(3) != null) {
                // Constant term (e.g., +4)
                result += Double.parseDouble(matcher.group(3));
            } else {
                // Variable term (e.g., 2x^3)
                int coefficient = matcher.group(1).equals("") || matcher.group(1).equals("+") ? 1 :
                        matcher.group(1).equals("-") ? -1 : Integer.parseInt(matcher.group(1));
                int power = matcher.group(2).equals("") ? 1 : Integer.parseInt(matcher.group(2));

                result += coefficient * Math.pow(x, power);
            }
        }

        // Throw an exception if no valid polynomial terms are found
        if (result == 0.0) throw new Exception("Invalid polynomial");

        return result;
    }
}
