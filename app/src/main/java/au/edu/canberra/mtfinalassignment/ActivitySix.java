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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Intent intent = getIntent();
        itemName = intent.getStringExtra("itemName");
        classifiedResult = intent.getStringExtra("classifiedResult");
        imageFileName=intent.getStringExtra("imageFileName");
        outputFileUri=intent.getParcelableExtra("uri");
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
        textView2.append(classifiedResult);
        imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(photoBitmap);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void edit(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ActivitySeven.class);
        String itemName=textView1.getText().toString();
        String detectedResult=textView2.getText().toString();
        intent.putExtra("uri",outputFileUri);
        intent.putExtra("itemName",itemName);
        intent.putExtra("detectedResult",detectedResult);
        intent.putExtra("imageFileName",imageFileName);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.list) {
            Intent intent = new Intent(this, ActivityEight.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.add) {
            Intent intent = new Intent(this, ActivityThree.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
