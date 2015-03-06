package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.Spinner;

import com.sellcom.tracker.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hugo.figueroa on 25/02/15.
 */
public class FormAdapter extends ArrayAdapter<ListViewItem>{

    final static public String TAG = "TAG_FRAGMENT_ADAPTER";

    private Map<String,String> data;

    private ListViewItem[]  objects;
    private int element     = 3;

    public static final int TYPE_BINARY = 0;
    public static final int TYPE_TEXT   = 1;
    public static final int TYPE_RADIO  = 2;

    public FormAdapter(Context context, int resource, ListViewItem[]objects) {
        super(context, resource, objects);
        this.objects = objects;
        data = new HashMap<String, String>();

        for(int i =0; i<objects.length-1; i++){
            data.put(i+"","");
        }

    }

    @Override
    public int getViewTypeCount(){
        return element;
    }

    @Override
    public int getItemViewType(int position) {
        return objects[position].getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderForBinary viewHolderForBinary = null;
        ViewHolderForText viewHolderForText = null;
        ViewHolderForRadio viewHolderForRadio = null;
        int listViewItemType = getItemViewType(position);


        if (convertView == null) {

            if (listViewItemType == TYPE_BINARY) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_form_question_binary, null);

                TextView txt_question_binary = (TextView) convertView.findViewById(R.id.txt_question_binary);
                RadioGroup rgp_binary = (RadioGroup) convertView.findViewById(R.id.rgp_binary);
                RadioButton rbtn_binary1 = (RadioButton) convertView.findViewById(R.id.rbtn_binary1);
                RadioButton rbtn_binary2 = (RadioButton) convertView.findViewById(R.id.rbtn_binary2);

                viewHolderForBinary = new ViewHolderForBinary(txt_question_binary,rgp_binary,rbtn_binary1,rbtn_binary2);
                convertView.setTag(viewHolderForBinary);

            } else if (listViewItemType == TYPE_TEXT) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_form_question_text, null);

                TextView txt_questionnarie_text = (TextView) convertView.findViewById(R.id.txt_questionnarie_text);
                EditText edt_answer_text = (EditText) convertView.findViewById(R.id.edt_answer_text);

                viewHolderForText = new ViewHolderForText(txt_questionnarie_text,edt_answer_text);
                convertView.setTag(viewHolderForText);
            } else if (listViewItemType == TYPE_RADIO) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_form_question_radio, null);

                TextView txt_question_radio = (TextView) convertView.findViewById(R.id.txt_question_radio);
                Spinner spn_answer_radio = (Spinner) convertView.findViewById(R.id.spn_answer_radio);

                viewHolderForRadio = new ViewHolderForRadio(txt_question_radio,spn_answer_radio);
                convertView.setTag(viewHolderForRadio);
            }

        }else {

            if (listViewItemType == TYPE_BINARY) {
                viewHolderForBinary = (ViewHolderForBinary) convertView.getTag();
            } else if (listViewItemType == TYPE_TEXT) {
                viewHolderForText = (ViewHolderForText) convertView.getTag();
            } else if (listViewItemType == TYPE_RADIO) {
                viewHolderForRadio = (ViewHolderForRadio) convertView.getTag();
            }
        }


        if (listViewItemType == TYPE_BINARY) {
            viewHolderForBinary.getRgp_binary().setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    if(viewHolderForBinary.getRbtn_binary1().isChecked()){

                    }

                }
            });
            data.put(position+"","");
        } else if (listViewItemType == TYPE_TEXT) {
            data.put(position+"","");
        } else if (listViewItemType == TYPE_RADIO) {
            data.put(position+"","");
        }


        return convertView;
    }

    //CLASE VIEWHOLDER
    //------------------------------------------------------------------------------------------------------------------------
    public class ViewHolderForBinary{
        //BINARY
        RadioGroup rgp_binary;
        RadioButton rbtn_binary1,rbtn_binary2;
        TextView txt_question_binary;

        //BINARY
        public ViewHolderForBinary(TextView txt_question_binary, RadioGroup rgp_binary, RadioButton rbtn_binary1, RadioButton rbtn_binary2) {
            this.txt_question_binary = txt_question_binary;
            this.rgp_binary = rgp_binary;
            this.rbtn_binary1 = rbtn_binary1;
            this.rbtn_binary2 = rbtn_binary2;
        }

        public TextView getText() {
            return txt_question_binary;
        }

        public void setText(TextView txt_question_binary) {
            this.txt_question_binary = txt_question_binary;
        }

        public RadioGroup getRgp_binary() {
            return rgp_binary;
        }

        public void setRgp_binary(RadioGroup rgp_binary) {
            this.rgp_binary = rgp_binary;
        }

        public RadioButton getRbtn_binary1() {
            return rbtn_binary1;
        }

        public void setRbtn_binary1(RadioButton rbtn_binary1) {
            this.rbtn_binary1 = rbtn_binary1;
        }

        public RadioButton getRbtn_binary2() {
            return rbtn_binary2;
        }

        public void setRbtn_binary2(RadioButton rbtn_binary2) {
            this.rbtn_binary2 = rbtn_binary2;
        }

        public TextView getTxt_question_binary() {
            return txt_question_binary;
        }

        public void setTxt_question_binary(TextView txt_question_binary) {
            this.txt_question_binary = txt_question_binary;
        }

    }

    public class ViewHolderForText {
        //TEXT
        TextView txt_questionnarie_text;
        EditText edt_answer_text;

        //TEXT
        public ViewHolderForText(TextView txt_question_text, EditText edt_answer_text) {
            this.txt_questionnarie_text = txt_question_text;
            this.edt_answer_text = edt_answer_text;
        }

        public TextView getTxt_question_text() {
            return txt_questionnarie_text;
        }

        public void setTxt_question_text(TextView txt_question_text) {
            this.txt_questionnarie_text = txt_question_text;
        }

        public EditText getEdt_answer_text() {
            return edt_answer_text;
        }

        public void setEdt_answer_text(EditText edt_answer_text) {
            this.edt_answer_text = edt_answer_text;
        }

    }
    public class ViewHolderForRadio {

        //RADIO
        TextView txt_question_radio;
        Spinner spn_answer_radio;

        //RADIO
        public ViewHolderForRadio(TextView txt_question_radio, Spinner spn_answer_radio) {
            this.txt_question_radio = txt_question_radio;
            this.spn_answer_radio = spn_answer_radio;
        }

        public TextView getTxt_question_radio() {
            return txt_question_radio;
        }

        public void setTxt_question_radio(TextView txt_question_radio) {
            this.txt_question_radio = txt_question_radio;
        }

        public Spinner getSpn_answer_radio() {
            return spn_answer_radio;
        }

        public void setSpn_answer_radio(Spinner spn_answer_radio) {
            this.spn_answer_radio = spn_answer_radio;
        }
    }

    //------------------------------------------------------------------------------------------------------------------------

}


