package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter {

    //리사이클러뷰에 넣을 데이터 리스트
    ArrayList<Uri> dataModels;
    Context context;

    private OnIconClickListener listener = null;

    /**
     * 커스텀 이벤트 리스너
     * 클릭이벤트를 Adapter에서 구현하기에 제약이 있기 때문에 Activity 에서 실행시키기 위해 커스텀 이벤트 리스너를 생성함.
     * 절차
     * 1. 커스텀 리스너 인터페이스 정의
     * 2. 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할변수 추가
     * 3. 아이템 클릭 이벤트 핸들러 메스드에서 리스너 객체 메서드 호출
     * 4. 액티비티에서 커스텀 리스너 객체 생성 및 전달(MainActivity.java 에서 audioAdapter.setOnItemClickListener() )
     */
    // 1. 커스텀 이벤트 리스너
    public interface OnIconClickListener {
        void onItemClick(View view, int position);
    }
    // 2. 리스너 객체를 전달하는 메서드와 전달된 객체를 저장할 변수 추가
    public void setOnItemClickListener(OnIconClickListener listener) {
        this.listener = listener;
    }
    // 생성자를 통하여 데이터 리스트 context를 받음
    public AudioAdapter(Context context, ArrayList<Uri> dataModels) {
        this.dataModels = dataModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 자신이 만든 itemView를 inflate한 다음 뷰홀더 생성
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);

        // 생성된 뷰홀더 리턴, onBindViewHolder에 전달
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        String uriName = String.valueOf(dataModels.get(position));
        File file = new File(uriName);

        myViewHolder.audioTitle.setText(file.getName());

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton audioBtn;
        TextView audioTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            audioBtn = itemView.findViewById(R.id.playBtn_itemAudio);
            audioTitle = itemView.findViewById(R.id.audioTitle_itemAudio);

            audioBtn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 3. 아이템 클릭 이벤트 핸들러 메소드에서 리스너 객체 메서드 호출
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (listener != null) {
                            listener.onItemClick(view, pos);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // 데이터 리스트 크기 전달
        return dataModels.size();
    }

}