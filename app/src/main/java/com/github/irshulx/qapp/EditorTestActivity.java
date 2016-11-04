package com.github.irshulx.qapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.github.irshulx.Editor;
import com.github.irshulx.EditorCore;
import com.github.irshulx.EditorToolbar;
import com.github.irshulx.models.EditorTextStyle;

import java.io.IOException;

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
                /*
                Retrieve the content as serialized, you could also say getContentAsHTML();
                */
                String text= _editor.getContentAsSerialized();
                Intent intent=new Intent(getApplicationContext(), RenderTestActivity.class);
                intent.putExtra("content", text);
                startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == _editor.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                _editor.InsertImage(bitmap);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
           // _editor.RestoreState();
        }
        else if(requestCode== _editor.MAP_MARKER_REQUEST){
            _editor.InsertMap(data.getStringExtra("cords"));
        }
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