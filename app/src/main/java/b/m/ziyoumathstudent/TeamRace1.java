package b.m.ziyoumathstudent;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.Teamrace1Binding;

public class TeamRace1 extends AppCompatActivity {

    private Teamrace1Binding binding;
    String clnum,school,studentid , grade;
    String[] Grade = new String[]{"1", "2", "3", "4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Teamrace1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        school = it.getStringExtra("school");
        studentid = it.getStringExtra("studentid");

        binding.A.setOnClickListener(v -> Teamchoose("A"));
        binding.B.setOnClickListener(view -> Teamchoose("B"));
        binding.C.setOnClickListener(view -> Teamchoose("C"));
        binding.D.setOnClickListener(view -> Teamchoose("D"));

        binding.startrace.setOnClickListener(view -> start());

    }

    private void Teamchoose(String team) {
        @SuppressLint("ResourceType") AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("確定加入" + team + " ?");

        builder.setSingleChoiceItems(Grade, 0, (d, v)->{

            grade = Grade[v];
        });
        builder.setPositiveButton("確定" , new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (grade == null) grade = Grade[0];
                binding.chooseTeam.setVisibility(View.INVISIBLE);
                binding.checkteam.setText("Team " + team+grade );
                //binding.checkteam.setText("Team " + team + Grade[i]+ grade );
                binding.checkteam.setGravity(View.TEXT_ALIGNMENT_CENTER);
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    public void start(){
        Intent it = new Intent(this,TeamRace2.class);
        it.putExtra("clnum",clnum);
        it.putExtra("school",school);
        it.putExtra("studentid",studentid);
        startActivity(it);
    }


}