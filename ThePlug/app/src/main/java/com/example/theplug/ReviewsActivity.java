package com.example.theplug;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ReviewsActivity extends AppCompatActivity {

    public ImageView senderPF;
    public TextView senderUser, ratingUser;
    public Button submitReview;
    public EditText msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
        {
            setTheme(R.style.lightTheme);
        }else{
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_reviews);

        init();

        BackgroundReviewHelper sendR = new BackgroundReviewHelper();

        final Bundle extras = getIntent().getExtras();
        senderUser.setText(extras.getString("Sender"));
        sendR.execute("Image", senderUser.getText().toString());

        submitReview.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String cd = msg.getText().toString();
                if(cd.equals(null) || cd.trim().equals(""))
                {
                    Toast incorrect = Toast.makeText(getApplicationContext(), "Can't leave an empty review!", Toast.LENGTH_SHORT);
                    incorrect.show();
                }else{
                    BackgroundReviewHelper sendReview = new BackgroundReviewHelper();
                    sendReview.execute("Send", MainActivity.storedUsername, senderUser.getText().toString(), msg.getText().toString());
                }
            }
        });
    }

    public void init(){
        senderPF = findViewById(R.id.sellerPFP);
        senderUser = findViewById(R.id.textView6);
        ratingUser = findViewById(R.id.textView7);
        msg = findViewById(R.id.reviewMsg);
        submitReview = findViewById(R.id.sendReview);

    }

    class BackgroundReviewHelper extends AsyncTask<String,  Void, String>{

        Bitmap temp;

        @Override
        protected String doInBackground(String... strings) {
            String type = strings[0];
            if(type.equals("Image")){
                Bitmap img = null;
                try{
                    URL url = new URL("https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442ac/retrievePFP.php?un=" + strings[1]);
                    img = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                temp = img;
                return "Image Retrieved";
            }else if(type.equals("Send")){
                try{
                    String sender = strings[1];
                    String recip = strings[2];
                    String msg = strings[3];
                    URL url = new URL("https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442ac/sendReview.php");

                    HttpURLConnection httpCon;
                    httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setRequestMethod("POST");
                    httpCon.setDoOutput(true);
                    httpCon.setDoInput(true);
                    OutputStream outStr = httpCon.getOutputStream();
                    BufferedWriter buffW = new BufferedWriter(new OutputStreamWriter(outStr,"UTF-8"));
                    String req = URLEncoder.encode("frm", "UTF-8") + "=" + URLEncoder.encode(sender, "UTF-8")
                            + "&" + URLEncoder.encode("to", "UTF-8") + "=" + URLEncoder.encode(recip, "UTF-8")
                            + "&" + URLEncoder.encode("msg", "UTF-8") + "=" + URLEncoder.encode(msg, "UTF-8");
                    buffW.write(req);
                    buffW.flush();
                    buffW.close();
                    outStr.close();

                    InputStream inStr = httpCon.getInputStream();
                    BufferedReader buffR = new BufferedReader(new InputStreamReader(inStr, "iso-8859-1"));
                    String result = "";
                    String line = "";
                    while((line = buffR.readLine()) != null){
                        result += line;
                    }
                    buffR.close();
                    inStr.close();
                    httpCon.disconnect();
                    return result;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "Review Sent";
            }
            return "error";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            if(s.equals("Image Retrieved"))
            {
                senderPF.setImageBitmap(temp);
            }
            else if(s.equals("Review Submitted"))
            {
                Toast good = Toast.makeText(getApplicationContext(), "Review Submitted", Toast.LENGTH_SHORT);
                good.show();
                finish();
            }
        }
    }
}
