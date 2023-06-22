package b.m.ziyoumathstudent;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import b.m.ziyoumathstudent.databinding.Personalrace1Binding;

public class PersonalRace1 extends AppCompatActivity {
    final Handler countdown_handler = new Handler();
    private final String TAG = "PersonalRace1";
    private final FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    String clnum, schoolid, classid, studentid, Roomid;
    String topicsize ,now_topic, now_ans, user_ans;
    long stopwatch, time;
    ListenerRegistration inroom;
    DocumentReference recordDoc;
    boolean just_arrived = true;
    private Personalrace1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        binding = Personalrace1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        schoolid = it.getStringExtra("schoolid");
        classid = it.getStringExtra("classid");
        studentid = it.getStringExtra("studentid");


        binding.back.setOnClickListener(view -> finish());
        binding.start.setOnClickListener(null);
        getroom();
    }

    @Override
    protected void onDestroy() {
        if (inroom != null) {
            inroom.remove();
        }
        if (recordDoc != null){
            recordDoc.update("log." + DateString(new Date()) ,"leave room");
        }
        super.onDestroy();
    }

    //從firestore 以 題目id 取得 ansLink ,problemLink , choice,,,
    private void getroom() {
        if (!haveInternet()) return;
        if (inroom != null) inroom.remove();

        inroom = Fdb.collection("school/" + schoolid + "/class/" + classid + "/gameroom")
                .whereEqualTo("state.end", false)
                .limit(1)
                .addSnapshotListener(MetadataChanges.INCLUDE,this::getting_document);
    }

    @SuppressWarnings("unchecked")
    private void getting_document(QuerySnapshot Query, FirebaseFirestoreException e) {
        //系統報錯
        if (e != null) {
            Log.d(TAG, "getting_document: Exception : " + e.getMessage());
            showSnackbar(e.getMessage());
            return;
        }
        Log.d(TAG,"Query.getMetadata().isFromCache() :   "+Query.getMetadata().isFromCache());
        //排除暫存的數據
        if (Query.getMetadata().isFromCache()){
            return;
        }
        //沒有房間
        if (Query.isEmpty()) {
            showSnackbar("目前無競賽");
            if (inroom != null) inroom.remove();
            finish();
            return;
        }
        DocumentSnapshot doc = Query.getDocuments().get(0);
        if (doc == null || !doc.exists()) {
            showSnackbar("目前無競賽");
            finish();
            return;
        }
        Log.d(TAG, "getting_document: " + doc.getId());
        Log.d(TAG, "getting_document: " + doc.getData());
        Log.d(TAG, "getting_document: " + doc.getReference().getPath());
        //老師已關閉該房間
        if (Boolean.TRUE.equals(doc.getBoolean("state.end"))) {
            showSnackbar("活動已結束");
            inroom.remove();
            finish();
            return;
        }
        Roomid = doc.getId();
        binding.RoomID.setText(Roomid);
        //房間未開啟
        if (Boolean.FALSE.equals(doc.getBoolean("state.open"))) {
            new AlertDialog.Builder(this)
                    .setTitle("房間未開啟")
                    .setPositiveButton( "確定", (D,i) -> finish() )
                    .show();
            return;
        }
        //初次進入房間
        if (just_arrived) {
            showSnackbar("進入房間");
            just_arrived = false;
            //設定答題資料
            recordDoc = doc.getReference().collection("record").document(studentid);
            recordDoc.get().addOnSuccessListener(docs -> {
                if (!docs.exists()) {
                    recordDoc.set(new HashMap<String, Object>() {{
                        put("first_time", new Date());
                        put("studentID", studentid);
                    }});
                }else{
                    recordDoc.update("log." + DateString(new Date()) ,"Into room");
                }
            });
        }


        //取得資料
        Long now_time = doc.getLong("now.time");
        time = (now_time != null) ? now_time : 0;
        now_topic = doc.getString("now.topic");
        int nowtopic = Integer.valueOf(now_topic)+1;
        topicsize =  ""+doc.getLong("topic.size");
        binding.problemNum.setText((nowtopic + "/" + topicsize));
        String problemNum = doc.getString("topic." + now_topic + ".problemNum");
        String problemLink = doc.getString("topic." + now_topic + ".problemLink");
        String ansLink = doc.getString("topic." + now_topic + ".ansLink");
        Log.d(TAG, "now_topic : " + now_topic);

        //房間活動中
        if (Boolean.TRUE.equals(doc.getBoolean("state.active"))) {
            binding.problemNum.setText(problemNum);
            user_ans = "0";
            RadioGroupReset(doc.getString("topic." + now_topic + ".choices"));
            countdown(time);
            Glide.with(this).load("https://api.emath.math.ncu.edu.tw/problem/" + problemLink).override(1200, 400).into(binding.imageView);
            Glide.with(this).load("https://api.emath.math.ncu.edu.tw/problem/" + ansLink).override(1200, 400).into(binding.imageView2);
            return;
        }

        countdown(0);
        //下一題準備中
        if (Boolean.TRUE.equals(doc.getBoolean("state.prepare"))) {
            showSnackbar("下一題準備中");
            binding.imageView.setImageResource(0);
            binding.imageView2.setImageResource(0);
            return;
        }
        //這一題結束，顯示結果
        showSnackbar("時間到");
        now_ans = doc.getString("topic." + now_topic + ".answer");
        Log.d(TAG, "now_ans.equals(user_ans) : " + Objects.equals(now_ans,user_ans));
        binding.imageView.setImageResource(Objects.equals(now_ans,user_ans)? R.drawable.good_owl: R.drawable.bad_owl);
        if(Objects.equals(user_ans, "0")){
            binding.imageView.setImageResource(R.drawable.tired_owl);
        }
    }

    private void RadioGroupReset(String choice) {
        if (choice == null) {
            choice = "4";
        }
        int CM = Integer.parseInt(choice);
        if (CM < 4)
            binding.answer4.setVisibility(View.INVISIBLE);
        if (CM < 3)
            binding.answer3.setVisibility(View.INVISIBLE);
        binding.RG.setOnCheckedChangeListener(null);
        binding.answernull.setChecked(true);
        binding.RG.setOnCheckedChangeListener((RG, checkedId) -> updateRecord(checkedId));
    }

    private void countdown(long timer) {
        stopwatch = timer;
        countdown_handler.removeCallbacks(CD_Runnable);
        countdown_handler.post(CD_Runnable);
    }

    private void updateRecord(int checkedId) {

        View[] RBS = new View[]{binding.answernull, binding.answer1, binding.answer2, binding.answer3, binding.answer4};
        View v = findViewById(checkedId);
        int pos = Arrays.asList(RBS).indexOf(v);
        String[] UC = {"0", "1", "2", "3", "4"};
        user_ans = UC[pos];
        recordDoc.update("log." + DateString(new Date()) ,"sand answer");
        recordDoc.update("topic." + now_topic + ".ansuser", user_ans);
        recordDoc.update("topic." + now_topic + ".anstrue", now_ans);
        recordDoc.update("topic." + now_topic + ".time", (time - stopwatch));
        countdown(0);
    }

    //Snackbar 的簡化程式 縮減至只要放入字串id
    private void showSnackbar(@StringRes int errorMessageRes) {
        //Toast.makeText(this, errorMessageRes, Toast.LENGTH_SHORT).show();
        Snackbar.make(binding.getRoot(), errorMessageRes, Snackbar.LENGTH_LONG)
                .setAction("知道了", V -> Log.i("showSnackbar", getString(errorMessageRes)))
                .show();
        Toast.makeText(this, errorMessageRes,Toast.LENGTH_SHORT).show();
        Log.i("showSnackbar", getString(errorMessageRes));
    }

    //Snackbar 的簡化程式 縮減至只要放入字串
    private void showSnackbar(String errorMessage) {
        //Toast.makeText(this, errorMessageRes, Toast.LENGTH_SHORT).show();
        Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG)
                .setAction("知道了", V -> Log.i("showSnackbar", errorMessage))
                .show();
        Toast.makeText(this, errorMessage,Toast.LENGTH_SHORT).show();
        Log.i("showSnackbar", errorMessage);
    }

    private boolean haveInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            showSnackbar(R.string.NetWork_NA);
            return false;
        }
        return true;
    }    private final Runnable CD_Runnable = new Runnable() {
        @Override
        public void run() {
            binding.nowTime.setText("" + stopwatch);
            if (stopwatch <= 0) {
                countdown_handler.removeCallbacks(CD_Runnable);
                binding.RG.setVisibility(View.INVISIBLE);
                return;
            }
            stopwatch += -1;
            binding.RG.setVisibility(View.VISIBLE);
            countdown_handler.postDelayed(CD_Runnable, 1000);
        }
    };

    private String DateString(@Nullable Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("MM_dd_HH:mm:ss", Locale.ROOT).format(date);
    }


}