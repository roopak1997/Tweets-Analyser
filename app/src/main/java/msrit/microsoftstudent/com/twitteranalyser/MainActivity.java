package msrit.microsoftstudent.com.twitteranalyser;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    FloatingActionButton geosearch,trends,search,track;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        search = (FloatingActionButton) findViewById(R.id.search);
        geosearch = (FloatingActionButton) findViewById(R.id.geosearch);
        trends = (FloatingActionButton) findViewById(R.id.trends);
        track = (FloatingActionButton) findViewById(R.id.track);



        geosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,GeoSearch.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,Search.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }


            }
        });

        trends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,Trends.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    Intent i = new Intent(MainActivity.this,Track_a_meeting.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(MainActivity.this,"No internet connection",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timetable_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help) {
            Intent i = new Intent(MainActivity.this,help.class);
            startActivity(i);
        }

        if (id == R.id.exit) {
           finish();
        }

        if (id == R.id.credits) {

        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



