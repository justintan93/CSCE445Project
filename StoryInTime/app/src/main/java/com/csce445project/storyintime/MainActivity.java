package com.csce445project.storyintime;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private View mView;
    private TextView mText;
    private ImageView mImageView;
    private Button mLeftButton, mRightButton;
    private ListView listView;
    private TextAdapter textAdapter;
    private ArrayList<View> list;
    private LayoutInflater layoutInflater;
    private ViewFlipper mViewFlipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        list = new ArrayList<View>();
        layoutInflater = ((Activity)this).getLayoutInflater();
        listView = (ListView)this.findViewById(R.id.listView);
        textAdapter = new TextAdapter(this,list);
        listView.setAdapter(textAdapter);

        addViewToListView(createText("HELLO"));
        addViewToListView(createChoiceText("BYE", "HI", "B"));
        addViewToListView(createLinkText("LOLASvausfjafjkasl;kfjaskl;dfj;asdlfjkasopfjiawe;fjkal;skjfopsjifapoj;lasdjKJDAL:K", "https://www.facebook.com"));
        addViewToListView(createPlayerText("I AM PLAYER 1"));
        addViewToListView(createTimeText("Bob is currently walking to the supermarket"));
    }

    public void addViewToListView(View v) {
        list.add(v);
        textAdapter.notifyDataSetChanged();
    }

    public View createText(String message) {
        mView = LayoutInflater.from(this).inflate(R.layout.text,null);
        mText = (TextView) mView.findViewById(R.id.text_text);
        mText.setText(message);
        return mView;
    }

    public View createPlayerText(String message) {
        mView = LayoutInflater.from(this).inflate(R.layout.text_player,null);
        mText = (TextView) mView.findViewById(R.id.text_player);
        mText.setText(message);
        return mView;
    }

    public View createTimeText(String message) {
        mView = LayoutInflater.from(this).inflate(R.layout.text_time,null);
        mText = (TextView) mView.findViewById(R.id.text_time);
        mText.setText(message);
        return mView;
    }

    public View createLinkText(String message, String link) {
        mView = LayoutInflater.from(this).inflate(R.layout.text_link,null);
        mText = (TextView) mView.findViewById(R.id.text_link_text);
        mText.setText(message);
        mImageView = (ImageView) mView.findViewById(R.id.text_info);
        mViewFlipper = (ViewFlipper) mView.findViewById(R.id.view_flipper);
        mViewFlipper.setInAnimation(this,R.anim.display_text);
        mViewFlipper.setOutAnimation(this, R.anim.display_link);
        final String websiteLink = link;
        mImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String url = websiteLink;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                mViewFlipper.showPrevious();
                mImageView.setClickable(false);
                return true;
            }
        });
        mText.setOnLongClickListener(new View.OnLongClickListener(){
            public boolean onLongClick(View v){
                mViewFlipper.showNext();
                mImageView.setClickable(true);
                return true;
            }
        });
        return mView;
    }

    public View createChoiceText(String message, String button1, String button2) {
        mView = LayoutInflater.from(this).inflate(R.layout.text_choice,null);
        mText = (TextView) mView.findViewById(R.id.text_choice);
        mText.setText(message);
        mLeftButton = (Button) mView.findViewById(R.id.left_choice);
        mLeftButton.setText(button1);
        mRightButton = (Button) mView.findViewById(R.id.right_choice);
        mRightButton.setText(button2);
        return mView;
    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW,uriUrl);
        startActivity(launchBrowser);
    }

}
