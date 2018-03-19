package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.Tone;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import static android.view.View.GONE;


public class Track_a_meeting extends AppCompatActivity {

    Button bt1, bt;
    Button timer;
    ToneAnalysisWatson toneAnalysisWatson;
    ToneAnalysis tone;
    String tweets;
    TextView textView;
    PieChart pieChart;
    LineChart lineChart;
    TextView score;
    EditText edt;
    boolean tweets_obtained = true;
    List<Entry> valsComp1 = new ArrayList<Entry>();
    List<Entry> valsComp2 = new ArrayList<Entry>();
    List<Entry> valsComp3 = new ArrayList<Entry>();
    List<Entry> valsComp4 = new ArrayList<Entry>();
    List<Entry> valsComp5 = new ArrayList<Entry>();
    int j=0, duration = 200000;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_a_meeting);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        edt = (EditText) findViewById(R.id.edit);
        pieChart = (PieChart) findViewById(R.id.piechart);
        pieChart.setVisibility(GONE);
        bt = (Button) findViewById(R.id.start);
        bt1 = (Button)findViewById(R.id.startp);
        textView = (TextView) findViewById(R.id.textView);
        score = (TextView) findViewById(R.id.score);
        lineChart = (LineChart) findViewById(R.id.linechart);


        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.durationarray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String sch = edt.getText().toString();
                if (sch.isEmpty())
                    Toast.makeText(Track_a_meeting.this, "Please Enter a Hashtag", Toast.LENGTH_SHORT).show();
                else {
                    if (tweets_obtained) {
                        Toast.makeText(Track_a_meeting.this, "starting", Toast.LENGTH_SHORT).show();

                        task tt = new task();
                        tt.execute(sch);

                    } else {
                        Toast.makeText(Track_a_meeting.this, "Analysing", Toast.LENGTH_SHORT).show();
                        textView.setText(tweets);

                        final ToneAnalyzer service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
                        service.setUsernameAndPassword("bbcc2f3f-a81c-47dd-8fa4-95d204332f60", "RSGnVkoEM7B7");


                        String place = spinner.getSelectedItem().toString();
                        if (place.equals("1 hour"))
                            duration = 400000;
                        else if (place.equals("2 hours"))
                            duration = 600000;


                        new CountDownTimer(duration, duration / 10) {
                            public void onTick(long millisUntilFinished) {

                                        /*Generates JSON file*/

                                task tt = new task();
                                tt.execute("dsf");


                                tone = service.getTone(tweets, null).execute();
                                TextView textView = (TextView) findViewById(R.id.textView);
                                textView.setText(tone.getDocumentTone().toString());
                                //JSON generated


                                String print = "Social tones : ";
                                int i;
                                double sco[] = {0, 0, 0, 0, 0};
                                for (i = 0; i < 5; i++) {
                                    sco[i] = tone.getDocumentTone().getTones().get(2).getTones().get(i).getScore();
                                    print = print + sco[i] + " ";
                                    sco[i] = sco[i] * 1000;
                                }



                                Entry c1e1 = new Entry(j, (int) sco[0]);
                                valsComp1.add(c1e1);
                                Entry c2e1 = new Entry(j, (int) sco[1]);
                                valsComp2.add(c2e1);
                                Entry c3e1 = new Entry(j, (int) sco[2]);
                                valsComp3.add(c3e1);
                                Entry c4e1 = new Entry(j, (int) sco[3]);
                                valsComp4.add(c4e1);
                                Entry c5e1 = new Entry(j, (int) sco[4]);
                                valsComp5.add(c5e1);
                                j = j + 1;

                                LineDataSet setComp1 = new LineDataSet(valsComp1, "Anger");
                                setComp1.setColor(Color.parseColor("#E42217"));
                                LineDataSet setComp2 = new LineDataSet(valsComp2, "Disgust");
                                setComp2.setColor(Color.parseColor("#41A317"));
                                LineDataSet setComp3 = new LineDataSet(valsComp3, "Fear");
                                setComp3.setColor(Color.parseColor("#15317E"));
                                LineDataSet setComp4 = new LineDataSet(valsComp4, "Joy");
                                setComp4.setColor(Color.parseColor("#FFD801"));
                                LineDataSet setComp5 = new LineDataSet(valsComp5, "Sadness");
                                setComp5.setColor(Color.parseColor("#6A287E"));

                                List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                                dataSets.add(setComp1);
                                dataSets.add(setComp2);
                                dataSets.add(setComp3);
                                dataSets.add(setComp4);
                                dataSets.add(setComp5);

                                LineData data = new LineData(dataSets);


                                lineChart.setData(data);
                                lineChart.invalidate(); // refresh
                                lineChart.setBottom(0);
                                lineChart.setBorderColor(Color.parseColor("#000000"));
                                Description descr = new Description();
                                descr.setText("Emotion Analysis");
                                lineChart.setDescription(descr);
                                String values[] = new String[10];
                                for (int k = 0; k < 10; k++)
                                    values[k] = String.valueOf(k * duration / 10000);


                                XAxis xAxis = lineChart.getXAxis();
                                xAxis.setValueFormatter(new MyYAxisValueFormatter(values));


                            }

                            public void onFinish() {



                            }
                        }.start();
                    }
                }

            }
        });
    }
    public class MyYAxisValueFormatter implements IAxisValueFormatter {

        private String[] mValues;

        public MyYAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }

        /** this is only needed if numbers are returned, else return 0 */

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() != R.id.piechart)
            return;
        String[] op = {"emotion","language","social"};
        for(String each:op)
        {
            menu.add(each);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //int pos = info.position;
        if(item.getTitle().equals("emotion"))
        {
            Toast.makeText(Track_a_meeting.this,"emotion time",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Track_a_meeting.this, Tone.class);
            startActivity(i);
        }
        return true;
    }


    private String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }

    public class task extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... strings) {

            String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
            Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
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
            ToneAnalysis toner=new ToneAnalysis();
            String s = "";
            try {

                Query query = new Query(strings[0]);
                //query.setCount(20);
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
                            withHyper = withHyper.replaceAll(m.group(i),"").trim();
                            i++;
                        }

                        s = s + withHyper;
                    }
                }
            } catch (twitter4j.TwitterException e) {
                e.printStackTrace();
            }
            return s;
        }


        @Override
        protected void onPostExecute(String string) {

            tweets = string;
            tweets_obtained = false;
        }
    }




}
