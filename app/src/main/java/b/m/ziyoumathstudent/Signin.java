package b.m.ziyoumathstudent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;

import b.m.ziyoumathstudent.databinding.SigninBinding;

public class Signin extends AppCompatActivity {

    private SigninBinding binding;
    private final FirebaseAuth FAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FAuth.getCurrentUser();
    SQLiteDatabase db;
    Cursor c ;
    static final String db_class_name = "ZyStudentDB"; /*資料庫名稱*/
    static final String tb_class_name = "PBList"; /*資料表名稱*/


    String clnum, schoolid, studentid ,classid;

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
        binding = SigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.check.setOnClickListener(v -> Check());
    }

    //todo 登入要從 sql 改成 firebase
    @SuppressLint("Range")
    private void Check() {
        schoolid = binding.school.getText().toString();
        clnum = binding.Clnum.getText().toString();
        studentid = binding.studentid.getText().toString();

        boolean AisMatch = false;
        db = openOrCreateDatabase(db_class_name, Context.MODE_PRIVATE, null);
        c = db.rawQuery("SELECT*FROM " + tb_class_name, null);

        if (c.getCount() == 0) {
            Toast.makeText(this, "尚未註冊，請先註冊", Toast.LENGTH_SHORT).show();
            toMainActivity();
            return;
        }
        c.moveToFirst();
        do {
            AisMatch = (schoolid.equals(c.getString(c.getColumnIndex("SCHOOL"))) &&
                    studentid.equals(c.getString(c.getColumnIndex("STUDENTID"))));
            Log.d("ccccccccc", schoolid + clnum + studentid);
//            Log.d("aaaaa", c.getString(c.getColumnIndex("SCHOOL")) + c.getString(c.getColumnIndex("CLASSID")) + c.getString(c.getColumnIndex("STUDENTID")));

        } while ((!AisMatch) && c.moveToNext());
        if (AisMatch){
            classid = c.getString(c.getColumnIndex("CLASSID"));
            clnum = c.getString(c.getColumnIndex("CLASSNAME"));
            toClassMain();

            c.close();
            db.close();
            return;
        }
        Toast.makeText(this, "輸入有誤", Toast.LENGTH_SHORT).show();

        c.close();
        db.close();
    }
    private void signin2(){
        ArrayList<String> scopes = new ArrayList<String>(){
            {
                add("openid");
                add("profile");//add("fullname");
                add("email");
                add("schoolid");
                add("classinfo");
            }
        };
        new ArrayList<String>() {
            {
                add("openid");
                add("profile");
                add("email");
            }
        };
        String providerId = "oidc.sso.edu.tw" ;//https://oidc.tanet.edu.tw
        OAuthProvider SSOProvider = OAuthProvider
                .newBuilder(providerId, FAuth)
                .setScopes(scopes)
                .build();
        FAuth.startActivityForSignInWithProvider(this,SSOProvider)
                .addOnSuccessListener(
                        authResult -> {
                            Log.d("signin2", "activitySignIn:onSuccess:" + authResult.getUser());
                            user = authResult.getUser();
                            Log.w("signin2",user.getProviderId());
                            //startActivity(new Intent(Firebase_Auth.this, Firebase_Auth.class));
                            //Firebase_Auth.this.finish();
                        })
                .addOnFailureListener(
                        e -> {
                            Log.w("signin2", "activitySignIn:onFailure", e);
                        });
    }

    private void toMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void toClassMain(){
        Intent it = new Intent(this, ClassMain.class);
        it.putExtra("clnum", clnum);
        it.putExtra("schoolid", schoolid);
        it.putExtra("classid", classid);
        it.putExtra("studentid", studentid);
        startActivity(it);
        this.finish();
    }
}