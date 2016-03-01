package com.rflournoy.testtimer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.rflournoy.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private Timer timer;
    private int downloadCounter;
    private TextView messageTextView;
    private TextView rssDownloadTextView;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        messageTextView = (TextView) findViewById(R.id.messageTextView);
        rssDownloadTextView = (TextView) findViewById(R.id.rssDownloadTextView);
    }

    //Overriding onPause method to cancel timer
    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        timer.purge();

    }

    private void startTimer() {
        final long startMillis = System.currentTimeMillis();
        timer = new Timer(true);

        TimerTask task = new TimerTask() {
            
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startMillis;

                final String RSSFEED = "news_feed.xml";
                try {
                    URL url = new URL("http://rss.cnn.com/rss/cnn_tech.rss");
                    InputStream in = url.openStream();

                    FileOutputStream out =
                            openFileOutput(RSSFEED, Context.MODE_PRIVATE);

                    byte[] buffer = new byte[1024];
                    int bytesRead = in.read(buffer);
                    while (bytesRead != -1) {
                        out.write(buffer, 0, bytesRead);
                        bytesRead = in.read(buffer);
                    }
                    out.close();
                    in.close();
                    downloadCounter++;
                } catch (IOException e) {

                }
                updateView(elapsedMillis);
            }
        };

        timer.schedule(task, 0, 10000);
    }



    private void updateView(final long elapsedMillis) {
        // UI changes need to be run on the UI thread
        messageTextView.post(new Runnable() {

            int elapsedSeconds = (int) elapsedMillis/1000;

            @Override
            public void run() {
                rssDownloadTextView.setText("File has been downloaded " + downloadCounter + " times");
                messageTextView.setText("Seconds: " + elapsedSeconds);
            }
        });
    }

    public void onClickStart(View view) {startTimer();}

    public void onClickStop(View view) {timer.cancel();}


}