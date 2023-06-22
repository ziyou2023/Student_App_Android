package b.m.ziyoumathstudent;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import b.m.ziyoumathstudent.databinding.SignupBinding;

public class Signup extends AppCompatActivity {

    static final String db_class_name = "ZyStudentDB"; /*資料庫名稱*/
    static final String tb_class_name = "PBList"; /*資料表名稱*/
    private final FirebaseFirestore Fdb = FirebaseFirestore.getInstance();
    private final String[] ColumnNames = {"_id", "ID", "class_name", "book"};
    SQLiteDatabase db;
    MatrixCursor cursor;
    SimpleCursorAdapter adapter;
    private SignupBinding binding;
    private String grade, clnum, schoolid, classid, studentname;
    private String studentid;

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
        binding = SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.check.setOnClickListener(v -> showclasslist());
        binding.signup.setOnClickListener(v -> signup());

        binding.clist.setAdapter(adapter);
        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        @SuppressLint("Recycle")
        Cursor c = db.rawQuery("SELECT*FROM " + tb_class_name, null);
        if (c.getCount() > 0) {
            //finish();
            db.close();
        }
    }


    private void signin() {
        Intent it = new Intent(this, Signin.class);
        startActivity(it);
    }

    private void toclassmain() {
        Intent it = new Intent(this, ClassMain.class);
        it.putExtra("schoolid", schoolid);
        it.putExtra("classid", classid);
        it.putExtra("studentid", studentid);
        it.putExtra("clnum", clnum);
        it.putExtra("studentname", studentname);
        startActivity(it);
        this.finish();
    }

    @SuppressLint("Range")
    private void signup() {
        grade = binding.grade.getSelectedItem().toString();

        // team = binding.team.getText().toString();
        if (TextUtils.isEmpty(binding.schoolid.getText())) {
            binding.schoolid.setError("這裡不可空白");
            return;
        }
        if (binding.clist.getSelectedItem() == null) {
            Toast.makeText(this, "請先按下確認選擇班級", Toast.LENGTH_SHORT).show();
            return;
        }
        cursor.moveToPosition(binding.clist.getSelectedItemPosition());
        clnum = cursor.getString(cursor.getColumnIndex("class_name"));
        classid = cursor.getString(cursor.getColumnIndex("ID"));
        Log.d("claa_name", clnum);
        schoolid = binding.schoolid.getText().toString();
        studentid = binding.studentid.getText().toString();
        studentname = binding.studentname.getText().toString();
        Log.d("all_id", "getting query : " + grade + "," + clnum + "," + schoolid + "," + studentid);
        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        @SuppressLint("Recycle")
        Cursor c = db.rawQuery("SELECT*FROM " + tb_class_name, null);
        //check signup before
        boolean signupbefore = false;
        if (c.getCount() > 0) {
            finish();
        }
        while ((!signupbefore) && c.moveToNext()) {
            signupbefore = (schoolid.equals(c.getString(c.getColumnIndex("SCHOOL"))) &&
                    studentid.equals(c.getString(c.getColumnIndex("STUDENTID")))
            );
        }
        c.close();
        db.close();
        if (signupbefore) {
            signin();
            return;
        }
        checktosignup();
        //aftergetclassid();
        //showclasslist();

    }

    private void checktosignup() {
        new AlertDialog.Builder(this)
                .setTitle("確認註冊資訊無誤，註冊後將無法修改")
                .setPositiveButton("確定", (D, i) -> aftergetclassid())
                .setNegativeButton("取消", null)
                .show();

    }

    //取得班級
    private void showclasslist() {
        grade = binding.grade.getSelectedItem().toString();
        schoolid = binding.schoolid.getText().toString();
        studentid = binding.studentid.getText().toString();
        Fdb.collection("school/" + schoolid + "/class")
                .whereEqualTo("grade", grade)
                //.whereEqualTo("team", team)
                .get()
                .addOnSuccessListener(this::showclasslist2)
                .addOnFailureListener(e -> {
                    Snackbar.make(binding.getRoot(), "Error getting query", Snackbar.LENGTH_LONG).show();
                    Log.w("queryDocumentSnapshot", "Error getting query", e);
                });
    }

    //getclassquery::addOnSuccessListener
    private void showclasslist2(@NonNull QuerySnapshot querySnapshots) {
        cursor = new MatrixCursor(ColumnNames);
        int i = 0;
        for (QueryDocumentSnapshot Document : querySnapshots) {
            cursor.newRow()
                    .add("_id", i)
                    .add("ID", Document.getId())
                    .add("class_name", Document.getString("grade") + "年" + Document.getString("team") + "班")
                    .add("book", Document.getString("book"));
            i++;
        }
        adapter = new SimpleCursorAdapter(Signup.this, R.layout.hwbuildlist,
                cursor,
                new String[]{"class_name", "book"},
                new int[]{R.id.hwreplytime, R.id.problemname},
                0);

        binding.clist.setAdapter(adapter);
    }

    private void aftergetclassid() {

        Map<String, String> studen_data = new HashMap<String, String>() {
            {
            put("schoolid", schoolid);
            put("classid", classid);
            put("studentid", studentid);
            put("studentname", studentname);
            }
        };
        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        ContentValues cv = new ContentValues(1);
        cv.put("SCHOOL", schoolid);
        cv.put("STUDENTID", studentid);
        cv.put("CLASSNAME", clnum);
        cv.put("CLASSID", classid);

        db.insert(tb_class_name, null, cv);
        db.close();

        Fdb.document("school/" + schoolid + "/student/" + studentid)
                .set(studen_data);
        toclassmain();
    }
}