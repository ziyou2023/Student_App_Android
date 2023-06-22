package b.m.ziyoumathstudent;

import static android.text.InputType.TYPE_CLASS_NUMBER;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    String[] info = {"請輸入學號(座號)", "請輸入班級(X年X班)", "請輸入校名(xx縣/市oo國小)"};
    String[] infosave = new String[3];

    private ActivityMainBinding binding;
    SQLiteDatabase db;
    static final String db_class_name = "ZyStudentDB"; /*資料庫名稱*/
    static final String tb_class_name = "PBList"; /*資料表名稱*/

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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signin.setOnClickListener(view -> signin());
        binding.signup.setOnClickListener(view -> gotosignup());

        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        String creatTable = " CREATE TABLE IF NOT EXISTS " +
                tb_class_name +
                "(_id INTEGER primary key autoincrement, " +
                " SCHOOL     VARCHAR(32) ," +
                " CLASSID     VARCHAR(32) ," +
                " CLASSNAME     VARCHAR(32) ," +
                " STUDENTID  VARCHAR(32) )";
        db.execSQL(creatTable);
        db.close();
    }

    public void signin() {
        Intent it = new Intent(MainActivity.this, Signin.class);
        startActivity(it);
        //this.finish();
    }

    private void gotosignup(){
        Intent it = new Intent(MainActivity.this, Signup.class);
        startActivity(it);
        //this.finish();
    }

    public void showsignupdialog() {
        for (int s = 0; s < 3; s++) {
            Dialog(info[s], s);
        }
    }

    public void Dialog(String title, int t) {
        EditText editText = new EditText(MainActivity.this);
        editText.setSingleLine(true);
        if(t != 1) editText.setInputType(TYPE_CLASS_NUMBER);

        AlertDialog Dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("確定", (D, i) -> {
                    String input = editText.getText().toString().trim();
                    if (input.equals("")) {
                        Toast.makeText(this, "不可以空白", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    infosave[t] = input;
                    if (t == 0) {
                        signup();
                    }
                })
                .setNegativeButton("取消", null)
                .create();
        Dialog.show();
    }

    @SuppressLint("Range")
    private void signup() {
        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        @SuppressLint("Recycle")
        Cursor c = db.rawQuery("SELECT*FROM " + tb_class_name, null);
        boolean signupbefore = false;
        while ((!signupbefore) && c.moveToNext()) {
            signupbefore = (infosave[2].equals(c.getString(c.getColumnIndex("SCHOOL"))) &&
                            infosave[1].equals(c.getString(c.getColumnIndex("CLASS"))) &&
                            infosave[0].equals(c.getString(c.getColumnIndex("STUDENTID"))));
        }
        c.close();
        if (signupbefore) {
            signin();
            return;
        }
        ContentValues cv = new ContentValues(1);
        cv.put("SCHOOL", infosave[2]);
        cv.put("CLASS", infosave[1]);
        cv.put("STUDENTID", infosave[0]);
        db.insert(tb_class_name, null, cv);
        db.close();
        Intent it = new Intent(MainActivity.this, ClassMain.class);
        it.putExtra("schoolid", infosave[2]);
        it.putExtra("clnum", infosave[1]);
        it.putExtra("studentid", infosave[0]);
        startActivity(it);
    }

}