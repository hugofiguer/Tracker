package util;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;

import com.sellcom.tracker.R;

import java.util.List;
import java.util.Map;


public class VisitActivitiesAdapter extends BaseAdapter{

    private Context context;
    private List<Map<String,String>> datos;

    public VisitActivitiesAdapter(Context context, List<Map<String,String>> datos) {
        this.context = context;
        this.datos = datos;
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

        TextView txt_activity_name = (TextView)item.findViewById(R.id.txt_activity_name);
        txt_activity_name.setText(map.get("act_name"));

        TextView txt_activity_description = (TextView)item.findViewById(R.id.txt_activity_description);
        txt_activity_description.setText(map.get("act_description"));


        return item;
    }
}
