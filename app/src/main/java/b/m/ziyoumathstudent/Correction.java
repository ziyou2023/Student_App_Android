package b.m.ziyoumathstudent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.CorrectionBinding;

public class Correction extends AppCompatActivity {

    private CorrectionBinding binding;
    String clnum , mode , school,studentid;;

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
        binding = CorrectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        school = it.getStringExtra("school");
        studentid = it.getStringExtra("studentid");
        mode = it.getStringExtra("mode");
        binding.ClsNum.setText(clnum);
        binding.mode.setText(mode);

        binding.back.setOnClickListener( view -> back() );
    }

    private void back() {
        Intent it = new Intent(this, ScoreCheck.class);
        it.putExtra("clnum",clnum);
        it.putExtra("mode",mode);
        it.putExtra("school",school);
        it.putExtra("studentid",studentid);
        startActivity(it);
    }
}