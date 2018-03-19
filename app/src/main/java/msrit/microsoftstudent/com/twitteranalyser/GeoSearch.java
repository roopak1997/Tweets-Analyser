package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.GeoLocation;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GeoSearch extends AppCompatActivity implements GeoSearchCardAdapter.Myclick {
    EditText search1 ;
    String cont = null;
    String searchQuery="";
    HashMap<String,LocObject> hmap = new HashMap<>();
    TextView title;

    ArrayList<ToneAnalysis> toneList;

    RecyclerView recyclerview;
    GeoSearchCardAdapter adapter;

    HashMap<String,String> tweetMap;

    HashMap<String,ToneAnalysis> toneCountryMap;
    String[] country = {"india","australia","england","usa","canada","germany","france","spain"};

    TextView text ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geosearch);

        toneList = new ArrayList<>(10);
        search1 = (EditText)findViewById(R.id.search);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        text = (TextView)findViewById(R.id.test);
        cont ="";


        tweetMap = new HashMap<>();
        toneCountryMap = new HashMap<>();
        hmap = new HashMap<>();
        hmap.put("england",new LocObject( 53.4808, 2.2426, 283));
        hmap.put("india", new LocObject(23.2599, 77.4126,1043));
        hmap.put("usa", new LocObject(38.335928, -98.754175,1331));
        hmap.put("australia" , new LocObject(-27.299744, 133.622425,1876));
        hmap.put("canada", new LocObject(60.20,-118.57,1426));
        hmap.put("germany",new LocObject(50.5558, 9.6808,143));
        hmap.put("france",new LocObject(45.7772, 3.0870,295));
        hmap.put("spain",new LocObject(40.4168, 3.7038,242));

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
    }


    public void searchAll(View view){

        Snackbar.make(findViewById(android.R.id.content), "This will take a couple of minutes please wait!!!!\n Do not cancel on no response", Snackbar.LENGTH_INDEFINITE).show();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        searchQuery = search1.getText().toString();
        taskLocationAll tl = new taskLocationAll();
        tl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"india","australia","england","usa","canada","germany","france","spain");

    }


    public class LocObject {
        GeoLocation geoLocation;
        int radius;

        LocObject(double lat, double log, int rad){
            geoLocation = new GeoLocation(lat,log);
            radius = rad;
        }
    }

    public class taskLocationAll extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {

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

            for (int cr = 0; cr < strings.length; cr++) {

                String s = "";
                try {
                    Query query = new Query(searchQuery);
                    query.setCount(100);
                    query.setGeoCode(hmap.get(strings[cr]).geoLocation, hmap.get(strings[cr]).radius, Query.MILES);
                    QueryResult result;


                    result = twitter.search(query);
                    tweets = result.getTweets();
                    int count = 0;
                    for (twitter4j.Status tweet : tweets) {
                        String withHyper = "";

                        if (tweet.getLang().equals("en")) {
                            withHyper = tweet.getText().toString();
                            Matcher m = p.matcher(withHyper);
                            int i = 0;
                            while (m.find()) {
                                withHyper = withHyper.replaceAll(m.group(i), "").trim();
                                i++;
                            }
                            s = s + withHyper;
                            count++;
                        }
                    }
                } catch (twitter4j.TwitterException e) {
                    e.printStackTrace();
                }
                publishProgress(s,strings[cr]);
            }
            return "crap";
        }
        @Override
        protected void onPostExecute(String string) {
            Snackbar.make(findViewById(android.R.id.content), "Starting Sentiment Analysis", Snackbar.LENGTH_LONG).show();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countryAnalysis();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            tweetMap.put(values[1],values[0]);
            //Toast.makeText(GeoSearch.this,values[1],Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), "Obtaining tweets from "+values[1], Snackbar.LENGTH_SHORT).show();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void countryAnalysis() {

        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword("bbcc2f3f-a81c-47dd-8fa4-95d204332f60", "RSGnVkoEM7B7");

        Iterator<Map.Entry<String,String> > it1 = tweetMap.entrySet().iterator();

        int count =0;
        while(it1.hasNext()){
            Snackbar.make(findViewById(android.R.id.content), "Analysing tweets from "+country[count], Snackbar.LENGTH_SHORT).show();
            count++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ToneAnalysis tone = null;
            Map.Entry entry = it1.next();
            String country = entry.getKey().toString();
            String tweetsForCountry = entry.getValue().toString();

            if(country.equals("india"))
            {
                try {
                    tone = service.getTone(tweetsForCountry, null).execute();
                    if (tone != null) {
                        toneCountryMap.put("india", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("india", null);
                        //text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("india", null);
                }

            }
            else if(country.equals("england"))
            {
                try {

                    //text.append("England\n");
                    tone = service.getTone(tweetsForCountry, null).execute();
                    if (tone != null) {
                        toneCountryMap.put("england", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("england", null);
                        //  text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("england", null);
                }

            }

            else if(country.equals("usa"))
            {
                try {

                    //text.append("USA\n");
                    tone = service.getTone(tweetsForCountry, null).execute();
                    if (tone != null) {
                        toneCountryMap.put("usa", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("usa", null);
                        //  text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("usa", null);
                }

            }

            else if(country.equals("australia"))
            {
                try {
                    //text.append("Australia\n");
                    tone = service.getTone(tweetsForCountry, null).execute();

                    if (tone != null) {
                        toneCountryMap.put("australia", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("australia", null);
                        //  text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("australia", null);
                }
            }

            else if(country.equals("canada"))
            {
                //text.append("Canada\n");
                try {

                    tone = service.getTone(tweetsForCountry, null).execute();

                    if (tone != null) {
                        toneCountryMap.put("canada", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("canada", null);
                        //  text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("canada", null);
                }

            }


            else if(country.equals("germany"))
            {
                //text.append("Germany\n");
                try {
                    tone = service.getTone(tweetsForCountry, null).execute();
                    if (tone != null)

                        if (tone != null) {
                            toneCountryMap.put("germany", tone);
                            // text.append(tone.getDocumentTone().toString());
                        } else {
                            toneCountryMap.put("germany", null);
                            //      text.append("null");
                        }
                }
                catch (Exception e){
                    toneCountryMap.put("germany", null);
                }
            }

            else if(country.equals("france"))
            {
                try {
                    //text.append("France\n");
                    tone = service.getTone(tweetsForCountry, null).execute();

                    if (tone != null) {
                        toneCountryMap.put("france", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("france", null);
                        //  text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("france", null);
                }

            }

            else if(country.equals("spain"))
            {
                try {
                    tone = service.getTone(tweetsForCountry, null).execute();

                    if (tone != null) {
                        toneCountryMap.put("spain", tone);
                        //text.append(tone.getDocumentTone().toString());
                    } else {
                        toneCountryMap.put("spain", null);
                        //text.append("null");
                    }
                }
                catch (Exception e){
                    toneCountryMap.put("spain", null);
                }

            }
        }
        setadapterwithcontent();
    }

    private void setadapterwithcontent() {
        recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);

        adapter = new GeoSearchCardAdapter(toneCountryMap);
        adapter.setMyclick(this);
        recyclerview.setAdapter(adapter);

    }

    @Override
    public void afterclick(int pos) {
        Toast.makeText(this,"clicked  "+pos,Toast.LENGTH_SHORT).show();

        if(pos==0){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }

        }
        else if(pos==1){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==2){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==3){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==4){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==5){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==6){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==7){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = toneCountryMap.get(country[pos]);
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(GeoSearch.this,GeoSearchDetailed.class);
                i.putExtra("title",country[pos]);
                startActivity(i);
            }
            catch (Exception e){
                Toast.makeText(this,"Detailed Analysis not Available currently",Toast.LENGTH_SHORT).show();
            }
        }
    }

}