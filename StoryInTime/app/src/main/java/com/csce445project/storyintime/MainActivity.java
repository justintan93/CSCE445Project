package com.csce445project.storyintime;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
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

    //parsing key words
    static final String KEY_NODE = "node";
    static final String KEY_ID = "id";
    static final String KEY_LINE = "line";
    static final String KEY_LINK = "link";
    static final String KEY_DELAY = "delay";
    static final String KEY_CHOICE1 = "choice1";
    static final String KEY_CHOICE2 = "choice2";
    static final String KEY_TIMETEXT = "timeText";
    static final String KEY_TEXT = "text";
    static final String KEY_EMPTY = "empty";
    //node and line navigation info
   // private Button nextButton;
    int nodeId = 0;
    int lineIterator = 0;
    ArrayList<String> listText = new ArrayList<String>();
    ArrayList<Line> fullList = new ArrayList<Line>();
    int listOldSize = 0;

    ArrayList<Node> nodes = new ArrayList<Node>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        getStory();

        list = new ArrayList<View>();
        layoutInflater = ((Activity)this).getLayoutInflater();
        listView = (ListView)this.findViewById(R.id.listView);
        textAdapter = new TextAdapter(this,list);
        listView.setAdapter(textAdapter);

        setUp();



       // nextButton = (Button) mView.findViewById(R.id.nextButton);
       // nextButton.setOnClickListener(new View.OnClickListener() {
         //  public void onClick(View v) {
          //      update();
         //   }
       // });
        //addViewToListView(createText("HELLO"));
        //addViewToListView(createChoiceText("BYE", "HI", "B"));
       // addViewToListView(createLinkText("LOLASvausfjafjkasl;kfjaskl;dfj;asdlfjkasopfjiawe;fjkal;skjfopsjifapoj;lasdjKJDAL:K", "https://www.facebook.com"));
       // addViewToListView(createPlayerText("I AM PLAYER 1"));
       // addViewToListView(createTimeText("Bob is currently walking to the supermarket"));
    }

    public void addViewToListView(View v) {
        list.add(v);
        textAdapter.notifyDataSetChanged();
         listView.post(new Runnable(){
            @Override
            public void run() {
                 listView.setSelection(textAdapter.getCount()-1);
             }
        });
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

    public View createLinkText(String message, String link, String image) {
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
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View l) {
                Node parent = nodes.get(nodeId);
                Choice current = parent.getLeft();
                nodeId = current.getDest();
                setUp();
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View r) {
                Node parent = nodes.get(nodeId);
                Choice current = parent.getRight();
                nodeId = current.getDest();
                setUp();
            }
        });
        return mView;
    }

    private void goToUrl(String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW,uriUrl);
        startActivity(launchBrowser);
    }

    //XML tag classes
    //line currently contains the text in the line, link if it has one, and delay if it has one otherwise delay = 1
    private class Line{
        String text;
        int delay;
        String link;
        String timeText;
        String image;
        public Line() {
            text = "empty";
            delay = 2;
            link = "empty";
        }
        void setText(String s){text = s;}
        void setDelay(int i){delay = i;}
        void setTimeText(String t){timeText = t;}
        void setLink(String s){link = s;}
        void setImage(String i){image = i;}
        int getDelay(){return delay;}
        String getTimeText(){return timeText;}
        String getText(){return text;}
        String getLink(){return link;}
        String getImage(){return image;}
        Boolean hasLink(){
            if(!link.equals(KEY_EMPTY)){
                return true;
            }
            return false;
        }
    }
    //contains text body of choice, and the destination node for the choice
    private class Choice{
        int destID;
        String text;
        public Choice(){};
        int getDest(){return destID;}
        String getText(){return text;}
        void setDest(int i){destID = i;}
        void setText(String s){text = s;}
    }
    //contains the unique id, array of lines, and two choices
    private class Node{
        int id;
        Choice left;
        Choice right;
        ArrayList<Line> lines;

        public Node() {
            lines = new ArrayList<Line>();
            left = new Choice();
            right = new Choice();
        }
        void setId(int i){id = i;}
        void setLeft(Choice c){left = c;}
        void setRight(Choice c){right = c;}
        void addLine(Line l){lines.add(l);}
        int getId(){return id;}
        int getLeftDest(){return left.getDest();}
        int getRightDest(){return right.getDest();}
        Choice getLeft(){return left;}
        Choice getRight(){return right;}
        String getLeftText(){return left.getText();}
        String getRightText(){return right.getText();}
        ArrayList<Line> getLines(){return lines;}

    }
    private void getStory() {
        try {
            XmlResourceParser xpp = getResources().getXml(R.xml.storyintime);
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT){
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    System.out.println("Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    System.out.println("Start tag " + xpp.getName());
                    if(xpp.getName().equals(KEY_NODE)){
                        Node node = new Node();
                        while(true){
                            if(eventType == XmlPullParser.START_TAG) {
                                if(xpp.getName().equals(KEY_ID)) {
                                    while (eventType != XmlPullParser.END_TAG) {
                                        if(eventType == XmlPullParser.TEXT){
                                            node.setId(Integer.parseInt(xpp.getText().toString()));
                                        }
                                        eventType = xpp.next();
                                    }
                                }
                                else if(xpp.getName().equals(KEY_LINE)) {
                                    Line line = new Line();
                                    while(true) {
                                        if(xpp.getName().equals(KEY_TEXT)) {
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if (eventType == XmlPullParser.TEXT) {
                                                    line.setText(xpp.getText().toString());
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        else if(xpp.getName().equals(KEY_DELAY)){
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if (eventType == XmlPullParser.TEXT) {
                                                    line.setDelay(Integer.parseInt(xpp.getText().toString()));
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        else if(xpp.getName().equals(KEY_LINK)){
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if (eventType == XmlPullParser.TEXT) {
                                                    line.setLink(xpp.getText().toString());
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        else if(xpp.getName().equals(KEY_TIMETEXT)){
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if (eventType == XmlPullParser.TEXT) {
                                                    line.setTimeText(xpp.getText().toString());
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        eventType = xpp.next();
                                        if(eventType == XmlPullParser.END_TAG){
                                            if(xpp.getName().equals(KEY_LINE)){
                                                break;
                                            }
                                        }
                                    }
                                    node.addLine(line);
                                }
                                else if(xpp.getName().equals(KEY_CHOICE1)) {
                                    Choice c = new Choice();
                                    while(true){
                                        if(xpp.getName().equals(KEY_ID)) {
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if(eventType == XmlPullParser.TEXT){
                                                    c.setDest(Integer.parseInt(xpp.getText().toString()));
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        else if(xpp.getName().equals(KEY_TEXT)) {
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if(eventType == XmlPullParser.TEXT) {
                                                    c.setText(xpp.getText().toString());
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        eventType = xpp.next();
                                        if(eventType == XmlPullParser.END_TAG){
                                            if(xpp.getName().equals(KEY_CHOICE1)){
                                                break;
                                            }
                                        }
                                    }
                                    node.setLeft(c);
                                }
                                else if(xpp.getName().equals(KEY_CHOICE2)) {
                                    Choice c = new Choice();
                                    while(true){
                                        if(xpp.getName().equals(KEY_ID)) {
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if(eventType == XmlPullParser.TEXT){
                                                    c.setDest(Integer.parseInt(xpp.getText().toString()));
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        else  if(xpp.getName().equals(KEY_TEXT)) {
                                            while (eventType != XmlPullParser.END_TAG) {
                                                if(eventType == XmlPullParser.TEXT) {
                                                    c.setText(xpp.getText().toString());
                                                }
                                                eventType = xpp.next();
                                            }
                                        }
                                        eventType = xpp.next();
                                        if(eventType == XmlPullParser.END_TAG){
                                            if(xpp.getName().equals(KEY_CHOICE2)){
                                                break;
                                            }
                                        }
                                    }
                                    node.setRight(c);
                                }
                            }
                            eventType = xpp.next();
                            if(eventType == XmlPullParser.END_TAG){
                                if(xpp.getName().equals(KEY_NODE)){
                                    break;
                                }
                            }
                        }
                        nodes.add(node);
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    System.out.println("End tag "+xpp.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    System.out.println("Text "+xpp.getText());
                }
                eventType = xpp.next();
            }
            xpp.close();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    void setUp(){
        Node current = nodes.get(nodeId);
        ArrayList<Line> lines = current.getLines();
        System.out.println("OUTX: " + lines.size());
        for(int i = 0; i < lines.size(); i++){
            fullList.add(lines.get(i));
        }

        update();
    }

    void update(){
       // if(fullList.size() == 1)
        if(lineIterator < fullList.size() - 1) {

            if (fullList.get(lineIterator).hasLink()) {

                addViewToListView(createLinkText(fullList.get(lineIterator).getText(), fullList.get(lineIterator).getLink(), fullList.get(lineIterator).getImage()));
            } else {
                addViewToListView(createText(fullList.get(lineIterator).getText()));
            }
            if(fullList.get(lineIterator).getDelay() != 2){
                addViewToListView(createTimeText(fullList.get(lineIterator).getTimeText()));
                System.out.println("DELAY > 1");
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    update();
                }
            }, fullList.get(lineIterator).getDelay() * 1000);
        } else{
            addViewToListView(createChoiceText(fullList.get(lineIterator).getText(),nodes.get(nodeId).getLeftText(), nodes.get(nodeId).getRightText()));
        }
        lineIterator++;
    }


}
