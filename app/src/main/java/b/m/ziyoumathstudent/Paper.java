package b.m.ziyoumathstudent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.king.drawboard.view.DrawBoardView;

import b.m.ziyoumathstudent.databinding.PaperBinding;

/**
 * <h1>計算紙</h1>
 * 使用{@link DoProblem}的擷圖作為背景
 * <p>並將本介面啟動及結束時的動畫取消
 * <p>以達到看似在{@link DoProblem 原介面}出現一個畫布的效果
 * @see DrawBoardView
 * @see <a href=https://github.com/jenly1314/DrawBoard>github</a>
 */
public class Paper extends AppCompatActivity {
    private PaperBinding binding;
    private DrawBoardView Db;

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
        overridePendingTransition(0, 0);
        binding = PaperBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        settingbackground();
        Db = binding.DrawBoard;
        binding.back.setOnClickListener(v -> finish());
        binding.clear.setOnClickListener(v -> Db.clear());
        binding.pencolorgroup.setOnCheckedChangeListener((G, I) -> penChange(I));
    }

    /**
     * 將 Intent 的擷圖 (byte[]) 轉換成 bitmap
     */
    private void settingbackground(){
        byte[] bitmapByte = getIntent().getByteArrayExtra("img");
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length, null);
        BitmapDrawable bd = new BitmapDrawable(getResources(), bitmap);
        binding.img.setBackground(bd);
    }

    /**
     * 取消結束時的動畫
     */
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void penChange(int checkedId) {
        View v = findViewById(checkedId);
        Db.setDrawMode(DrawBoardView.DrawMode.ERASER);
        if (v == binding.eraser)
            return;
        Db.setDrawMode(DrawBoardView.DrawMode.DRAW_PATH);
        if (v == binding.blackPen)
            Db.setPaintColor(Color.BLACK);
        if (v == binding.redPen)
            Db.setPaintColor(Color.RED);
        if (v == binding.bluePen)
            Db.setPaintColor(Color.BLUE);
    }
}