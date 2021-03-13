package com.example.theplug;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    public static String storedUsername;

    private EditText passInput;
    private EditText emailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            setTheme(R.style.lightTheme);
        } else {
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    /**
     * Called when the user taps the Send button
     */
    public void loginAttempt(View view) {
        //check account
        passInput = (EditText) findViewById(R.id.Password);
        emailInput = (EditText) findViewById(R.id.Username);

        String passWord = passInput.getText().toString();
        String eMail = emailInput.getText().toString();

        BackgroundActivity bga = new BackgroundActivity(this);
        bga.execute("login", passWord, eMail);
    }


    public void goToForgotUser(View view) {
        Intent intent = new Intent(this, ForgotUsernameActivity.class);
        startActivity(intent);
    }

    public void goToForgotPass(View view) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    public void goToSignup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
