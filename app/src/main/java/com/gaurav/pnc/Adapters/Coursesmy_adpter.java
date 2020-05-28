package com.gaurav.pnc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.pnc.Models.Course_list_model;
import com.gaurav.pnc.R;

import java.util.List;

public class Coursesmy_adpter extends RecyclerView.Adapter<Coursesmy_adpter.viewholder> {

    Context mCtx;
    List<Course_list_model> courselist;

    public Coursesmy_adpter(Context mCtx, List<Course_list_model> courselist) {
        this.mCtx = mCtx;
        this.courselist = courselist;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.each_coursesmy, parent, false);
        viewholder holder = new viewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        final Course_list_model course = courselist.get(position);
        holder.name.setText(course.getCourse().toUpperCase());
    }

    @Override
    public int getItemCount() {
        return courselist.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        private TextView name;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.coursemy_textview);
        }
    }
}
