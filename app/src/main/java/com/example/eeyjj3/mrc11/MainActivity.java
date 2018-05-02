package com.example.eeyjj3.mrc11;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.eeyjj3.mrc11.Adapter.ResultAdapter;
import com.example.eeyjj3.mrc11.Helper.HttpDataHandler;
import com.example.eeyjj3.mrc11.Models.ApiModel;
import com.example.eeyjj3.mrc11.Models.ChatModel;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by eeyjj3 on 28/04/2018.
 * Main Activity of the project
 */


public class MainActivity extends AppCompatActivity {

    ListView listView;//install the chat list
    EditText editText;//let user enter their message
    List<ChatModel> list_chat = new ArrayList<>();//list of chat history
    FloatingActionButton btn_send_message;
    private final int REQ_CODE_SPEECH_INPUT = 66;//length of the input message
    private String [] welcome_array;
    private double currentTime, oldTime = 0.0;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //let the user enter speech input when the voice chat item is selected.
                case R.id.navigation_voicechat:
                    promptSpeechInput();
                    return true;
                case R.id.navigation_note:
                    //use intent to switch from the main activity to note book activity
                    Intent intent_note = NoteActivity.makeIntent(MainActivity.this);
                    startActivity(intent_note);

                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.list_of_message);//chat history
        editText = (EditText)findViewById(R.id.user_message);//message entered by user
        ResultAdapter welcome;

        // add a welcome tips
        ChatModel welcomeModel;
        welcomeModel = new ChatModel(getRandomWelcomeTips(),false,getTime());
        list_chat.add(welcomeModel);
        listView.setAdapter(new ResultAdapter(list_chat,getApplicationContext()));

        btn_send_message = (FloatingActionButton)findViewById(R.id.fab);

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTime();
                //get text message and create cat model
                String text = editText.getText().toString();
                ChatModel model = new ChatModel(text,true,getTime());// user send message
                list_chat.add(model);
                new MainActivity.DataAPI().execute(list_chat);

                //remove user message
                editText.setText("");
            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private class DataAPI extends AsyncTask<List<ChatModel>,Void,String> {
        String stream = null;
        List<ChatModel> models;
        //store the message send by user.
        String text = editText.getText().toString();


        @Override
        protected String doInBackground(List<ChatModel>... params) {
            //send the user data to the API endpoint through API key.
            String url = String.format("http://api.simsimi.com/request.p?key=%s&lc=en&ft=1.0&text=%s",getString(R.string.api),text);
            models = params[0];
            HttpDataHandler httpDataHandler = new HttpDataHandler();
            stream = httpDataHandler.GetHTTPData(url);//get request respond
            return stream;
        }

        @Override//transfer the JSON code to the readable string
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            ApiModel response = gson.fromJson(s,ApiModel.class);
            // get response from API
            ChatModel chatModel = new ChatModel(response.getResponse(),false,getTime());
            models.add(chatModel);
            ResultAdapter adapter = new ResultAdapter(models,getApplicationContext());
            listView.setAdapter(adapter);
        }
    }


    //========================================================================
    //=======================Speak chat setting===============================
    // Start the speech input
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "You can speak now :)");
        //check the speech support function of the device
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            //
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Receiving speech input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //record for a certain amount of time
            case REQ_CODE_SPEECH_INPUT: {
                //start the speech recognition and store the result
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery=result.get(0);
                    //store the recorded message as the message sent by the user.
                    editText.setText(userQuery);
                }
                break;
            }
        }
    }

    //get a random welcome tip
    private String getRandomWelcomeTips(){
        String welcome_tip = null;
        welcome_array = this.getResources().getStringArray(R.array.welcome_tips);
        int index = (int)(Math.random()*welcome_array.length-1);
        welcome_tip=welcome_array[index];
        return welcome_tip;
    }

    //get the current time of a day
    private String getTime(){
        currentTime=System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date();
        String str = format.format(curDate);
        if ((currentTime-oldTime)>=5*60*1000){
            return str;
        }else{return "";}
    }


}
