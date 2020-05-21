package au.edu.canberra.mtfinalassignment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;
import java.util.Date;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.ContentValues;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.android.library.camera.GalleryHelper;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;


public class ActivityThree extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int REQUEST_PERMISSION = 3000;
    private Activity activity;
    private Uri outputFileUri;
    private final String TAG = "";
    private String currentPhotoPath;
    private ImageView imageView;
    private TextView textView;
    private String temp;
    private String item;
    private Bitmap photoBitmap;
//    IBM stuff
    private VisualRecognition visualRecognition;
    private CameraHelper cameraHelper;
    private GalleryHelper galleryHelper;
    private File photoFile;
    private final String api_key = "lgFlrPmtL4HhgC7sJx8H_oitt4nSlSB3Z63HKbK3uXqP";
    String company;


    ArrayList<ClassifiedItem> ClassifiedItem = new ArrayList<ClassifiedItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        imageView = findViewById(R.id.imageView);

        if(savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null){
                company = null;
            } else {
                company = extras.getString("title");
                setTitle(company);
                if (company.equals("Google Firebase ML Cloud Services")){
                imageView.setImageDrawable(getResources()
                        .getDrawable((R.drawable.google), getTheme()));
            }
                else if(company.equals("IBM Watson ML Cloud Services")){
                    imageView.setImageDrawable(getResources()
                        .getDrawable((R.drawable.ibm), getTheme()));
                }
            }
        } else {
            company = (String) savedInstanceState.getSerializable("title");
            setTitle(company);
        }

        textView = (TextView) findViewById(R.id.textView);
        activity = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    public void capture(View view){
        if (checkPermissions() == false)
            return;
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePhotoIntent.resolveActivity(activity.getPackageManager()) != null) {
            outputFileUri = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(takePhotoIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void load(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (galleryIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
        }
    }

    private boolean checkPermissions() {
        String permissions[] = {android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean grantCamera =
                ContextCompat.checkSelfPermission(activity, permissions[0]) == PackageManager.PERMISSION_GRANTED;
        boolean grantExternal =
                ContextCompat.checkSelfPermission(activity, permissions[1]) == PackageManager.PERMISSION_GRANTED;

        if (!grantCamera && !grantExternal) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
        } else if (!grantCamera) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[0]}, REQUEST_PERMISSION);
        } else if (!grantExternal) {
            ActivityCompat.requestPermissions(activity, new String[]{permissions[1]}, REQUEST_PERMISSION);
        }

        return grantCamera && grantExternal;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        textView.setText("");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            photoBitmap = getCapturedImage();
            imageView.setImageBitmap(photoBitmap);
        }
        if (requestCode == PICK_IMAGE_REQUEST){
            photoBitmap = getBitmap(resultCode, data);
            outputFileUri=data.getData();
            imageView.setImageBitmap(photoBitmap);
        }

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(photoBitmap);

        FirebaseVisionObjectDetectorOptions options =
                new FirebaseVisionObjectDetectorOptions.Builder()
                        .setDetectorMode(FirebaseVisionObjectDetectorOptions.SINGLE_IMAGE_MODE)
                        .enableMultipleObjects()
                        .enableClassification()
                        .build();

        FirebaseVisionObjectDetector objectDetector = FirebaseVision
                .getInstance()
                .getOnDeviceObjectDetector(options);

        objectDetector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionObject>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionObject> firebaseVisionObjects) {
                                for (FirebaseVisionObject object : firebaseVisionObjects) {
                                    int category = object.getClassificationCategory();
                                    float score = 0;
                                    if (object.getClassificationConfidence() != null)
                                        score = object.getClassificationConfidence();
                                    if (category == 1) {
                                        textView.append("Home Good");
                                        item="Home Good";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    else if (category == 2) {
                                        textView.append("Detected Item: Fashion Good");
                                        item="Fashion Good";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    else if (category == 3) {
                                        textView.append("Detected Item: Food");
                                        item="Food";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    else if (category == 4) {
                                        textView.append("Detected Item: Place");
                                        item="Place";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    else if (category == 5) {
                                        textView.append("Detected Item: Plant");
                                        item="Plant";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    else{
                                        textView.append("Detected Item: Unknown");
                                        item="Unknown";
                                        temp="Detected Score: " + String.valueOf(score);
                                    }
                                    textView.append("\n");
                                }
                                String imageFileName=(item+String.valueOf(System.currentTimeMillis()).toLowerCase().replace(" ","_")+".jpg");
//                                showPhotoContext
                                Intent intent = new Intent(getApplicationContext(),ActivitySix.class);
                                String data=item;
                                String data2=textView.getText().toString()+"\n"+temp;
                                intent.putExtra("company", company);
                                intent.putExtra("uri",outputFileUri);
                                intent.putExtra("itemName",data);
                                intent.putExtra("imageFileName",imageFileName);
                                intent.putExtra("classifiedResult",data2);
                                startActivity(intent);
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        textView.setText("Failed");
                    }
                });
    }
    private Bitmap getCapturedImage() {
        Bitmap srcImage = null;
        try {
            srcImage = FirebaseVisionImage
                    .fromFilePath(getBaseContext(), outputFileUri)
                    .getBitmap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return srcImage;
    }

    public Bitmap getBitmap(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri targetUri = data.getData();
            try {
                return BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(targetUri));
            } catch (FileNotFoundException e) {
                Log.e(TAG, "File Not Found", e);
                return null;
            }
        }
        Log.e(TAG, "Result Code was not OK");
        return null;
    }

}
