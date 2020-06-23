package com.sachin.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sachin.R;
import com.sachin.notedb.model.Recorduser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by sachin suthar 23 june 2020.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.BeanHolder> {

    private List<Recorduser> list;
    private Context context;
    private LayoutInflater layoutInflater;
    private CheckBox chkSelected;

    public RecordAdapter(List<Recorduser> list, Context context) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }


    @Override
    public BeanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.record_list_item, parent, false);
        return new BeanHolder(view);
    }

    @Override
    public void onBindViewHolder(final BeanHolder holder, final int position) {
        Log.e("bind", "onBindViewHolder: " + list.get(position));
        holder.textViewTitle.setText(list.get(position).getTitle());
        holder.textViewContent.setText(list.get(position).getContent());

        holder.chkSelected.setChecked(list.get(position).isIscheck());
        holder.chkSelected.setTag(list.get(position));

        if (list.get(position).isIscheck()) {
            holder.llmain.setBackgroundColor(context.getResources().getColor(R.color.gray));
        } else {
            holder.llmain.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        File imgFile = new File(list.get(position).getImage());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.customer)
                .error(R.drawable.customer);
        Glide.with(context).load(Uri.fromFile(imgFile)).
                apply(options).into(holder.imageView);

        holder.chkSelected.setOnClickListener(v -> {
            CheckBox cb = (CheckBox) v;
            Recorduser recorduser = (Recorduser) cb.getTag();

            recorduser.setIscheck(cb.isChecked());
            list.get(position).setIscheck(cb.isChecked());
//                notifyItemMoved(position,list.size()-1);
            if (recorduser.isIscheck()) {
                list.remove(position);
                list.add(recorduser);
                notifyDataSetChanged(); //weird behaviour of app
            } else {
                holder.llmain.setBackgroundColor(context.getResources().getColor(R.color.white));
            }

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BeanHolder extends RecyclerView.ViewHolder {

        TextView textViewContent;
        TextView textViewTitle;
        CheckBox chkSelected;
        CardView cardView;
        LinearLayout llmain;
        CircleImageView imageView;

        public BeanHolder(View itemView) {
            super(itemView);
            textViewContent = itemView.findViewById(R.id.item_text);
            textViewTitle = itemView.findViewById(R.id.tv_title);
            chkSelected = itemView.findViewById(R.id.chkbx);
            cardView = itemView.findViewById(R.id.cardview);
            llmain = itemView.findViewById(R.id.llmain);
            imageView = itemView.findViewById(R.id.imgIcon);
        }

    }

    public void filterList(ArrayList<Recorduser> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }
}