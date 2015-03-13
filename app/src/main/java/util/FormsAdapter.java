package util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ImageView;

import com.sellcom.tracker_interno.R;

import java.util.List;
import java.util.Map;

/**
 * Created by hugo.figueroa on 12/03/15.
 */
public class FormsAdapter extends BaseAdapter {

    private Activity                    activity;
    private LayoutInflater              layoutInflater;
    private List<Map<String,String>>    listForms;

    public FormsAdapter(Activity activity,List<Map<String,String>> listForms){
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.activity = activity;
        this.listForms = listForms;
    }

    @Override
    public int getCount() {
        return listForms.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View view1 = view;

        if(view == null){
            view1 = layoutInflater.inflate(R.layout.item_form,null);
        }

        TextView txt_form_name      = (TextView)view1.findViewById(R.id.txt_form_name);
        ImageView img_obligatorio   = (ImageView)view1.findViewById(R.id.img_obligatorio);

        txt_form_name.setText(listForms.get(position).get("dtf_name"));
        if(Integer.parseInt(listForms.get(position).get("dtf_obligatory"))==1){
            img_obligatorio.setImageResource(R.drawable.ic_asterisco);
        }




        return view1;
    }
}
