package util;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by hugo.figueroa on 25/02/15.
 */
public class ListViewItem {

    private String      type    = "";
    private String      question = "";
    private String      id_question = "";
    private String      aop_option = "";

    public ListViewItem(String type,String question,String id_question,String aop_option) {
        this.type   = type;
        this.question = question;
        this.id_question = id_question;
        this.aop_option = aop_option;
    }

    public String getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }


    public String getId_question() {
        return id_question;
    }

    public String getAop_option() {
        return aop_option;
    }

    public void setAop_option(String aop_option) {
        this.aop_option = aop_option;
    }
}
