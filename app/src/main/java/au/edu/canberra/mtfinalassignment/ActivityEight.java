package au.edu.canberra.mtfinalassignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityEight extends AppCompatActivity {
    ArrayList<ClassifiedItem> ClassifiedItem = new ArrayList<ClassifiedItem>();

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference dbRef = db.getReference("detected_item");
    ArrayList<String> keys = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eight);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



        final ClassifiedItemAdapter adapter = new ClassifiedItemAdapter(
                this, R.layout.activity_eight, ClassifiedItem);
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ClassifiedItem item = new ClassifiedItem(
                        (String) dataSnapshot.child("ItemName").getValue(),
                        (String)(dataSnapshot.child("ClassifiedResult").getValue()),
                        ((String) dataSnapshot.child("ImageFileName").getValue())
                );
                adapter.add(item);
                keys.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String key=dataSnapshot.getKey();
                int index=keys.indexOf(key);

                if(index!=-1)
                {
                    ClassifiedItem.remove(index);
                    keys.remove(key);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String key=keys.get(position);
                        ClassifiedItem dtm = ClassifiedItem.get(position);
                        Intent intent = new Intent(view.getContext(), ActivityNine.class);
                        intent.putExtra("ItemName", dtm.getItemName());
                        intent.putExtra("DetectedResult", dtm.getClassifiedResult());
                        intent.putExtra("ImageFileName", dtm.getImageFileName());
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }
                });


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference imagesRef = storageRef.child("images");
        try {
            final File localFile = File.createTempFile("temp", ".jpg");
            //String filename = "img_" + String.valueOf(imageRes) + ".jpg";

            //downloadFile(imagesRef.child(filename), localFile);

        } catch (IOException ex) {
            notifyUser(ex.getMessage());
        }

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void downloadFile(StorageReference fileRef, final File file) {
        if (file != null) {
            fileRef.getFile(file)
                    .addOnSuccessListener(
                            new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Uri uri = Uri.fromFile(file);
                                    //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                    //imageView.setImageURI(uri);
                                    notifyUser("Download complete");
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    notifyUser("Unable to download");
                                }
                            });
        }
    }
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void openCap(View v) {
        Intent intent = new Intent(getApplicationContext(), ActivityThree.class);
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
