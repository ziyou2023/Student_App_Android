package b.m.ziyoumathstudent;

import static b.m.ziyoumathstudent.R.drawable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import b.m.ziyoumathstudent.databinding.ProblemMenuBinding;

/**
 * <h1>題目總覽</h1>
 * 讓使用者觀看目前題目作答狀況
 * <p>已作答 或 未作答
 * <p>並讓使用者點擊編號按鈕
 * <p>以跳至該題的題目畫面
 *
 * @see DoProblem
 */
public class ProblemMenu extends Activity {
    ProblemMenuBinding mbinding;
    LinearLayout main_Layout, sub_Layout;
    ArrayList<String> UserAnsArray = new ArrayList<>(); //使用者選項陣列
    String useway, topic_name;

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
        mbinding = ProblemMenuBinding.inflate(getLayoutInflater());
        setContentView(mbinding.getRoot());
        UserAnsArray = getIntent().getStringArrayListExtra("UserAnsArray");
        useway = getIntent().getStringExtra("useway");
        topic_name = getIntent().getStringExtra("topic_name");
        mbinding.back.setOnClickListener(view -> finish());
        int column = 7;
        setting_buttons(mbinding.LayoutProblems, UserAnsArray, column);
    }

    /**
     * 完全的從後端程式設定按鈕<code>(Button)</code>並放到前端
     * <p>也設定佈局<code>(LinearLayout)</code><p>處理按鈕的排版
     *
     * @param UserAns <code>UserAnsArray</code><p>檢查使用者是否作答過 <p>如果未作答則為<code>"0"</code>
     * @param columns 每一橫列最多有幾個按鈕，到上限時就新增一橫列
     * @see ViewGroup#addView
     */
    private void setting_buttons(LinearLayout Layout, ArrayList<String> UserAns, int columns) {
        main_Layout = Layout;
        sub_Layout = Sub_LinearLayout();
        main_Layout.addView(sub_Layout);
        for (int pos = 0; pos < UserAns.size(); pos++) {
            boolean undo = UserAns.get(pos).equals("0");
            Button owl = Button_owl(pos, undo);
            sub_Layout.addView(owl);
            if ((pos + 1) % columns == 0) {
                sub_Layout = Sub_LinearLayout();
                main_Layout.addView(sub_Layout);
            }
        }
    }

    /**
     * <code>SubLayout</code> 的一些設定<p>放在迴圈裡太醜，所以移出來了
     */
    private LinearLayout Sub_LinearLayout() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout sub_LinearLayout = new LinearLayout(this);
        //sub_LinearLayout.setBackgroundColor(getResources().getColor(color.AAFFAA));
        sub_LinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        sub_LinearLayout.setLayoutParams(params);
        return sub_LinearLayout;
    }

    /**
     * <code>Button</code> 的一些設定<p>放在迴圈裡太醜，所以移出來了
     *
     * @param position 題號
     * @param is_tired 是否有答題過
     */
    private Button Button_owl(Integer position, Boolean is_tired) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(5, 5, 5, 5);
        Button owl = new Button(this);
        owl.setBackgroundResource((is_tired) ? drawable.tired_owl : drawable.energy_owl);
        owl.setText("" + (position + 1) + "\n");
        owl.setOnClickListener(view -> to_position(position));
        owl.setTextSize(25);
        owl.setGravity(Gravity.CENTER | Gravity.BOTTOM);
        owl.setLayoutParams(params);
        return owl;
    }

    /**
     * Button 的 {@code OnClickListener}
     *
     * @param pos 題號(0~n-1)
     */
    private void to_position(int pos) {
        Intent it = new Intent().putExtra("position", pos);
        setResult(Activity.RESULT_OK, it);
        finish();
    }
}