package msrit.microsoftstudent.com.twitteranalyser;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

public class eachEmotion extends Fragment {
    TextView tv;
    PieChart pieChart;

    public eachEmotion(){
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

        Bundle bundle = getArguments();
        int tone_param = bundle.getInt("tone_param");
        int tone_cat = bundle.getInt("tone_cat");

        ArrayList<String> sentences = get_emotion_sentences(tone_cat,tone_param);
        setpiechart(tone_cat,tone_param);
        tweets_display_card_adapter adapter = new tweets_display_card_adapter(sentences);
        lv.setAdapter(adapter);

        tv.setText(tone_cat+"   "+ tone_param+"");
        return view;
    }


    public void setpiechart(int tone_cat,int tone_param){
        pieChart.setUsePercentValues(true);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText("Tone analysis");
        pieChart.setCenterTextSize(15);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);

        ArrayList<PieEntry> yvalues = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();
        PieDataSet dataSet;

        int[] count = {0,0,0,0,0,0,0,0,0,0};
        int size = ToneAnalysisWatson.tone.getSentencesTone().size();
        for(int i = 0;i<size;i++){
            try {
                double score = ToneAnalysisWatson.tone.getSentencesTone().get(i).getTones().get(tone_cat).getTones().get(tone_param).getScore();
                int sc = (int) score * 10;
                count[sc]++;
            }
            catch (Exception exception){
                Log.d("tones error",exception.toString());
            }

        }

        for (int i = 0;i<10;i++){
            yvalues.add(new PieEntry(count[i],i));

        }

        colours.add(Color.GREEN);
        colours.add(Color.RED);
        colours.add(Color.YELLOW);
        colours.add(Color.BLUE);
        colours.add(Color.GRAY);

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

    public ArrayList<String> get_emotion_sentences(int tone_cat,int tone_param)
    {
        ArrayList<String> ret = new ArrayList<>();
        int size = ToneAnalysisWatson.tone.getSentencesTone().size();
        for(int i = 0;i<size;i++){
            if(ToneAnalysisWatson.tone.getSentencesTone().get(i).getTones().get(tone_cat).getTones().get(tone_param).getScore()>0.5){
                ret.add(ToneAnalysisWatson.tone.getSentencesTone().get(i).getText());
            }
        }
        return ret;
    }
}
