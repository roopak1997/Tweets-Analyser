package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;

public class GeoSearchDetailed extends AppCompatActivity {

    TextView textView,senti,tt;
    TextView dev;
    TextView title;
    CardView emo,lan,social;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geosearchdetailed);

        pieChart = (PieChart) findViewById(R.id.piechart);

        emo = (CardView)findViewById(R.id.emo);
        lan = (CardView)findViewById(R.id.lan);
        social = (CardView)findViewById(R.id.social);

        title = (TextView)findViewById(R.id.title);
        senti = (TextView)findViewById(R.id.senti);
        tt = (TextView)findViewById(R.id.Tone);

        title.setText(getIntent().getStringExtra("title"));

        registerForContextMenu(pieChart);

        emo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GeoSearchDetailed.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",0);
                startActivity(i);
            }
        });

        lan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GeoSearchDetailed.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",1);
                startActivity(i);
            }
        });

        social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GeoSearchDetailed.this,TonesDetailedAnalysis.class);
                i.putExtra("tone_cat",2);
                startActivity(i);
            }
        });

        gettonesetpie();
    }

    public void gettonesetpie(){

        senti.setText(ToneAnalysisWatson.tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getName());
        tt.setText(ToneAnalysisWatson.tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getTones().get(ToneAnalysisWatson.sentiment_tone_position).getName());

        pieChart.setUsePercentValues(true);
        pieChart.setRotationEnabled(true);
        pieChart.setCenterText("Tone analysis");
        pieChart.setCenterTextSize(7);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setDrawEntryLabels(true);

        ArrayList<PieEntry> yvalues = new ArrayList<>();
        ArrayList<String> x = new ArrayList<>();
        ArrayList<Integer> colours = new ArrayList<>();

        double total = ToneAnalysisWatson.emotone + ToneAnalysisWatson.lantone + ToneAnalysisWatson.socialtone;
        yvalues.add(new PieEntry((int) (ToneAnalysisWatson.emotone * 100 / total), 0));
        yvalues.add(new PieEntry((int) (ToneAnalysisWatson.lantone * 100 / total), 1));
        yvalues.add(new PieEntry((int) (ToneAnalysisWatson.socialtone * 100 / total), 2));

        colours.add(Color.parseColor("#FF3D00"));
        colours.add(Color.parseColor("#64DD17"));
        colours.add(Color.parseColor("#651FFF"));

        PieDataSet dataSet = new PieDataSet(yvalues, "\nGreean-Emotion\n Red-Language\n Yellow-Social");
        dataSet.setSelectionShift(2);
        dataSet.setValueTextSize(10);
        dataSet.setColors(colours);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
        //Toast.makeText(this,ToneAnalysisWatson.tweet_data.size()+"",Toast.LENGTH_LONG).show();
    }

}



