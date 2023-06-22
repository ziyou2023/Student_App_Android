package b.m.ziyoumathstudent;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.Record1Binding;

public class Record1 extends AppCompatActivity {

    private Record1Binding binding;
    String clnum, school, studentid ;

    Cursor cur; //MatrixCursor
    SimpleCursorAdapter adapter;
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
        binding = Record1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        school = it.getStringExtra("school");
        studentid = it.getStringExtra("studentid");
        binding.ClsNum.setText(clnum);
        binding.back.setOnClickListener(view -> finish());

        //adapter.changeCursor(cur);
        binding.RecordList.setAdapter(adapter);
        //adapter.notifyDataSetChanged();

        binding.RecordList.setOnItemLongClickListener((P,V,pos,id) -> Del(id));

    }

    private boolean Del(long id) {
        AlertDialog Dialog = new AlertDialog.Builder(this)
                .setMessage("確定刪除?")
                .setTitle("提示")
                .setPositiveButton("確定", (d, i) -> {

                })
                .setNegativeButton("取消", null)
                .create();
        Dialog.show();
        return true;
    }

}