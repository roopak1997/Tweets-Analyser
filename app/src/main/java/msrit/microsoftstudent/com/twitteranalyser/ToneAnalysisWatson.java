package msrit.microsoftstudent.com.twitteranalyser;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import static com.twitter.sdk.android.core.Twitter.TAG;
import static com.twitter.sdk.android.core.Twitter.getLogger;

/**
 * Created by Roopak on 6/27/2017.
 */

public class ToneAnalysisWatson {

    static String tweets;

    static int sentiment_position;
    static int sentiment_tone_position;
    static String sentiment,sentiment_tone;
    static double lantone=0,emotone=0,socialtone=0;
    static int lanmaxpos=0,socailmaxpos=0,emomaxpos=0;
    static ToneAnalysis tone;

    static double min_cutoff = 0;
    static int pie_wedges=0;

    public ToneAnalysisWatson(){}

    public void setdocumenttones()
    {
            //double lantone=0,emotone=0,socialtone=0;
            int sentmax;
            ;

            ////analyse emotion
            double m=0;
            for(int i  = 0;i<5;i++)
                {
                    double temp = tone.getDocumentTone().getTones().get(0).getTones().get(i).getScore();
                    emotone =emotone + temp;
                    if(temp>m)
                    {emomaxpos = i;m=temp;}
                }
            emotone = emotone/5;

            /////analyse language
            m=0;
            for(int i  = 0;i<3;i++)
            {
                double temp = tone.getDocumentTone().getTones().get(1).getTones().get(i).getScore();
                lantone = lantone + temp;
                if(temp>m)
                {emomaxpos = i;m=temp;}
            }
            lantone = lantone/3;

            //////analyse socialness
            m=0;
            for(int i  = 0;i<5;i++)
            {
                double temp = tone.getDocumentTone().getTones().get(2).getTones().get(i).getScore();
                socialtone = socialtone + temp;
                if(temp>m)
                {emomaxpos = i;m=temp;}
            }
            socialtone = socialtone/5;

            if(emotone>lantone)
            {
                if(emotone>socialtone) {
                    sentiment_position = 0;
                    sentiment_tone_position=emomaxpos;
                }
                else {
                    sentiment_position = 2;
                    sentiment_tone_position=socailmaxpos;
                }
            }
            else
            {
                if(lantone>socialtone) {
                    sentiment_position = 1;
                    sentiment_tone_position=lanmaxpos;
                }
                else {
                    sentiment_position = 2;
                    sentiment_tone_position=socailmaxpos;
                }
            }
        sentiment = tone.getDocumentTone().getTones().get(sentiment_position).getName();
        sentiment_tone = tone.getDocumentTone().getTones().get(sentiment_position).getTones().get(sentiment_tone_position).getName();
    }

}
