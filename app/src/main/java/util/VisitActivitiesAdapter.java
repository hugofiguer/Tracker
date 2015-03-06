package util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.sellcom.tracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class VisitActivitiesAdapter extends BaseAdapter{

    private Context context;
    private List<Map<String,String>> datos;
    private int status[];


    public VisitActivitiesAdapter(Context context, List<Map<String,String>> datos,int[] status) {
        this.context = context;
        this.datos = datos;
        this.status = status;

    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public Object getItem(int pos) {
        return datos.get(pos);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView;

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            item = inflater.inflate(R.layout.item_visit_activities, null);
        }

        Map<String,String> map = datos.get(position);


        TextView txt_activities_preview_num = (TextView)item.findViewById(R.id.txt_activities_preview_num);
        txt_activities_preview_num.setText(""+((position++) + 1));

        ImageView imgCheck = (ImageView)item.findViewById(R.id.imgCheck);
        imgCheck.setImageResource(R.drawable.ic_no_check);

        if(status[position-1] == 1) {
            txt_activities_preview_num.setBackgroundResource(R.color.green);
            imgCheck.setImageResource(R.drawable.ic_check);
        }

        TextView txt_activity_name = (TextView)item.findViewById(R.id.txt_activity_name);
        txt_activity_name.setText(map.get("act_name"));

        TextView txt_activity_description = (TextView)item.findViewById(R.id.txt_activity_description);
        txt_activity_description.setText(map.get("act_description"));


        return item;
    }
}
