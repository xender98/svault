package com.example.registerloginsp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingindicators;
    private MaterialButton buttonOnboardingAction;

    private SharedPreferences sharedPreferences;

    public static final String MyPREFERENCES = "Mypassword" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getSupportActionBar().hide();

        layoutOnboardingindicators=findViewById(R.id.layoutonBording);
        buttonOnboardingAction=findViewById(R.id.buttonOnbording);


        setupOnboardingItems();
        ViewPager2 onbordingViewPager=findViewById(R.id.viewpageronBording);
        onbordingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingindicators();
        setCurrentOnboardingIndicator(0);
        onbordingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onbordingViewPager.getCurrentItem()+1<onboardingAdapter.getItemCount()){
                    onbordingViewPager.setCurrentItem(onbordingViewPager.getCurrentItem()+1);
                }else{
                    sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();


                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));


                }
            }
        });
    }
    private void setupOnboardingItems(){
        List<OnboardingItem> onboardingItem=new ArrayList<>();
        OnboardingItem itemPayOnline= new OnboardingItem();
        itemPayOnline.setTitle("SVault");
        itemPayOnline.setDescription("SVault is an encrypted space on your smartphone for storing files and images for your eyes only.");
        itemPayOnline.setImage(R.drawable.img1);


        OnboardingItem itemOnTheWay= new OnboardingItem();
        itemOnTheWay.setTitle("Image Encryption");
        itemOnTheWay.setDescription("SVault helps to protect your sensitive images using image encryption and make your image unrecognizable using the secret key.");
        itemOnTheWay.setImage(R.drawable.img2);

        OnboardingItem itemEatTogether= new OnboardingItem();
        itemEatTogether.setTitle("Text Encryption");
        itemEatTogether.setDescription("SVault helps to Protect your work or personal files from identity theft and leaks as well as from being read by hackers and eavesdroppers");
        itemEatTogether.setImage(R.drawable.img3);

        onboardingItem.add(itemPayOnline);
        onboardingItem.add(itemOnTheWay);
        onboardingItem.add(itemEatTogether);
        onboardingAdapter=new OnboardingAdapter(onboardingItem);


    }

    private void setupOnboardingindicators(){
        ImageView[] indicators= new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);
        for(int i=0;i<indicators.length;i++){
            indicators[i]=new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingindicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount=layoutOnboardingindicators.getChildCount();
        for(int i=0;i<childCount;i++){
            ImageView imageView=(ImageView)layoutOnboardingindicators.getChildAt(i);
            if(i==index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active));
            }
            else{
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            }
        }
        if(index==onboardingAdapter.getItemCount()-1){
            buttonOnboardingAction.setText("start");
        }else{
            buttonOnboardingAction.setText("Next");
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}