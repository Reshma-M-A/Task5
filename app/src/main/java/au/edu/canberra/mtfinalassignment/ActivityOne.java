package au.edu.canberra.mtfinalassignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ActivityOne extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
    }

    public void openListView(View v) {
        Intent intent = new Intent(getApplicationContext(), ActivityTwo.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.list:
                Intent intent = new Intent(this, ActivityEight.class);
                startActivity(intent);
                break;
//                removing mpas since it will break if the user clicks on it
            case R.id.map_normal:
                break;
            case R.id.map_satellite:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
