package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

/**
 * Created by Roopak on 7/6/2017.
 */
public class FifthTone extends Fragment {
    TextView tv,desc;
    PieChart pieChart;
    ScrollView sc;
    TextView non;
    int ton_param = 4;
    double min_cutoff = ToneAnalysisWatson.min_cutoff;
    int pie_chart_wedges = ToneAnalysisWatson.pie_wedges;
    int[] count = {0,0,0,0,0,0,0,0,0,0};
    boolean significant = false;


    public FifthTone(){
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.eachemotionfragment,container,false);
        ListView lv = (ListView)view.findViewById(R.id.list_of_tweets);
        pieChart = (PieChart)view.findViewById(R.id.piechart);
        tv = (TextView)view.findViewById(R.id.test);
        sc = (ScrollView)view.findViewById(R.id.scroll);
        non = (TextView)view.findViewById(R.id.nosentiment);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("tone_cat", Context.MODE_PRIVATE);
        int tone_cat = sharedPreferences.getInt("category",0);

        desc = (TextView)view.findViewById(R.id.desc);
        setdesc(tone_cat);

        try {
            check_sentiment(tone_cat);
            if (significant) {
                setpiechart(tone_cat);
                ArrayList<String> sentences = get_emotion_sentences(tone_cat);
                if (sentences.size() > 0) {
                    tweets_display_card_adapter adapter = new tweets_display_card_adapter(sentences);
                    lv.setAdapter(adapter);
                    tv.setVisibility(View.GONE);
                } else {
                    tv.setVisibility(View.VISIBLE);
                }
            }
        }
        catch (Exception e){
            tv.setVisibility(View.VISIBLE);
        }
        //tv.setText(tone_cat+"   ");
        return view;
    }

    private void check_sentiment(int tone_cat) {
        int size = ToneAnalysisWatson.tone.getSentencesTone().size();
        for(int i = 0;i<size;i++){
            try {
                if(ToneAnalysisWatson.tone.getSentencesTone().get(i).getTones().get(tone_cat).getTones().get(ton_param).getScore()>min_cutoff) {
                    double score = ToneAnalysisWatson.tone.getSentencesTone().get(i).getTones().get(tone_cat).getTones().get(ton_param).getScore() * 10;
                    int sc = (int) score;
                    count[sc]++;
                }
            }
            catch (Exception exception){
                Log.d("tones error",exception.toString());
            }
        }
        int tt = 0;
        for(int i = pie_chart_wedges;i<10;i++)
        {
            tt = tt + count[i];
        }

        if(tt>5){
            significant = true;
        }
        else{
            sc.setVisibility(View.GONE);
            non.setVisibility(View.VISIBLE);
        }

    }


    public void setpiechart(int tone_cat){
        pieChart.setUsePercentValues(true);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText(ToneAnalysisWatson.tone.getDocumentTone().getTones().get(tone_cat).getName());
        pieChart.setCenterTextSize(15);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);

        ArrayList<PieEntry> yvalues = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();
        PieDataSet dataSet;

        for (int i = pie_chart_wedges;i<10;i++){
            yvalues.add(new PieEntry(count[i],i));
           // tv.append(count[i]+"-");
        }
        colours.add(Color.parseColor("#F3E5F5"));
        colours.add(Color.parseColor("#E1BEE7"));
        colours.add(Color.parseColor("#CE93D8"));
        colours.add(Color.parseColor("#BA68C8"));
        colours.add(Color.parseColor("#AB47BC"));
        colours.add(Color.parseColor("#9C27B0"));
        colours.add(Color.parseColor("#8E24AA"));
        colours.add(Color.parseColor("#7B1FA2"));
        colours.add(Color.parseColor("#6A1B9A"));
        colours.add(Color.parseColor("#4A148C"));

        dataSet = new PieDataSet(yvalues, "Least to max");

        dataSet.setSelectionShift(2);
        dataSet.setValueTextSize(10);

        dataSet.setColors(colours);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    public ArrayList<String> get_emotion_sentences(int tone_cat)
    {

        ArrayList<String> ret = new ArrayList<>();
        int size = ToneAnalysisWatson.tone.getSentencesTone().size();
        for(int i = 0;i<size;i++){
            try {
                if (ToneAnalysisWatson.tone.getSentencesTone().get(i).getTones().get(tone_cat).getTones().get(ton_param).getScore() > 0.5) {
                    ret.add(ToneAnalysisWatson.tone.getSentencesTone().get(i).getText());
                }
            }
            catch (Exception e)
            {
                Log.d("error",e.toString());
            }
        }
        return ret;
    }


    private void setdesc(int tone_cat) {
        if(tone_cat==0){
            desc.setText("Sadness indicates a feeling of loss and disadvantage. When a person is quiet, less energetic, and withdrawn, it may be inferred that they feel sadness.");
        }
        else if(tone_cat==1){
            desc.setText("");
        }
        else if(tone_cat==2){
            desc.setText("The extent to which a person is open to experiencing a variety of activities");
        }
    }
}