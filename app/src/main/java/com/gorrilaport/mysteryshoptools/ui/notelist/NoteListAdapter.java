package com.gorrilaport.mysteryshoptools.ui.notelist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.core.listeners.NoteItemListener;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.util.Constants;
import com.gorrilaport.mysteryshoptools.util.TimeUtils;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;


import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteListAdapter extends UltimateViewAdapter {
    private List<Note> mNotes;
    private final Context mContext;
    private NoteItemListener mItemListener;
    private View noteView;


    public NoteListAdapter(List<Note> notes, Context mContext) {
        mNotes = notes;
        this.mContext = mContext;
    }

    public void remove(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onItemDismiss(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        noteView = inflater.inflate(R.layout.custom_row_note_list, parent, false);
        return new ViewHolder(noteView);
    }

    @Override
    public UltimateRecyclerviewViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public UltimateRecyclerviewViewHolder newHeaderHolder(View view) {
        return new UltimateRecyclerviewViewHolder<>(view);
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        noteView = inflater.inflate(R.layout.custom_row_note_list, parent, false);
        ViewHolder vh = new ViewHolder(noteView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Note note = mNotes.get(position);

        ((ViewHolder) holder).title.setText(note.getTitle());
        ((ViewHolder) holder).noteDate.setText(TimeUtils.getTimeAgo(note.getDateModified()));


        ((ViewHolder) holder).title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onNoteClick(note);
            }
        });

        ((ViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemListener.onDeleteButtonClicked(note);
            }
        });

        try {
            if (note.getNoteType().equals(Constants.NOTE_TYPE_AUDIO)) {
                Glide.with(mContext).load(R.drawable.headphone_button).into(((ViewHolder) holder).noteCircleIcon);
            } else if (note.getNoteType().equals(Constants.NOTE_TYPE_REMINDER)) {
                Glide.with(mContext).load(R.drawable.appointment_reminder).into(((ViewHolder) holder).noteCircleIcon);
            } else if (note.getNoteType().equals(Constants.NOTE_TYPE_IMAGE)) {
                //Show the image
            } else {                   //Show TextView Image

                String firstLetter = note.getTitle().substring(0, 1);
                ColorGenerator generator = ColorGenerator.MATERIAL;
                int color = generator.getRandomColor();

                ((ViewHolder) holder).noteCircleIcon.setVisibility(View.GONE);
                ((ViewHolder) holder).noteIcon.setVisibility(View.VISIBLE);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(firstLetter, color);
                ((ViewHolder) holder).noteIcon.setImageDrawable(drawable);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public Note getItem(int position) {
        return mNotes.get(position);
    }

    public void replaceData(List<Note> notes) {
        setList(notes);
        notifyDataSetChanged();
    }

    private void setList(List<Note> notes) {
        mNotes = notes;
    }

    public void setNoteItemListener(NoteItemListener listener) {
        mItemListener = listener;
    }

    public void setOnDragStartListener(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;

    }


    public class ViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_note_title)
        TextView title;

        @BindView(R.id.text_view_note_date)
        TextView noteDate;

        @BindView(R.id.image_view_expand)
        ImageView delete;

        @BindView(R.id.image_view)
        ImageView noteIcon;
        @BindView(R.id.circle_image_view)
        ImageView noteCircleIcon;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Note note = getItem(position);
            mItemListener.onNoteClick(note);
        }

        public Note getNote() {
            return getItem(getAdapterPosition());
        }
    }
}

