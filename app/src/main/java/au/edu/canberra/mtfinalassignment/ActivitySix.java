package au.edu.canberra.mtfinalassignment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetector;
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ActivitySix extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_IMAGE_CAPTURE = 1000;
    private static final int REQUEST_PERMISSION = 3000;
    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;
    private Bitmap photoBitmap;
    private Uri outputFileUri;
    String imageFileName;
    String classifiedResult;
    String itemName;
    String company;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);

        Intent intent = getIntent();
        itemName = intent.getStringExtra("itemName");

        classifiedResult = intent.getStringExtra("classifiedResult");
        imageFileName=intent.getStringExtra("imageFileName");

        outputFileUri=intent.getParcelableExtra("uri");
        company = intent.getStringExtra("company");
        setTitle(company);

        try {
            photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView1=findViewById(R.id.textView1);
        textView1.setText("");
        textView1.append(itemName);

        textView2=findViewById(R.id.textView2);
        textView2.setText("");

        if(company.equals("Google Firebase ML Cloud Services")){
            textView2.append("Firebase ML: ");
        }

        if(company.equals("IBM Watson ML Cloud Services")){
            textView2.append("IBM Watson: ");
        }

        textView2.append(classifiedResult);
        imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(photoBitmap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }
    public void edit(View view) {
        Intent intent = new Intent(getApplicationContext(),ActivitySeven.class);
        String itemName = textView1.getText().toString();
        String classifiedResult = textView2.getText().toString();
        intent.putExtra("uri",outputFileUri);
        intent.putExtra("itemName",itemName);
        intent.putExtra("classifiedResult",classifiedResult);
        intent.putExtra("imageFileName",imageFileName);
        intent.putExtra("company", company);
        startActivity(intent);
    }
}
