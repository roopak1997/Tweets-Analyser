package msrit.microsoftstudent.com.twitteranalyser;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Roopak on 7/13/2017.
 */

public class TrendsRecyclerAdapter extends RecyclerView.Adapter<TrendsRecyclerAdapter.ViewHolder> {

    Myclick myclick;

    public interface  Myclick{
        public void afterclick(int pos);
    }

    public void setMyclick(Myclick myclick)   //this is passes in function call
    {
        this.myclick=myclick;
    }


    public HashMap<String,ToneAnalysis> toneTrendMap;
    public ArrayList<String> trendslist;


    public TrendsRecyclerAdapter(HashMap<String,ToneAnalysis> lol,ArrayList<String> ll) {
        this.toneTrendMap = lol;
        this.trendslist = ll;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView trendview;
        private TextView sentiment;
        private TextView tone_param;
        private TextView no_sentiment;
        CardView cc;

        public ViewHolder(View itemView) {
            super(itemView);
            trendview = (TextView) itemView.findViewById(R.id.trend);
            sentiment = (TextView) itemView.findViewById(R.id.sentiment);
            tone_param=(TextView) itemView.findViewById(R.id.tone);
            no_sentiment=(TextView) itemView.findViewById(R.id.no_tone);
            cc = (CardView) itemView.findViewById(R.id.cc);
            cc.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            myclick.afterclick(getAdapterPosition());
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trends_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToneAnalysis ty;
        ty = toneTrendMap.get(trendslist.get(position));
       // Log.d("wtf is wrong",toneTrendMap.get(trendslist.get(position)).toString());
       holder.trendview.setText(trendslist.get(position));
        if(!(ty==null))
        {
            try{
                ToneAnalysisWatson toneAnalysisWatson = new ToneAnalysisWatson();
                toneAnalysisWatson.tone = ty;
                toneAnalysisWatson.setdocumenttones();
                holder.sentiment.setText(toneAnalysisWatson.tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getName());
                holder.tone_param.setText(toneAnalysisWatson.tone.getDocumentTone().getTones().get(ToneAnalysisWatson.sentiment_position).getTones().get(ToneAnalysisWatson.sentiment_tone_position).getName());
            }
            catch (Exception e){
                Log.d("lol",e.toString());
                holder.tone_param.setVisibility(View.GONE);
                holder.sentiment.setVisibility(View.GONE);
                holder.no_sentiment.setVisibility(View.VISIBLE);
               // holder.no_sentiment.setText("Analysis failed");
            }

        }
        else {
            holder.tone_param.setVisibility(View.GONE);
            holder.sentiment.setVisibility(View.GONE);
            holder.no_sentiment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return toneTrendMap.size();
    }


}
