package com.github.maxoumime.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.maxoumime.Editor;
import com.github.maxoumime.EditorToolbar;

public class EditorTestActivity extends AppCompatActivity {
    Editor _editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_test);
        _editor= (Editor) findViewById(R.id.editor);

        _editor.setOptions(
            EditorToolbar.TOOLBAR_OPTION.BOLD,
            EditorToolbar.TOOLBAR_OPTION.ITALIC,
            EditorToolbar.TOOLBAR_OPTION.HR,
            EditorToolbar.TOOLBAR_OPTION.UNORDERED_NUMBERED,
            EditorToolbar.TOOLBAR_OPTION.BULLETED
        );

        _editor.Render();

        findViewById(R.id.btnRender).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditorTestActivity.this, _editor.getContentAsHTML(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void setGhost(Button button) {
        int radius = 4;
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setStroke(4, Color.WHITE);
        background.setCornerRadius(radius);
        button.setBackgroundDrawable(background);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Editor?")
                .setMessage("Are you sure you want to exit the editor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Button btnRender=(Button)findViewById(R.id.btnRender);
        setGhost(btnRender);
    }
}