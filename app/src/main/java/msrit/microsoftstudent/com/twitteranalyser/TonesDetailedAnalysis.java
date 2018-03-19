package msrit.microsoftstudent.com.twitteranalyser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roopak on 7/6/2017.
 */

public class TonesDetailedAnalysis extends AppCompatActivity {

    //Toolbar toolbar;
    ViewPager viewPager;
    private TabLayout tabLayout;
    int tone_cat;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tonedetailedanalysis);

        tone_cat = getIntent().getIntExtra("tone_cat",0);
        sharedPreferences = this.getSharedPreferences("tone_cat",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("category",tone_cat);
        editor.commit();

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        if(tone_cat==0)
            setupViewPagerEmotion(viewPager);
        else if(tone_cat==1)
            setupViewPagerLanguage(viewPager);
        else
            setupViewPagerSocial(viewPager);
    }

    private void setupViewPagerEmotion(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new FirstTone(), "Anger");

        adapter.addFrag(new SecondTone(), "Disgust");

        adapter.addFrag(new ThirdTone(), "Fear");

        adapter.addFrag(new FourthTone(), "Joy");

        adapter.addFrag(new FifthTone(), "Sadness");

        viewPager.setAdapter(adapter);
    }

    private void setupViewPagerLanguage(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new FirstTone(), "Analytical");

        adapter.addFrag(new SecondTone(), "Confident");

        adapter.addFrag(new ThirdTone(), "Tentative");

        viewPager.setAdapter(adapter);
    }

    private void setupViewPagerSocial(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new FirstTone(), "Openness");

        adapter.addFrag(new SecondTone(), "Conscientiousness");

        adapter.addFrag(new ThirdTone(), "Extraversion");

        adapter.addFrag(new FourthTone(), "Agreeableness");

        adapter.addFrag(new FifthTone(), "Emotional Range");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
