package util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sellcom.tracker_interno.R;

/**
 * Created by hugo.figueroa on 14/01/15.
 */
public class StepVisitAdapter extends BaseAdapter{



    private LayoutInflater layoutInflater;
    private static Activity activity;
    private int steps = 3; //Numero de pasos de la visita
    private int[] images = {R.drawable.ic_evidence,
                            R.drawable.ic_activities,
                            R.drawable.ic_form}; //Arreglo de enteros para guardar las imagenes de los pasos de la visita

    private int[] name_steps = {R.string.sv_evidence,
                                R.string.sv_activities,
                                R.string.sv_form}; //Arreglo de enteros para guardar las el nombre de los pasos de la visita

    public StepVisitAdapter(Activity a){

        layoutInflater = (LayoutInflater)a.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        activity = a;

    }

    @Override
    public int getCount() {
        return steps;
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

        LinearLayout linearLayout;
        ImageView image_step_visit;
        TextView name_step_visit;
        View vista = view;
        if(view == null){
            vista = layoutInflater.inflate(R.layout.item_step_visit,null);
        }

        Resources res = activity.getResources();

        //Para asignarle una imagen a cada elemento del gridview
        image_step_visit = (ImageView)vista.findViewById(R.id.image_step_visit);
        image_step_visit.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image_step_visit.setImageResource(images[position]);

        //Para asignarle un nombre a cada elemento del gridview
        name_step_visit = (TextView)vista.findViewById(R.id.name_step_visit);
        name_step_visit.setText(res.getString(name_steps[position]));

        return vista;
    }


}
