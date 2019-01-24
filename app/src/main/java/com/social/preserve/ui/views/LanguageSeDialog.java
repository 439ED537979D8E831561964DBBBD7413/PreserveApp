package com.social.preserve.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import com.social.preserve.App;
import com.social.preserve.R;
import com.social.preserve.model.LanguageListModel;
import com.social.preserve.ui.adapter.LanguageListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 语言选择弹窗
 * Created by Tan on 2018/12/18.
 */

public class LanguageSeDialog extends Dialog{
    private final Context mContext;
    private List<LanguageListModel> data = new ArrayList<>();
    private LanguageListAdapter.OnLanguageClickListener listener;
    private static final String TAG = "LanguageSeDialog";
    public LanguageSeDialog(@NonNull Context context, List<LanguageListModel> data, LanguageListAdapter.OnLanguageClickListener listener) {
        super(context, R.style.CustomDialogStyle);
        this.mContext = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(mContext).inflate(R.layout.languageselayout,null);
        RecyclerView rv = view.findViewById(R.id.languagese_rv);
        final LanguageListAdapter adapter = new LanguageListAdapter(mContext,data);
        if(data != null) {
            Log.d(TAG, "onCreate: App.getInstance().getLanguage() "+App.getInstance().getLanguage());
            for (int position = 0; position < data.size(); position++) {
                if (data.get(position).language != null && App.getInstance().getLanguage() != null) {
                    if (data.get(position).language.equals(App.getInstance().getLanguage().toString())) {
                        Log.d(TAG, "adapter.setCheckPos: ");
                        adapter.setCheckPos(position);
                    }
                }
            }
        }
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(manager);
        adapter.setListener(new LanguageListAdapter.OnLanguageClickListener() {
            @Override
            public void onClick(int position) {
                adapter.notifyDataSetChanged();
            }
        });
        rv.setAdapter(adapter);
        TextView tv = view.findViewById(R.id.languagese_tv);
        TextView title = view.findViewById(R.id.languagese_title);
        title.setText("Which language do you like most");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null){
                    listener.onClick(adapter.getCheckPos());
                }
                dismiss();
            }
        });
        this.setContentView(view);
    }
}
