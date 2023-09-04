package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecordActivity extends AppCompatActivity {

    private ImageButton startRecordingButton;
    private TextView statusTextView;
    private Button saveButton;
    private Button reRecordButton;
    //오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;
    //오디오 파일 녹음 관련변수
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private boolean isRecording = false;
    private Uri audioUri = null; // 오디오 파일 uri
    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageView playIcon;
    /** 리사이클러뷰 */
    private AudioAdapter audioAdapter;
    private ArrayList<Uri> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        // 권한 확인 및 요청
        if (checkPermissions()) {
            init();
        }

    }
    private void init() {
        startRecordingButton = findViewById(R.id.startRecord);
        statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText("버튼을 누르면 녹음이 시작됩니다.");
        // "저장" 버튼
        Button saveButton = findViewById(R.id.saveButton);
        // "재녹음" 버튼
        Button reRecordButton = findViewById(R.id.reRecordButton);

        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recorded_audio.3gp";

        startRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    //녹음중
                    isRecording = false; // 녹음 상태 값
                    statusTextView.setText("녹음 중입니다.");
                    startRecording();
                    startRecordingButton.setImageResource(R.drawable.baseline_play_arrow_24); // 녹음 중지 아이콘으로 변경
                    // "저장" 및 "재녹음" 버튼 표시
                    saveButton.setVisibility(View.VISIBLE);
                    reRecordButton.setVisibility(View.VISIBLE);
                } else {
                    isRecording = true; // 녹음 상태 값
                    statusTextView.setText("버튼을 누르면 녹음이 시작됩니다.");
                    stopRecording();
                    startRecordingButton.setImageResource(R.drawable.baseline_pause_24); // 녹음 중 아이콘으로 변경
                    // "저장" 및 "재녹음" 버튼 숨김
                    saveButton.setVisibility(View.GONE);
                    reRecordButton.setVisibility(View.GONE);
                }
            }
        });

        // 리사이클러뷰
        RecyclerView audioRecyclerView = findViewById(R.id.recyclerview);

        audioList = new ArrayList<>();
        audioAdapter = new AudioAdapter(this, audioList);
        audioRecyclerView.setAdapter(audioAdapter);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        audioRecyclerView.setLayoutManager(mLayoutManager);

        // 커스텀 이벤트 리스너 4. 액티비티에서 커스텀 리스너 객체 생성 및 전달
        audioAdapter.setOnItemClickListener(new AudioAdapter.OnIconClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String uriName = String.valueOf(audioList.get(position));

                /*음성 녹화 파일에 대한 접근 변수 생성;
                     (ImageView)를 붙여줘서 View 객체를 형변환 시켜줌.
                     전역변수로 한 이유는
                    * */

                File file = new File(uriName);

                if(isPlaying){
                    // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                    if(playIcon == (ImageView)view){
                        // 같은 파일을 클릭했을 경우
                        stopAudio();
                    } else {
                        // 다른 음성 파일을 클릭했을 경우
                        // 기존의 재생중인 파일 중지
                        stopAudio();

                        // 새로 파일 재생하기
                        playIcon = (ImageView)view;
                        playAudio(file);
                    }
                } else {
                    playIcon = (ImageView)view;
                    playAudio(file);
                }
            }
        });
    }
    //오디오 파일 권한 체크
    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    private void startRecording() {
        //파일의 외부 경로 확인
        String recordPath = getExternalFilesDir("/").getAbsolutePath();
        // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFilePath = recordPath + "/" +"RecordExample_" + timeStamp + "_"+"audio.mp4";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //녹음 시작
        mediaRecorder.start();
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        // 파일 경로(String) 값을 Uri로 변환해서 저장
        //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
        //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
        audioUri = Uri.parse(audioFilePath);


        // 데이터 ArrayList에 담기
        audioList.add(audioUri);


        // 데이터 갱신
        audioAdapter.notifyDataSetChanged();
    }
    // 녹음 파일 재생
    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow_24, null));
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

    }

    // 녹음 파일 중지
    private void stopAudio() {
        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_24, null));
        isPlaying = false;
        mediaPlayer.stop();
    }
}