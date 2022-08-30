package com.example.karthikfirebase.project;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.karthikfirebase.R;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> {
    Context context;
    List<NoteModel> noteModelList=new ArrayList<>();
    OnItemClickListerner onItemClickListerner;

    public NoteAdapter(Context context, List<NoteModel> noteModelList) {
        this.context = context;
        this.noteModelList = noteModelList;
    }

    public NoteAdapter(Context context, List<NoteModel> noteModelList, OnItemClickListerner onItemClickListerner) {
        this.context = context;
        this.noteModelList = noteModelList;
        this.onItemClickListerner = onItemClickListerner;
    }

    @NonNull
    @Override
    public NoteAdapter.NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root= LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_reycler,parent,false);
        return new NoteHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.NoteHolder holder, int i) {

        holder.uid.setText(noteModelList.get(i).getUid());
        holder.title.setText(noteModelList.get(i).getTitle());
        holder.desc.setText(noteModelList.get(i).getDesc());
        holder.timeStamp.setText(""+noteModelList.get(i).getTimeStamp2());

    }

    @Override
    public int getItemCount() {
        return noteModelList.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        TextView uid,title,desc,timeStamp;
        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            uid=itemView.findViewById(R.id.uid);
            title=itemView.findViewById(R.id.title);
            desc=itemView.findViewById(R.id.desc);
            timeStamp=itemView.findViewById(R.id.timeStamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListerner.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
