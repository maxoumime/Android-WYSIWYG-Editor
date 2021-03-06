package com.github.maxoumime;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.maxoumime.component.DividerExtensions;
import com.github.maxoumime.component.HTMLExtensions;
import com.github.maxoumime.component.InputExtensions;
import com.github.maxoumime.component.ListItemExtensions;
import com.github.maxoumime.model.EditorContent;
import com.github.maxoumime.model.EditorControl;
import com.github.maxoumime.model.EditorTextStyle;
import com.github.maxoumime.model.EditorType;
import com.github.maxoumime.model.Op;
import com.github.maxoumime.model.RenderType;
import com.github.maxoumime.model.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mkallingal on 4/30/2016.
 */
public class EditorCore extends LinearLayout {
    /*
    * EditText initializors
    */
    public String PlaceHolder = null;
    /*
    * Divider initializors
    */
    private final String SHAREDPREFERENCE = "QA";
    private Context __context;
    private Activity __activity;
    private LinearLayout __parentView;
    private RenderType __renderType;
    private Resources __resources;
    private View __activeView;
    private Utilities __utilities;
    private EditorListener __listener;
    private String __imageUploaderUri;
    private InputExtensions __inputExtensions;
    private ListItemExtensions __listItemExtensions;
    private DividerExtensions __dividerExtensions;
    private HTMLExtensions __htmlExtensions;

    private int toolbarId;
    private EditorToolbar toolbar;

    public EditorCore(Context context) {
        super(context);
    }

    public EditorCore(Context _context, AttributeSet attrs) {
        super(_context, attrs);

        if (!isInEditMode()) {

            this.__context = _context;
            this.__activity = (Activity) _context;
            this.setOrientation(VERTICAL);
            initialize(_context, attrs);
        }
    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        __utilities = new Utilities();
        this.__resources = context.getResources();
        __inputExtensions = new InputExtensions(this);
        __listItemExtensions = new ListItemExtensions(this);
        __dividerExtensions = new DividerExtensions(this);
        __htmlExtensions = new HTMLExtensions(this);
        this.__parentView = this;
    }

    //region Getters_and_Setters
    public Activity getActivity() {
        return this.__activity;
    }

    public LinearLayout getParentView() {
        return this.__parentView;
    }

    public int getParentChildCount() {
        return this.__parentView.getChildCount();
    }

    public RenderType getRenderType() {
        return this.__renderType;
    }

    public Resources getResources() {
        return this.__resources;
    }

    public View getActiveView() {
        return this.__activeView;
    }

    public void setActiveView(View view) {
        this.__activeView = view;
    }

    public Utilities getUtilitiles() {
        return this.__utilities;
    }

    public EditorListener getEditorListener() {
        return this.__listener;
    }

    public void setEditorListener(EditorListener _listener) {
        this.__listener = _listener;
    }

    public InputExtensions getInputExtensions() {
        return this.__inputExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return this.__htmlExtensions;
    }

    public ListItemExtensions getListItemExtensions() {
        return this.__listItemExtensions;
    }

    public DividerExtensions getDividerExtensions() {
        return this.__dividerExtensions;
    }

    public String getImageUploaderUri() {
        return this.__imageUploaderUri;
    }

    public EditorToolbar getToolbar() {

        if (toolbar != null) return toolbar;

        if (toolbarId < 0) return null;

        toolbar = (EditorToolbar) getRootView().findViewById(toolbarId);

        return toolbar;
    }

    public void setToolbar(EditorToolbar toolbar) {
        this.toolbar = toolbar;
    }

    public void setImageUploaderUri(String imageUploaderUri) {
        this.__imageUploaderUri = imageUploaderUri;
    }

    //endregion
    public interface EditorListener {
        void onTextChanged(EditText editText, Editable text);
    }

    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);
            this.PlaceHolder = a.getString(R.styleable.editor_placeholder);
            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.__renderType = RenderType.Editor;
            } else {
                this.__renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
            }
            toolbarId = a.getResourceId(R.styleable.editor_toolbar, -1);

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }
    }

    public int determineIndex(EditorType type) {
        int size = this.__parentView.getChildCount();
        if (this.__renderType == RenderType.Renderer)
            return size;
        View _view = this.__activeView;
        if (_view == null)
            return size;
        int currentIndex = this.__parentView.indexOfChild(_view);
        EditorType tag = getControlType(_view);
        if (tag == EditorType.INPUT) {
            int length = ((EditText) this.__activeView).getText().length();
            if (length > 0) {
                return type == EditorType.UL_LI || type == EditorType.OL_LI ? currentIndex : currentIndex;
            } else {
                return currentIndex;
            }
        } else if (tag == EditorType.UL_LI || tag == EditorType.OL_LI) {
            EditText _text = (EditText) _view.findViewById(R.id.txtText);
            if (_text.getText().length() > 0) {

            }
            return size;
        } else {
            return size;
        }
    }

    public boolean containsStyle(List<EditorTextStyle> _Styles, EditorTextStyle style) {
        for (EditorTextStyle item : _Styles) {
            if (item == style) {
                return true;
            }
            continue;
        }
        return false;
    }

    public EditorControl updateTagStyle(EditorControl controlTag, EditorTextStyle style, Op _op) {
        List<EditorTextStyle> styles = controlTag._ControlStyles;
        if (_op == Op.Delete) {
            int index = styles.indexOf(style);
            if (index != -1) {
                styles.remove(index);
                controlTag._ControlStyles = styles;
            }
        } else {
            int index = styles.indexOf(style);
            if (index == -1) {
                styles.add(style);
            }
        }
        return controlTag;
    }

    public EditorType getControlType(View _view) {
        EditorControl _control = (EditorControl) _view.getTag();
        return _control.Type;
    }

    public EditorControl getControlTag(View view) {
        EditorControl control = (EditorControl) view.getTag();
        return control;
    }

    public EditorControl createTag(EditorType type) {
        EditorControl control = new EditorControl();
        control.Type = type;
        control._ControlStyles = new ArrayList<>();
        switch (type) {
            case hr:
            case INPUT:
            case ul:
            case UL_LI:
        }
        return control;
    }

    public void deleteFocusedPrevious(EditText view) {
        String parentTagName = view.getParent().getClass().getName();
        if (parentTagName.toLowerCase().equals("android.widget.tablerow")) {
            TableRow _row = (TableRow) view.getParent();
            TableLayout _table = (TableLayout) _row.getParent();
            int index = _table.indexOfChild(_row);
            _table.removeView(_row);
            if (index > 0) {
                TableRow focusrow = (TableRow) _table.getChildAt(index - 1);
                EditText text = (EditText) focusrow.findViewById(R.id.txtText);
                if (text.requestFocus()) {
                    text.setSelection(text.getText().length());
                }
            } else {
                removeParent(_table);
            }
        } else {
            removeParent(view);
        }
    }

    public void removeParent(View view) {
        int indexOfDeleteItem = __parentView.indexOfChild(view);
        View nextItem = null;
        if (indexOfDeleteItem == 0)
            return;
        //remove hr if its on top of the delete field
        __dividerExtensions.deleteHr(indexOfDeleteItem - 1);
        for (int i = 0; i < __parentView.getChildCount(); i++) {
            if (i < indexOfDeleteItem && getControlType(__parentView.getChildAt(i)) == EditorType.INPUT) {
                nextItem = __parentView.getChildAt(i);
            }
        }
        this.__parentView.removeView(view);
        if (nextItem != null) {
            EditText text = (EditText) nextItem;
            String x = text.getText().toString();
            //   _Text.setSelection(_Text.getText().length());
            if (text.requestFocus()) {
                text.setSelection(text.getText().length());
            }
            this.__activeView = nextItem;
        }
    }

    public String getValue(String Key, String defaultVal) {
        SharedPreferences _Preferences = __context.getSharedPreferences(SHAREDPREFERENCE, 0);
        return _Preferences.getString(Key, defaultVal);

    }

    public void putValue(String Key, String Value) {
        SharedPreferences _Preferences = __context.getSharedPreferences(SHAREDPREFERENCE, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public EditorContent getContent() {

        if (this.__renderType == RenderType.Renderer) {
            Log.e("EditorCore", "This option only available in editor mode");
            return null;
        }

        int childCount = this.__parentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<State> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            State state = new State();
            View view = __parentView.getChildAt(i);
            EditorType type = getControlType(view);
            state.type = type;
            state.content = new ArrayList<>();
            switch (type) {
                case INPUT:
                    EditText _text = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    state._ControlStyles = tag._ControlStyles;
                    state.content.add(Html.toHtml(_text.getText()));
                    list.add(state);
                    break;
                case hr:
                    list.add(state);
                    break;
                case ul:
                case ol:
                    TableLayout table = (TableLayout) view;
                    int _rowCount = table.getChildCount();
                    for (int j = 0; j < _rowCount; j++) {
                        View row = table.getChildAt(j);
                        EditText li = (EditText) row.findViewById(R.id.txtText);
                        state.content.add(Html.toHtml(li.getText()));
                    }
                    list.add(state);
                    break;
            }
        }
        editorState.stateList = list;
        return editorState;
    }

    public void RenderEditor(EditorContent _state) {
        this.__parentView.removeAllViews();
        for (State item : _state.stateList) {
            switch (item.type) {
                case INPUT:
                    String text = item.content.get(0);
                    TextView view = __inputExtensions.InsertEditText(0, "", text);
                    if (item._ControlStyles != null) {
                        for (EditorTextStyle style : item._ControlStyles) {
                            __inputExtensions.UpdateTextStyle(style, view);
                        }
                    }
                    break;
                case hr:
                    __dividerExtensions.InsertDivider();
                    break;
                case ul:
                case ol:
                    TableLayout _layout = null;
                    for (int i = 0; i < item.content.size(); i++) {
                        if (i == 0) {
                            _layout = __listItemExtensions.insertList(_state.stateList.indexOf(item), item.type == EditorType.ol, item.content.get(i));
                        } else {
                            __listItemExtensions.AddListItem(_layout, item.type == EditorType.ol, item.content.get(i));
                        }
                    }
                    break;
            }
        }
    }


    public boolean isLastRow(View view) {
        int index = this.__parentView.indexOfChild(view);
        int length = this.__parentView.getChildCount();
        return length - 1 == index;
    }

    public int GetChildCount() {
        int Count = __parentView.getChildCount();
        return Count;
    }

    public int GetChildCountforView(View view) {
        return ((ViewGroup) view).getChildCount();
    }

    public void RenderEditorFromHtml(String content) {
        __htmlExtensions.parseHtml(content);
    }

    public void clearAllContents() {
        this.__parentView.removeAllViews();

    }

    public class Utilities {
        public float pxtoSp(float px) {
            float scaledDensity = __context.getResources().getDisplayMetrics().scaledDensity;
            float sp = px / scaledDensity;
            return sp;
        }

        public int[] getScreenDimension() {
            Display display = ((Activity) __context).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen = {width, height};
            return dimen;
        }
    }
}
