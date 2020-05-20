package au.edu.canberra.mtfinalassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ActivitySeven extends AppCompatActivity {
    ArrayList<ClassifiedItem> ClassifiedItem = new ArrayList<ClassifiedItem>();
    private TextView editText;
    private TextView editText2;
    private Bitmap photoBitmap;
    private ImageView imageView;
    private Uri outputFileUri;
    String imageFileName;
    String itemName;
    String classifiedResult;
    String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven);
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
        classifiedResult = intent.getStringExtra("detectedResult");
        imageFileName=intent.getStringExtra("imageFileName");
        outputFileUri=intent.getParcelableExtra("uri");
        key=intent.getStringExtra("key");
        try {
            photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText=findViewById(R.id.editText);
        editText.setText("");
        editText.append(itemName);
        editText2=findViewById(R.id.editText2);
        editText2.setText("");
        editText2.append(classifiedResult);
        imageView=findViewById(R.id.imageView);
        imageView.setImageBitmap(photoBitmap);
    }
    public void cancel(View view){
        Intent intent = new Intent(getApplicationContext(),ActivityEight.class);
        startActivity(intent);
    }
    public void save(View view){

        if(key!=null) {
            ClassifiedItem item = new ClassifiedItem(editText.getText().toString(), editText2.getText().toString(), imageFileName);
            updateDataItemFromFirebaseDatabase(item, key);
        }
        else{
            addItemToRealtimeDB();
            uploadFileToFirebaseStorate(imageFileName,outputFileUri);}
        Intent intent = new Intent(getApplicationContext(),ActivityEight.class);
        startActivity(intent);
    }
    public void addItemToRealtimeDB() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("detected_item");

        ClassifiedItem Item = new ClassifiedItem(editText.getText().toString(), editText2.getText().toString(), imageFileName);
        String key = dbRef.push().getKey();
        dbRef.child(key).child("ItemName").setValue(Item.getItemName());
        dbRef.child(key).child("ClassifiedResult").setValue(Item.getClassifiedResult());
        dbRef.child(key).child("ImageFileName").setValue(Item.getImageFileName());

    }
    public void uploadFileToFirebaseStorate(String filename, Uri photoUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imagesRef = storageRef.child("images");
        if (photoUri != null) {
            UploadTask uploadTask = imagesRef.child(filename).putFile(photoUri);

            uploadTask.addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Upload failed: " + e.getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();

                        }
                    })
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(getApplicationContext(), "Upload complete",
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
        } else {
            Toast.makeText(getApplicationContext(), "Nothing to upload",
                    Toast.LENGTH_LONG).show();
        }
    }
    public String updateDataItemFromFirebaseDatabase(ClassifiedItem item,String key)
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("detected_item");
        dbRef.child(key).child("ItemName").setValue(item.getItemName());
        dbRef.child(key).child("DetectedResult").setValue(item.getClassifiedResult());
        dbRef.child(key).child("ImageFileName").setValue(item.getImageFileName());
        return key;
    }
    public void deleteDataItemFromFirebaseDatabase(String key)
    {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("detected_item");
        if(key != null) {
            dbRef.child(key).removeValue();
        }
        Intent intent = new Intent(getApplicationContext(),ActivityEight.class);
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
