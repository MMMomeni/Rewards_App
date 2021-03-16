package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateProfileActivity extends AppCompatActivity {

    private EditText storyBox;
    private TextView sbCounter;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText departmentName;
    private EditText positionTitle;
    private final List<String> myProfileInfoList = new ArrayList<>();
    private ImageView profileImage;
    private final int REQUEST_IMAGE_GALLERY = 10;
    private String imageString64;
    private String apiKey;
    private String location;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        storyBox = findViewById(R.id.storyBox);
        sbCounter = findViewById(R.id.textView1);
        username = findViewById(R.id.usernameBox1);
        password = findViewById(R.id.passwordBox1);
        firstName = findViewById(R.id.firstNameBox1);
        lastName = findViewById(R.id.lastnameBox1);
        departmentName = findViewById(R.id.departmentNameBox1);
        positionTitle = findViewById(R.id.positionTitleBox1);
        profileImage = findViewById(R.id.profileImage1);

        Intent intent = getIntent();
        apiKey = intent.getStringExtra("apiKey");
        location = intent.getStringExtra("location");


        editText();

        setTitle("Create Profile");
    }

    public void doGallery(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_GALLERY);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            try {
                processGallery(data);
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        if (requestCode == 2){
            finish();
        }


    }

    private void processGallery(Intent data) {
        Uri galleryImageUri = data.getData();
        if (galleryImageUri == null)
            return;

        InputStream imageStream = null;
        try {
            imageStream = getContentResolver().openInputStream(galleryImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
        profileImage.setImageBitmap(selectedImage);
        toBase64(null);
    }

    public void toBase64(View v) {
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        int count = 0;
        count = imageString64.length();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.create_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.createProfileSaveMenu:

                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                        firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()
                || departmentName.getText().toString().isEmpty() || positionTitle.getText().toString().isEmpty()
                || storyBox.getText().toString().isEmpty() || imageString64.isEmpty() ){
                    Toast.makeText(this, "All Fileds Must Be filled! Try Again.", Toast.LENGTH_LONG).show();
                    return false;
                }

                if (location.isEmpty()){
                    Toast.makeText(this, "No Location is found, restart the app.", Toast.LENGTH_LONG).show();
                    return false;
                }
                myProfileInfoList.add(username.getText().toString());
                myProfileInfoList.add(password.getText().toString());
                myProfileInfoList.add(firstName.getText().toString());
                myProfileInfoList.add(lastName.getText().toString());
                myProfileInfoList.add(departmentName.getText().toString());
                myProfileInfoList.add(positionTitle.getText().toString());
                myProfileInfoList.add(storyBox.getText().toString());
                myProfileInfoList.add(imageString64);
                myProfileInfoList.add(location);
                CreateProfileAPIRunnable CPA = new  CreateProfileAPIRunnable(this, myProfileInfoList, apiKey);
                new Thread(CPA).start();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editText(){
        storyBox.setFilters(new InputFilter[] {
                new InputFilter.LengthFilter(200)
        });

        storyBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int len = s.toString().length() + 1;
                String countText = "Your Story: (" + len + " of " + 200 + ")";
                sbCounter.setText(countText);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void profileInfoHandler(Profile result){


        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("result" , (Serializable) result);
        intent.putExtra("apiKey", apiKey);
        startActivityForResult(intent, 2);

    }

}