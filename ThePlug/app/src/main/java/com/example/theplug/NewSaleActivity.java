package com.example.theplug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.example.theplug.MainActivity.storedUsername;

public class NewSaleActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    EditText editName, editPrice, editType, editDesc, editLength;
    Button putforSale, putforBid;
    ImageButton takePic;
    ImageView prodView;
    int latestID = 0;
    private Uri prodImage;
    private static final int GalleryPick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            setTheme(R.style.lightTheme);
        } else {
            setTheme(R.style.darkTheme);
        }
        setContentView(R.layout.activity_new_sale);

        init();

        //CALL A PHP SCRIPT TO GET THE ID OF THE 4 MOST RECENT PRODUCT

        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 500);
            }
        });

        putforSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodUpLoader();
            }
        });

        putforBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hrs = editLength.getText().toString();
                if (hrs.equals("1") || hrs.equals("2") || hrs.equals("3") || hrs.equals("4") || hrs.equals("5") || hrs.equals("6")) {
                    bidUpLoader(hrs);
                } else {
                    Toast bad = Toast.makeText(getApplicationContext(), "Please enter a valid time", Toast.LENGTH_SHORT);
                    bad.show();
                }
            }
        });
    }

    // Allows user to choose image from gallery regardless of device after clicking image
    public void upLoader(View v) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    public void init() {
        editName = (EditText) findViewById(R.id.editText4);
        editPrice = (EditText) findViewById(R.id.editText2);
        editLength = (EditText) findViewById(R.id.editText3);
        editType = (EditText) findViewById(R.id.editText6);
        editDesc = (EditText) findViewById(R.id.editText);
        prodView = (ImageView) findViewById(R.id.imageView7);
        putforSale = (Button) findViewById(R.id.button3);
        putforBid = (Button) findViewById(R.id.button4);
        takePic = (ImageButton) findViewById(R.id.camButton);

        class getLatestID extends AsyncTask<String, Void, String> {

            URL url = null;
            String[] ids;

            @Override
            protected String doInBackground(String... strings) {
                String result = "0";
                try {
                    url = new URL("https://www-student.cse.buffalo.edu/CSE442-542/2020-spring/cse-442ac/getRecentID.php");
                    HttpURLConnection httpCon;
                    httpCon = (HttpURLConnection) url.openConnection();
                    httpCon.setRequestMethod("GET");

                    InputStream inStr = httpCon.getInputStream();
                    BufferedReader buffR = new BufferedReader(new InputStreamReader(inStr, "iso-8859-1"));
                    String line = "";
                    while ((line = buffR.readLine()) != null) {
                        result += line;
                    }
                    buffR.close();
                    inStr.close();
                    httpCon.disconnect();
                    ids = result.split("\\|");
                    latestID = Integer.parseInt(ids[0]);
                    Log.d("latestID:", Integer.toString(latestID));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }

        getLatestID getID = new getLatestID();
        getID.execute();

    }

    public void prodUpLoader() {
        String name = editName.getText().toString();
        String price = editPrice.getText().toString();
        String type = editType.getText().toString();
        String desc = editDesc.getText().toString();
        String id = Integer.toString(latestID + 1);
        String selltype = "0";

        BitmapDrawable bmd = (BitmapDrawable) prodView.getDrawable();
        Bitmap itemImg = bmd.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        itemImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        String userName = MainActivity.storedUsername;

        NewProductActivity npa = new NewProductActivity(this);
        npa.execute("upload", name, type, price, desc, id, selltype, encImage, userName);
    }

    public void bidUpLoader(String h) {
        String name = editName.getText().toString();
        String price = editPrice.getText().toString();
        String type = editType.getText().toString();
        String desc = editDesc.getText().toString();
        String id = Integer.toString(latestID + 1);
        String selltype = "1";
        String len = h;

        BitmapDrawable bmd = (BitmapDrawable) prodView.getDrawable();
        Bitmap itemImg = bmd.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        itemImg.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        String userName = MainActivity.storedUsername;

        NewProductActivity npa = new NewProductActivity(this);
        npa.execute("uploadBid", name, type, price, desc, id, selltype, encImage, userName, len);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK) {
            prodImage = data.getData();
            prodView.setImageURI(prodImage);
        } else if (requestCode == 500) {
            Bitmap bm = (Bitmap) data.getExtras().get("data");
            prodView.setImageBitmap(bm);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem page) {
        if (page.getItemId() == R.id.accountButton) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if (page.getItemId() == R.id.messageButton) {
            Intent intent = new Intent(this, MessagesActivity.class);
            startActivity(intent);
            return true;
        }
        if (page.getItemId() == R.id.transaction_historyButton) {
            Intent intent = new Intent(this, TransactionsActivity.class);
            startActivity(intent);
            return true;
        }
        if (page.getItemId() == R.id.settingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (page.getItemId() == R.id.newSaleButton) {
            Intent intent = new Intent(this, NewSaleActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


}
