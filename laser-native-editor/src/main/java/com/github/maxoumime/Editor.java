/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.maxoumime;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.github.maxoumime.model.EditorContent;
import com.github.maxoumime.model.EditorTextStyle;
import com.github.maxoumime.model.RenderType;

public class Editor extends EditorCore {

    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (isInEditMode()) return;

        super.setEditorListener(null);
        //  initialize(context,parentView,renderType,_PlaceHolderText);

        setDividerLayout(R.layout.tmpl_divider_layout);
        setListItemLayout(R.layout.tmpl_list_item);
    }

    public void setEditorListener(EditorListener _listener) {
        super.setEditorListener(_listener);
    }

    public EditorContent getContent() {
        return super.getContent();
    }

    public String getContentAsHTML() {
        return getHtmlExtensions().getContentAsHTML();
    }

    public String getContentAsHTML(EditorContent content) {
        return getHtmlExtensions().getContentAsHTML(content);
    }

    public void Render(EditorContent _state) {
        super.RenderEditor(_state);
    }

    public void Render(String HtmlString) {
        RenderEditorFromHtml(HtmlString);
    }

    public void Render() {
        if (getRenderType() == RenderType.Editor) {
            getInputExtensions().InsertEditText(0, this.PlaceHolder, null);
        }
    }

    public void clearAllContents() {
        super.clearAllContents();
        if (getRenderType() == RenderType.Editor) {
            getInputExtensions().InsertEditText(0, this.PlaceHolder, null);
        }
    }

    //region Miscellanious getters and setters
        /*input extensions
         */
    public int getH1TextSize() {
        return getInputExtensions().getH1TextSize();
    }

    public void setH1TextSize(int size) {
        getInputExtensions().setH1TextSize(size);
    }

    public int getH2TextSize() {
        return getInputExtensions().getH2TextSize();
    }

    public void setH2TextSize(int size) {
        getInputExtensions().setH2TextSize(size);
    }

    public int getH3TextSize() {
        return getInputExtensions().getH3TextSize();
    }

    public void setH3TextSize(int size) {
        getInputExtensions().setH3TextSize(size);
    }

    public void setFontFace(int StringResource) {
        getInputExtensions().setFontFace(StringResource);
    }

    public String getFontFace() {
        return getInputExtensions().getFontFace();
    }

    public void setLineSpacing(float value) {
        getInputExtensions().setLineSpacing(value);
    }

    public float getLineSpacing() {
        return getInputExtensions().getLineSpacing();
    }

    public void setOptions(EditorToolbar.TOOLBAR_OPTION... options) {
        if (getToolbar() != null) {

            getToolbar().setOptions(this, options);
        }
    }

    public OnClickListener getOptionListener(EditorToolbar.TOOLBAR_OPTION option) {

        switch (option) {

            case H1:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.H1);
                    }
                };
            case H2:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.H2);
                    }
                };
            case H3:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.H3);
                    }
                };
            case BOLD:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.BOLD);
                    }
                };
            case ITALIC:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.ITALIC);
                    }
                };
            case INDENT:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.INDENT);
                    }
                };
            case OUTDENT:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.UpdateTextStyle(EditorTextStyle.OUTDENT);
                    }
                };
            case BULLETED:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.InsertList(false);
                    }
                };
            case UNORDERED_NUMBERED:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.InsertList(true);
                    }
                };
            case HR:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.InsertDivider();
                    }
                };
            case LINK:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.InsertLink();
                    }
                };
            case ERASE:
                return new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editor.this.clearAllContents();
                    }
                };
            default:
                return null;
        }
    }

    public void UpdateTextStyle(EditorTextStyle style) {
        getInputExtensions().UpdateTextStyle(style, null);
    }

    public void InsertLink() {
        getInputExtensions().InsertLink();
    }


        /*divider extensions
         */

    public void setDividerLayout(int layout) {
        this.getDividerExtensions().setDividerLayout(layout);
    }

    public void InsertDivider() {
        getDividerExtensions().InsertDivider();
    }

    /*List Item extensions
     */
    public void setListItemLayout(int layout) {
        this.getListItemExtensions().setListItemTemplate(layout);
    }

    public void InsertList(boolean isOrdered) {
        this.getListItemExtensions().Insertlist(isOrdered);
    }

    //  public void ExpressSetup(){
    //    super.ExpressSetup(this);
    // }
    //endregion


}