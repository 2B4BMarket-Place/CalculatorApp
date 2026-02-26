package com.example.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {
    private TextView timerText;
    private EditText codeInput;
    private Button submitButton;
    private String correctCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        timerText = findViewById(R.id.timerText);
        codeInput = findViewById(R.id.codeInput);
        submitButton = findViewById(R.id.submitButton);
        correctCode = getIntent().getStringExtra("code");

        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                timerText.setText("Осталось: " + millisUntilFinished / 1000 + " сек");
            }
            public void onFinish() {
                timerText.setText("Время вышло!");
                codeInput.setEnabled(false);
                submitButton.setEnabled(false);
                // Можно закрыть активность
            }
        }.start();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = codeInput.getText().toString();
                if (input.equals(correctCode)) {
                    Toast.makeText(TimerActivity.this, "Код верен", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(TimerActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
