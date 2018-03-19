package msrit.microsoftstudent.com.twitteranalyser;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class tweets_display_card_adapter extends BaseAdapter{

    ArrayList<String> array;

    public tweets_display_card_adapter(ArrayList<String> arr)
    {
        this.array = arr;
    }
    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public static class ViewHolder{
        TextView text;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null)
        {
            holder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tweetview, viewGroup, false);
            //view = inflater.inflate(R.layout.vote_groups_row,viewGroup,true);
            holder.text = (TextView) view.findViewById(R.id.text);
            view.setTag(holder);
        }
        else
            holder=(ViewHolder)view.getTag();
            holder.text.setText(array.get(i));
        return view;
    }
}
