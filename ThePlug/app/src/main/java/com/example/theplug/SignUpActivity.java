package com.example.theplug;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputFirstName, inputLastName, inputEmail, inputPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            setTheme(R.style.lightTheme);
        } else {
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_signup);

    }

    public void returnToMainPage(View view) {
        createAccount();
    }

    public void init() {
        inputFirstName = findViewById(R.id.firstNameEditText4);
        inputLastName = findViewById(R.id.lastNameEditText5);
        inputEmail = findViewById(R.id.emailEditText7);
        inputPass = findViewById(R.id.passwordEditText8);

    }

    public void createAccount() {
        init();

        String firstName = inputFirstName.getText().toString();
        String lastName = inputLastName.getText().toString();
        String email = inputEmail.getText().toString();
        String pass = inputPass.getText().toString();

        if (firstName.contains("|") || firstName.contains("*") || lastName.contains("|") || lastName.contains("*")
                || email.contains("|") || email.contains("*")) {
            Toast err = Toast.makeText(getApplicationContext(), "Names cannot contain illegal characters '|' or '*'", Toast.LENGTH_SHORT);
            err.show();
        } else {
            BackgroundActivity bga = new BackgroundActivity(this);
            bga.execute("signup", email, pass, firstName, lastName);
            finish();
        }
    }

}
