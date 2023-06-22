package b.m.ziyoumathstudent;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import b.m.ziyoumathstudent.databinding.ScorecheckBinding;

public class ScoreCheck extends AppCompatActivity {
    private ScorecheckBinding binding;

    private final FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    String clnum, mode, schoolid, studentid,classid;
    private MatrixCursor cursor;
    private SimpleCursorAdapter adapter;
    private final String[] cursor_ColN = {"_id", "topicname", "sendtime", "score"};

    ArrayList<String> AnswerArray = new ArrayList<>(); //答案選項陣列
    ArrayList<String> UserAnsArray = new ArrayList<>(); //使用者選項陣列

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        binding = ScorecheckBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級模式
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        mode = it.getStringExtra("mode");
        schoolid = it.getStringExtra("schoolid");
        classid = it.getStringExtra("classid");
        studentid = it.getStringExtra("studentid");

        Log.d("UserchoiceArray",UserAnsArray.toString());


        binding.ClsNum.setText(clnum);
        binding.Back.setOnClickListener(view -> finish());
        set_listview();
        getQuery();

    }
    private void set_listview() {
        cursor = new MatrixCursor(cursor_ColN);

        String[] from1 = { "topicname", "sendtime", "score"};

        int[] to = {R.id.Text1, R.id.Text2, R.id.Text3};
        adapter = new SimpleCursorAdapter(ScoreCheck.this, R.layout.list_record, cursor, from1, to, 0);

        binding.HWStuDentList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void getQuery() {
        Fdb.collection("school/" + schoolid + "/class/" + classid + "/record")
                .whereEqualTo("studentID",studentid)
                .whereEqualTo("useway","2")
                .get()
                .addOnSuccessListener(this::recordQuery)
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Error getting expaper query", Snackbar.LENGTH_LONG).show();
                    Log.w("queryDocumentSnapshot", "Error getting expaper query", e);
                });
    }
    private void  recordQuery(@NonNull QuerySnapshot querySnapshots) {
        Log.d("QuerySnapshot", querySnapshots.toString());
        cursor = new MatrixCursor(cursor_ColN);

        int i = 0;
        for (QueryDocumentSnapshot Doc : querySnapshots) {
            Log.d("queryDocumentSnapshot", Doc.getId());
            int cornum = (Doc.get("correctnum")==null)? 0 : Doc.get("correctnum", int.class);
            int tolnum = (Doc.get("tolnum")==null)? 1 : Doc.get("tolnum", int.class);


            double score = ((float) cornum / (float) tolnum) * 100;


            //將取到的Date轉成字串用於列表顯示 {"_id", "topicname" , "sendtime" ,"score"};
            cursor.newRow()
                    .add("_id", i)
                    .add("topicname", Doc.get("topic_name"))
                    .add("sendtime", DateString(Doc.getDate("sendtime")))
                    .add("score", score);
            i++;
        }
        adapter.changeCursor(cursor);
        adapter.notifyDataSetChanged();
    }

    private void countscore(ArrayList<String> ans,ArrayList<String> userchoice){
        double score = 0 ;
        for ( int i = 0 ; i < userchoice.size() ; ++i ){
            if ( userchoice.get(i).equals( ans.get(i) ) ){
                score = score + (float)100.0*( 1.0/userchoice.size() );
            }
        }
        binding.Score.setText( Integer.toString((int)Math.round(score) ) ) ;
    }

    private String DateString(@Nullable Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(date);
    }






}