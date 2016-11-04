package com.github.irshulx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import java.util.Arrays;
import java.util.List;

public class EditorToolbar extends HorizontalScrollView {

    public enum TOOLBAR_OPTION {
        H1(R.id.action_h1),
        H2(R.id.action_h2),
        H3(R.id.action_h3),
        BOLD(R.id.action_bold),
        ITALIC(R.id.action_Italic),
        INDENT(R.id.action_indent),
        OUTDENT(R.id.action_outdent),
        BULLETED(R.id.action_bulleted),
        UNORDERED_NUMBERED(R.id.action_unordered_numbered),
        HR(R.id.action_hr),
        IMAGE(R.id.action_insert_image),
        LINK(R.id.action_insert_link),
        MAP(R.id.action_map),
        ERASE(R.id.action_erase),
        ;

        int id;

        TOOLBAR_OPTION(int id) {
            this.id = id;
        }
    }


    public EditorToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.toolbar_editor_options, this);

    }

    public void setOptions(Editor editor, TOOLBAR_OPTION... options){

        setOptions(editor, Arrays.asList(options));
    }

    public void setOptions(Editor editor, List<TOOLBAR_OPTION> options){

        for (TOOLBAR_OPTION toolbarOption : TOOLBAR_OPTION.values()) {

            View option = findViewById(toolbarOption.id);

            if(option == null) continue;

            if(options.contains(toolbarOption)){
                option.setOnClickListener(editor.getOptionListener(toolbarOption));
            } else {
                option.setVisibility(View.GONE);
            }

        }
    }

}
