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

public class EditProfileActivity extends AppCompatActivity  {

    private EditText storyBox;
    private TextView sbCounter;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText departmentName;
    private EditText positionTitle;
    private final List<String> myProfileInfoList = new ArrayList<>();
    private Profile profileInfo;
    private ImageView profilePicture;
    private String imageString64;
    private final int REQUEST_IMAGE_GALLERY = 20;
    private List<String> loginInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        storyBox = findViewById(R.id.storyBox3);
        sbCounter = findViewById(R.id.textView3);
        username = findViewById(R.id.usernameBox3);
        password = findViewById(R.id.passwordBox3);
        firstName = findViewById(R.id.firstNameBox3);
        lastName = findViewById(R.id.lastnameBox3);
        departmentName = findViewById(R.id.departmentNameBox3);
        positionTitle = findViewById(R.id.positionTitleBox3);
        profilePicture = findViewById(R.id.profileImage3);

        Intent intent = getIntent();
        profileInfo = (Profile) intent.getSerializableExtra("profileInfo");
        loginInfo.addAll((List<String>) intent.getSerializableExtra("loginInfo"));
        activityHandler();
        editText();
        setTitle("Edit Profile");
    }

    private void activityHandler()
    {
        storyBox.setText(profileInfo.getStory());
        username.setText(profileInfo.getUserName());
        password.setText(profileInfo.getPassword());
        firstName.setText(profileInfo.getFirstName());
        lastName.setText(profileInfo.getLastName());
        departmentName.setText(profileInfo.getDepartment());
        positionTitle.setText(profileInfo.getPosition());
        textToImage(profileInfo.getImageBytes());
        toBase64(null);

        password.setFocusable(false);
        username.setFocusable(false);
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

    public void textToImage(String v) {
        if (v == null) return;

        byte[] imageBytes = Base64.decode(v, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        profilePicture.setImageBitmap(bitmap);
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
        profilePicture.setImageBitmap(selectedImage);
        toBase64(null);
    }

    public void toBase64(View v) {
        BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //int count = 0;
        //count = imageString64.length();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){ //this is the only code we have for menues
        //the menu we pass here is the actual menu we have made in layout
        //inflating means to build live objects
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()){
            case R.id.save_menu2:

                if (username.getText().toString().isEmpty() || password.getText().toString().isEmpty() ||
                        firstName.getText().toString().isEmpty() || lastName.getText().toString().isEmpty()
                        || departmentName.getText().toString().isEmpty() || positionTitle.getText().toString().isEmpty()
                        || storyBox.getText().toString().isEmpty() || imageString64.isEmpty()){
                    Toast.makeText(this, "All Fileds Must Be filled! Try Again.", Toast.LENGTH_LONG).show();
                    return false;
                }

                Profile newProfile = new Profile(firstName.getText().toString(), lastName.getText().toString(),
                        username.getText().toString(), departmentName.getText().toString(),
                        storyBox.getText().toString(), positionTitle.getText().toString(),
                        password.getText().toString(),"",
                        profileInfo.getLocation(), imageString64 , null, null);

                EditProfileRunnable EP = new  EditProfileRunnable(this, newProfile, loginInfo);
                new Thread(EP).start();
                //return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editThreadHandler(Profile result){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("result" , (Serializable) result);
        intent.putExtra("apiKey", loginInfo.get(0));
        startActivity(intent);
        //finish();

    }
}