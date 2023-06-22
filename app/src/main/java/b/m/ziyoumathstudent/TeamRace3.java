package b.m.ziyoumathstudent;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.Teamrace3Binding;

public class TeamRace3 extends AppCompatActivity {

    private Teamrace3Binding binding;
    String clnum,school,studentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Teamrace3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        school = it.getStringExtra("school");
        studentid = it.getStringExtra("studentid");
        binding.ClsNum.setText(clnum);

        binding.back.setOnClickListener(view -> backtomain());
    }

    public void backtomain(){
        Intent it = new Intent(this,ClassMain.class);
        it.putExtra("clnum",clnum);
        it.putExtra("school",school);
        it.putExtra("studentid",studentid);
        startActivity(it);
    }
}