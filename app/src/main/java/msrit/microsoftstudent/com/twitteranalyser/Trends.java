package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Trend;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class Trends extends AppCompatActivity {

    HashMap<String,Integer> trendMap;
    HashMap<String,String> trendStringMap;
    ArrayList<String> TrendsList;


    Spinner spinner;
    ListView lv;
    Button startanalysis;


    int country_select = 0;
    TextView country_name;
    String[] countries = {"Select Country","India","Usa","Australia","Canada", "Spain","New Zealand","Germany","France" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trends);

        trendMap = new HashMap<>();
        trendStringMap = new HashMap<>();
        country_name = (TextView)findViewById(R.id.country_name);
        lv=(ListView)findViewById(R.id.trends);
        startanalysis = (Button)findViewById(R.id.startanalysis);

        trendMap.put(countries[0],       23424848);
        trendMap.put(countries[1],         23424977);
        trendMap.put(countries[2],   23424748);
        trendMap.put(countries[3],      23424775);
        trendMap.put(countries[4],       23424950);
        trendMap.put(countries[5], 23424916);
        trendMap.put(countries[6],     23424829);
        trendMap.put(countries[7],      23424819);



        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adap21 = new ArrayAdapter<>(Trends.this, android.R.layout.simple_spinner_item, countries);
        adap21.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adap21);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0){
                    country_name.setText(countries[i]);
                }
                else{
                    Toast.makeText(Trends.this,"Select a  country",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(Trends.this,"Select a  country",Toast.LENGTH_SHORT).show();
            }
        });

        startanalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TrendsList.size()>1)
                    start_analysis();
                else
                    Toast.makeText(Trends.this,"No Trends to analyse",Toast.LENGTH_LONG).show();
            }
        });
    }



    public void TrendsForCountry(int item){

        taskTrendAll tl = new taskTrendAll();
        tl.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,countries[item]);
    }

    public void start(View view) {//on click method
        int country = spinner.getSelectedItemPosition();
        if(country!=0){
            country_select = country;
            country_name.setText(countries[country]);
            TrendsForCountry(country_select);
        }
        else{
            Toast.makeText(this,"Select a  country",Toast.LENGTH_SHORT).show();
        }

    }

    public void start_analysis() {

        /// new activity

        Intent i = new Intent(Trends.this,TrendAnalysis.class);
        i.putStringArrayListExtra("trends",TrendsList);
        startActivity(i);
        //Snackbar.make(findViewById(android.R.id.content), "Twitter sux", Snackbar.LENGTH_LONG).show();
       // taskLocationAll t1 = new taskLocationAll();
        //t1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,TrendsList.get(0),TrendsList.get(1),TrendsList.get(2),TrendsList.get(3),TrendsList.get(4));
    }


    public class taskTrendAll extends AsyncTask<String, String, ArrayList<String> >
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected ArrayList doInBackground(String... strings) {

            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
            //ToneAnalysis tone = new ToneAnalysis();
            List<twitter4j.Status> tweets;
            ConfigurationBuilder cb = new ConfigurationBuilder();

            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("LWY0wjNd6Yjgy8XN0xL0C0E8P")
                    .setOAuthConsumerSecret("FiIATMNToRlnekpBg4poXFaT2pNVyejIYDzveI2N9a1Ibjf4X7")
                    .setOAuthAccessToken("863833685346328576-xmfHmDYgEzI2ujeSwm2rcLpkBGwmfhI")
                    .setOAuthAccessTokenSecret("zr8y5BGEj4MrP43PcGeNoPsGjfQvkaITGh0uwjGCFGHKx");

            TwitterFactory tf = new TwitterFactory(cb.build());
            twitter4j.Twitter twitter = tf.getInstance();
            ArrayList<String>  s= new ArrayList<>();
            try {
                twitter4j.Trends trends = twitter.getPlaceTrends(trendMap.get(strings[0]));
                int count = 0;

                for(Trend trend : trends.getTrends()){
                    if(count < 5){
                        s.add(trend.getName());
                        count++;
                    }
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return s;
        }
        @Override
        protected void onPostExecute(ArrayList<String> s) {
            getTrendAnalysis(s);
        }
    }

    public void getTrendAnalysis(ArrayList<String> s) {
        if(!(TrendsList==null)){
            TrendsList.clear();
        }

        TrendsList = s;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, TrendsList);
        lv.setAdapter(adapter);
        startanalysis.setVisibility(View.VISIBLE);

    }


}
