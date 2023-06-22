package b.m.ziyoumathstudent;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import b.m.ziyoumathstudent.databinding.Teamrace2Binding;

public class TeamRace2 extends AppCompatActivity {

    private Teamrace2Binding binding;
    String clnum , Answer , school,studentid;
    Button[] check;
    /*
    Button[] check;

    {
        assert binding != null;
        check = new Button[]{binding.choose1,binding.choose2,binding.choose3,binding.choose4};
    }

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Teamrace2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        check = new Button[]{binding.choose1,binding.choose2,binding.choose3,binding.choose4};
        //設定班級 接收school/studentid
        Intent it = getIntent();
        clnum = it.getStringExtra("clnum");
        school = it.getStringExtra("school");
        studentid = it.getStringExtra("studentid");
        binding.ClsNum.setText(clnum);

        binding.finish.setOnClickListener(view -> finish());

        /*
        for(int s = 0 ; s<4 ; s++){

            int finalS = s;
            check[s].setOnClickListener(view -> Answer(finalS));
        }
        */
/*
        binding.choose1.setOnClickListener(view -> Answer(0) );
        binding.choose2.setOnClickListener(view -> Answer(1) );
        binding.choose3.setOnClickListener(view -> Answer(2) );
        binding.choose4.setOnClickListener(view -> Answer(3) );


 */

    }
    /*
    private void Answer(Integer i){

        for (int j = 0; j < 4 ; ++j){
            if (j==i){
                continue;
            }
            else{
                check[j].setBackgroundColor(Color.blue(2));
            }
        }

        Answer  = Integer.toString(i);
    }

     */


    public void finish(){
        Intent it = new Intent(this,TeamRace3.class);
        it.putExtra("clnum",clnum);
        it.putExtra("school",school);
        it.putExtra("studentid",studentid);
        startActivity(it);
    }
}