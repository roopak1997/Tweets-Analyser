package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Roopak on 7/19/2017.
 */

public class TrendAnalysis extends AppCompatActivity implements TrendsRecyclerAdapter.Myclick
{

    TrendsRecyclerAdapter adapter;
    RecyclerView recyclerview;
    HashMap<String,ToneAnalysis> trendToneMap;
    ArrayList<String> TrendsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.trendanalysis);

        trendToneMap = new HashMap<>();
        TrendsList = getIntent().getStringArrayListExtra("trends");

        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);

        Snackbar.make(findViewById(android.R.id.content), "This will take a couple of minutes please wait!!!!", Snackbar.LENGTH_INDEFINITE).show();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskLocationAll t1 = new taskLocationAll();
        t1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,TrendsList.get(0),TrendsList.get(1),TrendsList.get(2),TrendsList.get(3),TrendsList.get(4));
    }


    public void setadapter() {
        recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(layoutManager);

        adapter = new TrendsRecyclerAdapter(trendToneMap,TrendsList);
        adapter.setMyclick(this);
        recyclerview.setAdapter(adapter);

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
                    Query query = new Query(strings[cr]);
                    query.setCount(100);
                    QueryResult result;

                    result = twitter.search(query);
                    tweets = result.getTweets();
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
                        }
                    }
                } catch (twitter4j.TwitterException e) {
                    e.printStackTrace();
                }
                publishProgress(strings[cr],s);
            }
            return "lol";
        }
        @Override
        protected void onPostExecute(String string) {
            setadapter();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            getTrendTone(values[0],values[1]);
        }
    }

    public void getTrendTone(String trendname,String string) {

        Snackbar.make(findViewById(android.R.id.content), "Analysing "+trendname, Snackbar.LENGTH_LONG).show();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        service.setUsernameAndPassword("bbcc2f3f-a81c-47dd-8fa4-95d204332f60", "RSGnVkoEM7B7");
        ToneAnalysis tone= new ToneAnalysis();
        try{
            tone = service.getTone(string, null).execute();
        }
       catch (Exception e){
           trendToneMap.put(trendname,null);
       }

        if(!(tone==null))
        {
            trendToneMap.put(trendname,tone);
        }
        else {
            trendToneMap.put(trendname,null);
        }


        //trend_set.append("\n"+trendname+" \n "+tone.getSentencesTone().size());
    }

    @Override
    public void afterclick(int pos) {
        //Toast.makeText(this,"clicked  "+pos,Toast.LENGTH_SHORT).show();

        if(pos==0){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(pos));
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(TrendAnalysis.this,TrendsDetailed.class);
                i.putExtra("title",TrendsList.get(pos));
                startActivity(i);
            }
            catch(Exception e){
                Toast.makeText(this,"Analysis Not Available",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==1){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(pos));
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(TrendAnalysis.this,TrendsDetailed.class);
                i.putExtra("title",TrendsList.get(pos));
                startActivity(i);
            }
            catch(Exception e){
                Toast.makeText(this,"Analysis Not Available",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==2){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(pos));
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(TrendAnalysis.this,TrendsDetailed.class);
                i.putExtra("title",TrendsList.get(pos));
                startActivity(i);
            }
            catch(Exception e){
                Toast.makeText(this,"Analysis Not Available",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==3){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(pos));
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(TrendAnalysis.this,TrendsDetailed.class);
                i.putExtra("title",TrendsList.get(pos));
                startActivity(i);
            }
            catch(Exception e){
                Toast.makeText(this,"Analysis Not Available",Toast.LENGTH_SHORT).show();
            }
        }
        else if(pos==4){
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = trendToneMap.get(TrendsList.get(pos));
                toneAnalysisWatson.setdocumenttones();
                Intent i = new Intent(TrendAnalysis.this,TrendsDetailed.class);
                i.putExtra("title",TrendsList.get(pos));
                startActivity(i);
            }
            catch(Exception e){
                Toast.makeText(this,"Analysis Not Available",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
