package com.social.preserve.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.social.preserve.R;
import com.social.preserve.model.LanguageListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页选择语言列表adapter
 * Created by Tan on 2018/12/18.
 */

public class LanguageListAdapter extends RecyclerView.Adapter<LanguageListAdapter.LanguageViewHolder>{
    private final Context mContext;
    private List<LanguageListModel> data = new ArrayList<>();
    private int checkPos = 0;
    private OnLanguageClickListener listener;
    public LanguageListAdapter(Context context, List<LanguageListModel> data) {
        this.mContext = context;
        this.data = data;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_languagelayout,parent,false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, final int position) {
        holder.textView.setText(data.get(position).language);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClick(position);
                }
             checkPos = position;
            }
        });
        if(checkPos == position){
            holder.imageView.setImageResource(R.mipmap.icon_langue_c);
        }else{
            holder.imageView.setImageResource(R.mipmap.icon_langue_f);
        }
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class LanguageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        LinearLayout layout;
        public LanguageViewHolder(View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.itemt_language_iv);
            textView = itemView.findViewById(R.id.itemt_language_tv);
            layout = itemView.findViewById(R.id.itemt_language_layout);
        }
    }
    public int getCheckPos(){
        return checkPos;
    }
    public void setCheckPos(int pos){
        this.checkPos = pos;
    }
    public LanguageListModel getCheckItem(){
        if(checkPos != -1){
            return data.get(checkPos);
        }
        return null;
    }
    public void setListener(OnLanguageClickListener listener){
        this.listener = listener;
    }

    public interface OnLanguageClickListener{
        void onClick(int position);
    }
}
