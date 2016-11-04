package com.github.maxoumime.component;

import android.widget.TableLayout;
import android.widget.TextView;

import com.github.maxoumime.EditorCore;
import com.github.maxoumime.model.EditorContent;
import com.github.maxoumime.model.EditorTextStyle;
import com.github.maxoumime.model.EditorType;
import com.github.maxoumime.model.HtmlTag;
import com.github.maxoumime.model.State;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * Created by mkallingal on 5/25/2016.
 */
public class HTMLExtensions {
    EditorCore editorCore;

    public HTMLExtensions(EditorCore editorCore){
        this.editorCore = editorCore;
    }
    public void parseHtml(String htmlString){
        Document doc= Jsoup.parse(htmlString);
        for (Element element:doc.body().children()){
            if(!matchesTag(element.tagName()))
                continue;
            buildNode(element);
        }
    }
    private void buildNode(Element element) {
        String text;
        TextView editText;
        HtmlTag tag= HtmlTag.valueOf(element.tagName().toLowerCase());
        int count= editorCore.getParentView().getChildCount();
        if("<br>".equals(element.html().replaceAll("\\s+", ""))||"<br/>".equals(element.html().replaceAll("\\s+", ""))){
            editorCore.getInputExtensions().InsertEditText(count, null, null);
            return;
        }
        else if("<hr>".equals(element.html().replaceAll("\\s+", ""))||"<hr/>".equals(element.html().replaceAll("\\s+", ""))){
            editorCore.getDividerExtensions().InsertDivider();
            return;
        }
        switch (tag){
            case h1:
            case h2:
            case h3:
                RenderHeader(tag,element);
                break;
            case p:
                text= element.html();
                editText= editorCore.getInputExtensions().InsertEditText(count, null, text);
                break;
            case ul:
            case ol:
                RenderList(tag==HtmlTag.ol, element);
                break;
        }
    }

    private void RenderList(boolean isOrdered, Element element) {
        if(element.children().size()>0){
            Element li=element.child(0);
            String text=getHtmlSpan(li);
            TableLayout layout= editorCore.getListItemExtensions().insertList(editorCore.getParentChildCount(),isOrdered,text);
            for (int i=1;i<element.children().size();i++){
                 text=getHtmlSpan(li);
                editorCore.getListItemExtensions().AddListItem(layout,isOrdered,text);
            }
        }
    }

    private void RenderHeader(HtmlTag tag, Element element){
       int count= editorCore.getParentView().getChildCount();
       String text=getHtmlSpan(element);
       TextView  editText= editorCore.getInputExtensions().InsertEditText(count, null, text);
       EditorTextStyle style= tag==HtmlTag.h1? EditorTextStyle.H1:tag==HtmlTag.h2? EditorTextStyle.H2: EditorTextStyle.H3;
       editorCore.getInputExtensions().UpdateTextStyle(style,editText);
    }

    private String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style",element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element){
        return element.getAllElements().size() > 0;
    }


    private static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }

    private String getTemplateHtml(EditorType child){
        String template=null;
        switch (child){
            case INPUT:
                template= "<{{$tag}} data-tag=\"input\" {{$style}}>{{$content}}</{{$tag}}>";
                break;
            case hr:
                template="<hr data-tag=\"hr\"/>";
                break;
            case ol:
                template="<ol data-tag=\"ol\">{{$content}}</ol>";
                break;
            case ul:
                template="<ul data-tag=\"ul\">{{$content}}</ul>";
                break;
            case OL_LI:
            case UL_LI:
                template="<li>{{$content}}</li>";
                break;
        }
        return template;
    }
    private String getInputHtml(State item){
        boolean isParagraph=true;
        String tmpl= getTemplateHtml(EditorType.INPUT);
      //  CharSequence content= android.text.Html.fromHtml(item.content.get(0)).toString();
      //  CharSequence trimmed= editorCore.getInputExtensions().noTrailingwhiteLines(content);
        String trimmed= Jsoup.parse(item.content.get(0)).body().select("p").html();
        if(item._ControlStyles.size()>0) {
            for (EditorTextStyle style : item._ControlStyles) {
                switch (style) {
                    case BOLD:
                        tmpl = tmpl.replace("{{$content}}", "<b>{{$content}}</b>");
                        break;
                    case BOLDITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<b><i>{{$content}}</i></b>");
                        break;
                    case ITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<i>{{$content}}</i>");
                        break;
                    case INDENT:
                        tmpl= tmpl.replace("{{$style}}","style=\"margin-left:25px\"");
                        break;
                    case OUTDENT:
                        tmpl= tmpl.replace("{{$style}}","style=\"margin-left:0px\"");
                        break;
                    case H1:
                        tmpl = tmpl.replace("{{$tag}}", "h1");
                        isParagraph = false;
                        break;
                    case H2:
                        tmpl = tmpl.replace("{{$tag}}", "h2");
                        isParagraph = false;
                        break;
                    case H3:
                        tmpl = tmpl.replace("{{$tag}}", "h3");
                        isParagraph = false;
                        break;
                    case NORMAL:
                        tmpl=tmpl.replace("{{$tag}}","p");
                        isParagraph=true;
                        break;
                }
            }
            if (isParagraph) {
                tmpl = tmpl.replace("{{$tag}}", "p");
            }
            tmpl=tmpl.replace("{{$content}}",trimmed);
            tmpl=tmpl.replace("{{$style}}","");
            return tmpl;
        }
            tmpl = tmpl.replace("{{$tag}}", "p");
        tmpl=tmpl.replace("{{$content}}",trimmed);
        tmpl=tmpl.replace(" {{$style}}","");
        return tmpl;
    }

    public String getContentAsHTML() {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        EditorContent content = editorCore.getContent();
       return getContentAsHTML(content);
    }

    public String getContentAsHTML(EditorContent content){
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        for (State item : content.stateList) {
            switch (item.type) {
                case INPUT:
                    html = getInputHtml(item);
                    htmlBlock.append(html);
                    break;
                case hr:
                    htmlBlock.append(getTemplateHtml(item.type));
                    break;
                case ul:
                case ol:
                    htmlBlock.append(getListAsHtml(item));
                    break;
            }
        }
        return htmlBlock.toString();
    }


    private String getListAsHtml(State item) {
        int count= item.content.size();
        String tmpl_parent = getTemplateHtml(item.type);
        StringBuilder childBlock=new StringBuilder();
        for (int i=0 ; i<count; i++){
            String tmpl_li= getTemplateHtml(item.type == EditorType.ul ? EditorType.UL_LI : EditorType.OL_LI);
            String trimmed= Jsoup.parse(item.content.get(i)).body().select("p").html();
            tmpl_li= tmpl_li.replace("{{$content}}",trimmed);
            childBlock.append(tmpl_li);
        }
       tmpl_parent= tmpl_parent.replace("{{$content}}",childBlock.toString());
        return tmpl_parent;
    }
}
