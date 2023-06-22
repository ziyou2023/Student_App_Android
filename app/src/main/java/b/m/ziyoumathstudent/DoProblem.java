package b.m.ziyoumathstudent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import b.m.ziyoumathstudent.databinding.DoproblemBinding;

/**
 * <h1>作答頁面</h1>
 *
 * @see Paper
 * @see ProblemMenu
 */
public class DoProblem extends AppCompatActivity {
    private final ArrayList<Bitmap> BitmapArrayProb = new ArrayList<>(); //題目圖檔陣列
    private final ArrayList<Bitmap> BitmapArrayAnsw = new ArrayList<>(); //答案圖檔陣列
    private final ArrayList<String> AnswerArray = new ArrayList<>(); //答案選項陣列
    private final ArrayList<String> choiceArray = new ArrayList<>(); //選項數陣列
    private final ArrayList<String> UserAnsArray = new ArrayList<>(); //使用者選項陣列
    private final FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private DoproblemBinding binding;
    private Integer picNo = 0;
    private String clnum, useway, topicID, schoolid, classid, studentid, usewayid, topic_name, studentname;
    private boolean isloading = false;
    private int timer = 0;
    private ArrayList<Map<String, Object>> problemsID;
    /**
     * <h1>到 {@link ProblemMenu} 用的 ActivityResultLauncher</h1>
     * <p>一般到新頁面用 {@link #startActivity(Intent) startActivity}就好
     * <p>而若是要從該頁面將資訊回傳到這個頁面則是要用{@link #startActivityForResult(Intent, int) startActivityForResult}
     * <p>這方法已被棄用，所以已改用{@link ActivityResultLauncher ResultLauncher} ( {@link ActivityResultContract ResultContract}、{@link ActivityResultCallback ResultCallback})
     * <p><h3>Launcher = {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)   register(Contract,Callback)}</h3>
     * <p><h4>{@code Contract} : </h4>這邊使用的是預設類型
     * <p>"{@link ActivityResultContracts.StartActivityForResult ResultContracts.StartForResult()}"
     * <p><h4>{@code Callback} : </h4>這邊使用 Lambda 表示式 簡化 {@link #ProblemMenuResult(ActivityResult) Callback}
     *
     * @see #startActivity(Intent)
     * @see #startActivityForResult(Intent, int)
     */
    private final ActivityResultLauncher<Intent> Problem_menu_Launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            this::ProblemMenuResult);

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
        binding = DoproblemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        binding.ClsNum.setText(clnum);
        binding.mode.setText(useway);
        SetViewListener();
        getDocument(topicID);
    }

    private void getIntentExtra() {
        //設定班級 接收school/studentid
        Intent it = getIntent();
        studentname = it.getStringExtra("studentname");
        studentid = it.getStringExtra("studentid");
        schoolid = it.getStringExtra("schoolid");
        topicID = it.getStringExtra("topicID");
        classid = it.getStringExtra("classid");
        useway = it.getStringExtra("useway");
        clnum = it.getStringExtra("clnum");
    }

    private void SetViewListener() {
        binding.Before.setOnClickListener(this::on_change_Problem);
        binding.Next.setOnClickListener(this::on_change_Problem);
        binding.ansgroup.setOnCheckedChangeListener((G, I) -> on_choice_answer(I));
        binding.paper.setOnClickListener(view -> to_paper());
        binding.allPb.setOnClickListener(view -> ToProblemMenu());
        binding.sendans.setOnClickListener(view -> sendans());
    }

    private void on_change_Problem(View v) {
        if (v == binding.Before) {
            picNo = Integer.max(picNo - 1, 0);
        }
        if (v == binding.Next) {
            picNo = Integer.min(picNo + 1, problemsID.size() - 1);
        }
        Log.d("position : ", "position : " + problemsID.size() + "[" + picNo + "]");
        ShowChoice(picNo);

    }

    private void on_choice_answer(int checkedId) {
        View[] RBS = new View[]{binding.answernull, binding.answer1, binding.answer2, binding.answer3, binding.answer4};
        View v = findViewById(checkedId);
        int pos = Arrays.asList(RBS).indexOf(v);
        String[] UC = {"0", "1", "2", "3", "4"};
        UserAnsArray.set(picNo, UC[pos]);
        Log.d("DoProblem", "UC[pos]: [" + (picNo + 1) + "] , " + UC[pos]);
        ShowChoice(picNo);

        logggg();
    }

    //從 firestore 以 題目id 取得 ansLink ,problemLink , choice,,,
    private void getDocument(String expaperid) {
        Fdb.document("school/" + schoolid + "/class/" + classid + "/expaper/" + expaperid)
                .get()
                .addOnSuccessListener(this::getArraylist);
    }

    @SuppressWarnings("unchecked")
    private void getArraylist(DocumentSnapshot document) {
        if (!document.exists()) {
            return;
        }
        Log.d("DocumentSnapshot : ", "DocumentSnapshot : " + document.getId() + "\n" + document.getString("chapter"));
        Map<String, Object> doc_map = document.getData();
        problemsID = (ArrayList<Map<String, Object>>) doc_map.get("problemsID");
        usewayid = String.valueOf(doc_map.get("usewayid"));
        topic_name = (String) doc_map.get("topic_name");
        Log.d("getArratlist : ", "getArratlist : " + problemsID.size() + "     ,       " + problemsID);
        Log.d("getArratlist : ", "getArratlist : " + usewayid);
        Log.d("getArratlist : ", "getArratlist : " + topic_name);

        new Get_bitmap_Asyn().execute(problemsID);
    }

    /**
     * Deed to doInBackground
     * <p>從 {@code "https://api.emath.math.ncu.edu.tw/problem/"} 以 ArrayList 取得 ansLink ,problemLink 對應的image(Bitmap)
     */
    private void Problem_IMG_N_ANS(@NonNull ArrayList<Map<String, Object>> pp) {
        for (int n = 0; n < pp.size(); ++n) {
            Bitmap prob_jpg = null;
            Bitmap answ_jpg = null;
            Log.d("Problem_IMG_N_ANS", "problemNum: (" + n + ") " + pp.get(n).get("problemNum"));
            try {
                prob_jpg = Glide.with(getApplicationContext()).asBitmap()
                        .load("https://api.emath.math.ncu.edu.tw/problem/" + pp.get(n).get("problemLink")).submit().get();
                answ_jpg = Glide.with(getApplicationContext()).asBitmap()
                        .load("https://api.emath.math.ncu.edu.tw/problem/" + pp.get(n).get("ansLink")).submit().get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Log.e("Problem_IMG_N_ANS", "problemNum: (" + n + ") error :" + e.getMessage());
            }
            UserAnsArray.add("0");
            AnswerArray.add("" + pp.get(n).get("answer"));
            choiceArray.add("" + pp.get(n).get("choices"));
            BitmapArrayProb.add(prob_jpg);
            BitmapArrayAnsw.add(answ_jpg);
        }
    }

    @SuppressLint("SetTextI18n")
    private void ShowChoice(int pos) {
        /*顯示題目圖形*/
        Glide.with(this).load(BitmapArrayProb.get(pos)).override(1200, 400).into(binding.imageView);
        Glide.with(this).load(BitmapArrayAnsw.get(pos)).override(1200, 400).into(binding.imageView2);
        /*顯示題目已選選項*/
        binding.answer3.setVisibility(View.VISIBLE);
        binding.answer4.setVisibility(View.VISIBLE);
        int CM = Integer.parseInt(choiceArray.get(pos));
        if (CM < 4)
            binding.answer4.setVisibility(View.INVISIBLE);
        if (CM < 3)
            binding.answer3.setVisibility(View.INVISIBLE);
        int UC = Integer.parseInt(UserAnsArray.get(pos));
        RadioButton[] answers = new RadioButton[]{binding.answernull, binding.answer1, binding.answer2, binding.answer3, binding.answer4};
        answers[UC].setChecked(true);
        /*顯示題目相關資訊*/
        binding.numTot.setText("總 :" + problemsID.size());
        binding.numNow.setText("目前 :" + (pos + 1));
        binding.numDone.setText("未答 :" + Collections.frequency(UserAnsArray, "0"));
        logggg();
    }

    private void logggg() {
        Log.d("DoProblem", "ShowChoice: " + choiceArray.get(picNo));
        Log.d("DoProblem", "UserAns: " + UserAnsArray.get(picNo));
        StringBuilder UserNow_Here = new StringBuilder(" ");
        for (int i = 0; i < problemsID.size(); ++i) {
            if (i == picNo)
                UserNow_Here.append("↓  ");
            else
                UserNow_Here.append("   ");
        }
        Log.d("DoProblem", "UserNow_Here: " + UserNow_Here);
        Log.d("DoProblem", "UserAnsArray: " + UserAnsArray);
        Log.d("DoProblem", "    ansArray: " + AnswerArray);
        Log.d("DoProblem", " choicesizes: " + choiceArray);
        Log.d("DoProblem", "       picNo: " + picNo);
    }

    private void sendans() {
        new AlertDialog.Builder(this)
                .setTitle("確認送出")
                .setMessage("完成了嗎?")
                .setPositiveButton("好了", (dialog1, which) -> {
                    WriteTofdb(topicID);
                    Intent it = getIntent().setClass(this, ScoreCheck.class);
                    startActivity(it);
                    finish();
                })
                .setNegativeButton("還沒", null)
                .create()
                .show();

    }

    private void WriteTofdb(String id) {

        Map<String, Object> topic_map = new HashMap<>();
        topic_map.put("sendtime", new Date());
        DocumentReference studentsfile = Fdb.document("school/" + schoolid + "/student/" + studentid);
        studentsfile.update("topics." + id, topic_map);
        topic_map.put("studentID", studentid);
        topic_map.put("Answer", AnswerArray);
        topic_map.put("UserAns", UserAnsArray);
        topic_map.put("topic_name", topic_name);
        topic_map.put("useway", usewayid);
        topic_map.put("topicid", topicID);
        topic_map.put("correctnum", countscore(AnswerArray, UserAnsArray));
        topic_map.put("tolnum", AnswerArray.size());
        topic_map.put("studentname", studentname);
        DocumentReference recordfile = Fdb.document("school/" + schoolid + "/class/" + classid + "/record/" + studentid + "_" + usewayid + "_" + id);
        recordfile.set(topic_map);
    }

    private int countscore(ArrayList<String> A, ArrayList<String> B) {
        int correctnum = 0;
        for (int i = 0; i < A.size(); ++i) {
            correctnum += ((A.get(i).equals(B.get(i))) ? 1 : 0);
        }
        return correctnum;
    }

    /**
     * 將擷的圖轉換成 byte 才能放進 Intent
     *
     * @see Paper
     */
    private void to_paper() {
        //getScreenShot
        Bitmap bitmap = getScreenShot();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bitmapByte = baos.toByteArray();
        Intent it = new Intent(this, Paper.class);
        it.putExtra("img", bitmapByte);
        startActivity(it);
    }

    /**
     * 藉由View來Cache全螢幕畫面後放入Bitmap
     *
     * @return {@link #to_paper()}
     */
    private Bitmap getScreenShot() {
        //藉由View來Cache全螢幕畫面後放入Bitmap
        View Window = getWindow().getDecorView();
        Window.setDrawingCacheEnabled(true);
        Window.buildDrawingCache();
        Bitmap DrawingCache = Window.getDrawingCache();
        Bitmap CreateBitmap = Bitmap.createBitmap(DrawingCache);
        //將Cache的畫面清除
        Window.destroyDrawingCache();
        return CreateBitmap;
    }

    /**
     * <h2>Callback</h2>  <h3>{@link #Problem_menu_Launcher}</h3> 處理 Result
     * <p>原本
     * <pre>{@code
     *  new ActivityResultCallback<ActivityResult>() {
     *      @Override
     *      public void onActivityResult(ActivityResult result) {
     *          if (result.getResultCode() == Activity.RESULT_OK &&
     *                  result.getData() != null) {
     *              picNo = result.getData().getIntExtra("position", picNo);
     *              ShowChoice(picNo);
     *          }
     *      }
     *  };
     * }</pre>
     * 簡化成 :
     * <p>{@code result -> ProblemMenuResult(result)}
     * <p>再簡化成 :
     * <p>{@code this::ProblemMenuResult}
     */
    private void ProblemMenuResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            picNo = result.getData().getIntExtra("position", picNo);
            ShowChoice(picNo);
        }
    }

    /**
     * 用 ActivityResultLauncher 啟動 Activity
     */
    private void ToProblemMenu() {
        Intent it = new Intent(this, ProblemMenu.class);
        it.putExtra("useway", useway);
        it.putExtra("topic_name", topic_name);
        it.putStringArrayListExtra("UserAnsArray", UserAnsArray);
        Problem_menu_Launcher.launch(it);
    }

    /**
     * <h3>顯示 <code>loading (image view)</code> 用的  <code>handler</code> 及  <code>Runnable</code></h3>
     * <p><code> bool {@link #isloading isloading}</code>
     * <p><code> int {@link #timer timer}</code>
     */
    public void OnLoading() {
        final Handler handler = new Handler();
        final int[] ID = {R.drawable.loading1, R.drawable.loading2, R.drawable.loading3};
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                timer = (timer + 1) % 3;
                binding.loading.setImageResource(ID[timer]);
                binding.loadingbg.setVisibility(isloading ? View.VISIBLE : View.INVISIBLE);
                if (isloading) handler.postDelayed(this,200);
            }
        };
        handler.post(runnable);
    }

    /**
     * AsyncTask
     * 讓 Problem_IMG_N_ANS(problemsID) 不在主執行序 改在 背景執行序工作
     * 並在前後設定顯示及隱藏loading image_view
     */
    private class Get_bitmap_Asyn extends AsyncTaskExecutorService<ArrayList<Map<String, Object>>, Long, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isloading = true;
            OnLoading();
        }

        @Override
        protected String doInBackground(ArrayList<Map<String, Object>> problemsID) {
            Problem_IMG_N_ANS(problemsID);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            isloading = false;
            Log.d("onPostExecute", "BitmapArrayAnsw : " + BitmapArrayAnsw.size());
            picNo = 0;
            ShowChoice(picNo);

        }
    }

}

/*
                                                                                .,,,,,,...................
                                                                           ,,,,,,,,,,..........................,
                                                                       .,,,,,,,,,,,,,..............................,
                                                                     ***,,,,,,,,,,,,...................................
                                                                   *****,,,,,,,,,,,......................................
                                                                 ,********,,,,,,,,,......................................,
                                                                /*******,,,,,,,,,,,,......................................,,
                                                               //*********,,,,,,,,,,,,,,,,,,,............................,,,,
                                                              //&/********,,,,,,,,,,,,,,,,,,,,,,.....................,,,,,,*,,
                                                             * ///#/%&/*****,,,,,,,,,,,,,,,,,,,,,,,,,,..............,,(&%*,,/,*,
                                                            .* /@/&&@@&/******,,,,,,,,,,,,,,,,,,,,,,,,,,,.,,,.....,,*#%&%.(/,,*,
                                                            ** /(@@@@@%/*********,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,/&%&@&&,/,,,,
                                                           ,***%&*#&@//********,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*(%(.(&&%,,,,,
                                                          .*** /&%&&(//**********,,,,,,,,,,,,* //(((((/*,.....,,,,,,,,,*%&&&%,......,
                                                         ******* ///************,,,,,/#(((((((((((((((((((((((*.,,,,,,,,,,,,........,
                                                        ,*******,***************###((((((((((((((((((((((((((((((*,,,,,,............,
                                                        ******************** /##(((((((((((((((((((((((((((((((((((((*,,,,...........,,
                                                       .***************** /(##((((((((########################(((((((((/,,,,,,,,,,,,,,,
                                                        ************** /(((((((((################################(((((((//,,,,,,,,,,,,,
                                                        *************(((((((#######%%%%%%%%%%%%%%%%%%%%%%%%%######((((((//*,,,,,,,,,,
                                                         ************(((((######%%%%%%%%%%%%%%%############%%%%%##((((((((/*,,,,,,,,
                                                          *********** /(#########%%%%%####(((#####((//////(#######(((((((//*,,,,,,,.
                                                      .** ///***********(#############%%%%%%%%%%%%#%##%###%###((((////////*,,,,,,.
                    .**,,,,,,                   .**** ///////////*********((###(((((#####################((((///////((//*,,,,,,,,,,,,,
                  /****,,,,,,,,,,,,.        ,***** ////////////////////**** //((((##((((((((((((((///////////** /((((//***********,,,,,,,,,,.                  ,*,*****.
                ,///*****,,,,,,,,,,,,,*..******** ////////////////////////////////(((((((((((((((((((((((((((((//**************,,,,,,,,,,,,,,*            .,,,,,,********
                /(///*****,,,,,,,,,,,,,,*********** //////////////////////////////////////((((((((((((((///**************,,,,,,,,,,,,,,,,,,,,,**,,,,,,,,,,,,,,,,,********* /
               ./(((///****,,,,,,,,,,,,,,***************** ////////////////////////////////////////////////////*******,,,,,,,..,...,.,,,,,,,,,,**,,,,,,,,,,,****************
                /((((///***,,,,,,,,,,,,,,,*********************** ////////////////////////////////////////////*****,,,,,,,,,.............,,,,,,**,,,,,,,,,*********** ////*,**
                //((((//***,,,,,,,,,,,,,,,,*********************** /////////////////(((((((((((((((((////////*****,,,,,,,,,..,..,...,.,,.,,,,,****,,,,,***************** //***
                 ///////***,,,,,,,,....,,,,*****,******************** //////////////(((((((((((((((///////******,,,,,,,,,..,,.,...,,..,,,,,,,,,**,,,,,,,,,,,,,,,,***********,
                  * ////******,,,,,,,,,..,,********************************* /////////////((((((////////*******,,,,,,,,,.....,.......,..,..,,,,,,,,,,,,,,,,,,,,,,,,*********,,
                   *************,,,,,,,,****************,,,*,**************************** //////**********,,,,,,,.......,............,,,,,,,,,,,,,,,,,,,,,,,,,,,,,**,,,,,,,,
                    .**************,,,******************,,,,,,*,,,,,,,,,,,,,,*,*****************,,,,,,,,,,,..........................,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.
                      ,*************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.................................,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                        .**************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.....................................,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                           ***************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,.,...............................,.,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                              ,***************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,.,,................,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                  ./***************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                    ,//****************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                     .///***************************************,***,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*,,
                                       ////***********************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,
                                        /////**************************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*
                                          (////***********************************************************,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,**
                                            (////*****************************************************,*,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,*,
                                              ////////**********************************************************,**********,,,,,,,,,,,,,,,,,,,***,
                                                 *(////////*********************************************************************,,,,,,,,,,*****
                                                     .#/////////****************************************************************,********** /
                                                           *((/////////*****************************************************************
                                                                  ,(((////////************************************************* //,
                                                                            ,(#((////////////******************** /////(/,
*/