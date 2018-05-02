package com.example.eeyjj3.mrc11.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;

import java.util.List;

import com.example.eeyjj3.mrc11.Models.ChatModel;
import com.example.eeyjj3.mrc11.R;


/**
 * Created by eeyjj3 on 28/04/2018.
 * Used to display the result
 */

public class ResultAdapter extends BaseAdapter {

    private List<ChatModel> list_chat_models;
    private Context context;
    private LayoutInflater layoutInflater;

    public ResultAdapter(List<ChatModel> list_chat_models, Context context) {
        this.list_chat_models = list_chat_models;
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }//constructor

    @Override
    public int getCount() {
        return list_chat_models.size();
    }

    @Override
    public Object getItem(int position) {
        return list_chat_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null)//check the message is received or sent.
        {
            //if the message is sent from the user, post the message into list_item_send
            //if the message is received from the API, post the message into list_item_receive
            if(list_chat_models.get(position).isSend)
                view = layoutInflater.inflate(R.layout.list_item_send,null);
            else
                view = layoutInflater.inflate(R.layout.list_item_receive,null);
            //put the message in the chat bubble.
            BubbleTextView text_message = (BubbleTextView)view.findViewById(R.id.text_message);
            text_message.setText(list_chat_models.get(position).message);
            TextView time=(TextView)view.findViewById(R.id.time);
            time.setText(list_chat_models.get(position).getTime());

        }
        return view;
    }
}