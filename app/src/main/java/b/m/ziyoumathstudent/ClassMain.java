package b.m.ziyoumathstudent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import javax.annotation.Nonnull;

import b.m.ziyoumathstudent.databinding.ClassmainBinding;

public class ClassMain extends AppCompatActivity {
    private ClassmainBinding binding;
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
        binding = ClassmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //設定班級
        Intent it = getIntent();
        String clnum = it.getStringExtra("clnum");
        binding.ClsNum.setText(clnum);
        binding.Racemode    .setOnClickListener(view -> ToActivity(PersonalRace1.class));
        binding.Homework    .setOnClickListener(view -> ToActivity(HomeWork1.class));
        binding.Record      .setOnClickListener(view -> ToActivity(Record1.class));
        binding.signout     .setOnClickListener(view -> ToActivity(MainActivity.class));

    }
    private void ToActivity(@Nonnull Class<?> Activity_class) {
        getIntent().setClass(this, Activity_class);
        startActivity(getIntent());
    }
}