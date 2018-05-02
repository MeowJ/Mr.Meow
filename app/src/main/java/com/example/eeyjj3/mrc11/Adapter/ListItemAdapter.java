package com.example.eeyjj3.mrc11.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eeyjj3.mrc11.Models.Note;
import com.example.eeyjj3.mrc11.NoteActivity;
import com.example.eeyjj3.mrc11.R;

import java.util.List;

/**
 * Created by eeyjj3 on 29/04/2018.
 * show the note list
 *
 */

class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener
{
    ItemClickListener itemClickListener;
    TextView item_title,item_description;

    public ListItemViewHolder(View itemView){
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
        item_title = (TextView)itemView.findViewById(R.id.item_title);
        item_description = (TextView)itemView.findViewById(R.id.item_description);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Delete the note?");
        contextMenu.add(0,0,getAdapterPosition(),"DELETE");
    }//The instruction when variable 'isLongClick' is one.
}

public class ListItemAdapter extends RecyclerView.Adapter<ListItemViewHolder> {

    NoteActivity mainActivity;
    List<Note> noteList;//use the model of Note, each note have an id, a title and a description.

    public ListItemAdapter(NoteActivity mainActivity, List<Note> noteList) {
        this.mainActivity = mainActivity;
        this.noteList = noteList;
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mainActivity.getBaseContext());
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ListItemViewHolder(view);
    }//show the lauout

    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {


        //set item data
        holder.item_title.setText(noteList.get(position).getTitle());
        holder.item_description.setText(noteList.get(position).getDescription());
        holder.setItemClickListener(new ItemClickListener(){//check the ListClickListener(), it might be wrong
            @Override
            public void onClick(View view, int position, boolean isLongClick){
                //set the Item to data to Edit text view when the user select it.
                mainActivity.title.setText(noteList.get(position).getTitle());
                mainActivity.description.setText(noteList.get(position).getDescription());
                mainActivity.isUpdate=true;//when the update flag is set
                mainActivity.idUpdate=noteList.get(position).getId();
            }
      });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
