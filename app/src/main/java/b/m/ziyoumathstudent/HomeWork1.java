package b.m.ziyoumathstudent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import b.m.ziyoumathstudent.databinding.Homework1Binding;

public class HomeWork1 extends AppCompatActivity {

    private final FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    String clnum, studentid, topicID;
    private Homework1Binding binding;
    private String schoolid = "000000";
    private String classid = "000000";
    private boolean isloading = false;
    private int timer = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        binding = Homework1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        schoolid = it.getStringExtra("schoolid");
        classid = it.getStringExtra("classid");
        studentid = it.getStringExtra("studentid");
        Log.d("getIntent: ", "getIntent ID: " + clnum + "   " + schoolid + "   " + classid + "   " + studentid);
        binding.ClsNum.setText(clnum);
        binding.limittime.setOnClickListener(v -> gotodohw());
        binding.topicname.setOnClickListener(v -> gotodohw());
        binding.scorecheck.setOnClickListener(v -> gotoscorecheck());
        binding.back.setOnClickListener(v -> finish());
        getHWquery();
    }

    private void gotoscorecheck() {
        Intent it = getIntent().setClass(this, ScoreCheck.class);
        startActivity(it);
    }

    private void gotodohw() {
        Intent it = getIntent().setClass(this, DoProblem.class);
        it.putExtra("topicID", topicID);
        it.putExtra("useway", "回家作業");
        it.putExtra("account", 0);
        startActivity(it);
    }

    //todo: Get homework what need to do
    private void getHWquery() {
        isloading = true;
        OnLoading();
        Fdb.collection("school/" + schoolid + "/class/" + classid + "/expaper")
                .whereEqualTo("usewayid", 2)
                .whereEqualTo("release", true)
                .whereGreaterThan("limittime", new Date())//.whereLessThan("limittime",new Date())
                .orderBy("limittime")
                .limit(1)
                .get()
                .addOnSuccessListener(this::getHWquery2)
                .addOnFailureListener(e -> {
                    isloading = false;
                    Snackbar.make(binding.getRoot(), "Error getting expaper query", Snackbar.LENGTH_LONG).show();
                    Log.w("queryDocumentSnapshot", "Error getting expaper query", e);
                });
    }

    //getHWquery::SuccessListener
    private void getHWquery2(@NonNull QuerySnapshot querySnapshots) {
        Log.d("QuerySnapshot", querySnapshots.toString());
        isloading = false;
        if (querySnapshots.isEmpty()) {
            binding.limittime.setOnClickListener(null);
            binding.topicname.setOnClickListener(null);
            return;
        }

        DocumentSnapshot hwinfo = querySnapshots.getDocuments().get(0);
        SimpleDateFormat sddf = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        Date limtt = hwinfo.getDate("limittime");
        topicID = hwinfo.getId();
        Log.d("getHWquery2", "topicID : " + topicID);
        binding.limittime.setText(sddf.format(limtt));
        binding.topicname.setText(hwinfo.getString("topic_name"));

    }

    /**
     * <h3>顯示 <code>loading (image view)</code> 用的  <code>handler</code> 及  <code>Runnable</code></h3>
     * <p><code> bool {@link #isloading isloading}</code>
     * <p><code> int {@link #timer timer}</code>
     */
    public void OnLoading() {
        final Handler handler = new Handler();
        final int[] ID = {R.drawable.loading1, R.drawable.loading2, R.drawable.loading3};
        handler.post(new Runnable() {
            @Override
            public void run() {
                timer = (timer + 1) % 3;
                binding.loading.setImageResource(ID[timer]);
                binding.loadingbg.setVisibility(isloading ? View.VISIBLE : View.INVISIBLE);
                if (isloading) handler.postDelayed(this, 200);
            }
        });
    }
}