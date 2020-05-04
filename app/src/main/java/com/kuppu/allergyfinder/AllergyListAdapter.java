package com.kuppu.allergyfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergyListAdapter extends RecyclerView.Adapter<AllergyListAdapter.AllergyViewHolder> {

    public interface OnDeleteClickListener{
        void OnDeleteClickListener(AllergyNote myNote);
    }

    private final LayoutInflater layoutInflater;
    private Context mContext;
    private List<AllergyNote> mallergyNotes;
    private OnDeleteClickListener onDeleteClickListener;

    public AllergyListAdapter(Context context,OnDeleteClickListener listener) {
        layoutInflater=LayoutInflater.from(context);
        mContext=context;
        this.onDeleteClickListener=listener;

    }

    @NonNull

    public AllergyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.list_item, parent, false);
        AllergyViewHolder viewHolder = new AllergyViewHolder(itemView);
        return viewHolder;
    }


    public void onBindViewHolder(@NonNull AllergyViewHolder holder, int position) {

        if (mallergyNotes != null) {
            AllergyNote allergynote = mallergyNotes.get(position);
            holder.setData(allergynote.getName(), position);
            holder.setListeners();
        } else {
            // Covers the case of data not being ready yet.
            holder.noteItemView.setText(R.string.no_note);
        }
    }


    @Override
    public int getItemCount() {
        if (mallergyNotes != null)
            return mallergyNotes.size();
        else return 0;
    }
    public void setNotes(List<AllergyNote> notes){
        mallergyNotes=notes;
        notifyDataSetChanged();
    }
    public class AllergyViewHolder extends RecyclerView.ViewHolder{
        private TextView noteItemView;
        private int mPosition;
        private ImageView imgDelete, imgEdit;


        public AllergyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteItemView = itemView.findViewById(R.id.txvNote);
            imgDelete 	 = itemView.findViewById(R.id.ivRowDelete);
            imgEdit 	 = itemView.findViewById(R.id.ivRowEdit);
        }

        public void setData(String note, int position) {
            noteItemView.setText(note);
            mPosition = position;
        }

        public void setListeners() {
            imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext,EditNoteItemActivity.class);
                    intent.putExtra("note_id",mallergyNotes.get(mPosition).getId());
                    ((Activity)mContext).startActivityForResult(intent,ProfileActivity.UPDATE_NOTE_ACTIVITY_REQUEST_CODE);
                }
            });
            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDeleteClickListener!=null){
                        onDeleteClickListener.OnDeleteClickListener(mallergyNotes.get(mPosition));
                    }
                }
            });
        }
    }
}
