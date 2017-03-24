package com.gorrilaport.mysteryshoptools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ricardo on 8/29/2016.
 */
public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private List<Note> mNotes;
    private Context mContext;

    public NoteListAdapter(List<Note> notes, Context context){
        mNotes = notes;
        mContext = context;
    }
    @Override
    public NoteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_recycler_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(rowView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(NoteListAdapter.ViewHolder holder, int position) {
        holder.noteTitle.setText(mNotes.get(position).getTitle());
        holder.noteContent.setText(mNotes.get(position).getTextInput());
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView noteTitle, noteContent;

        public ViewHolder(View itemView){
            super(itemView);
            noteTitle = (TextView)itemView.findViewById(R.id.text_view_note_title);
            noteContent = (TextView)itemView.findViewById(R.id.text_view_note_content);
        }
    }
}
