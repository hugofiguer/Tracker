package util;

/**
 * Created by hugo.figueroa on 12/03/15.
 */
public class ListViewForms {

    private int     id_data_form    = 0;
    private String  dtf_name        = "";
    private int     dtf_obligatory  = 0;

    public ListViewForms(int id_data_form, String dtf_name, int dtf_obligatory){
        this.id_data_form   = id_data_form;
        this.dtf_name       = dtf_name;
        this.dtf_obligatory = dtf_obligatory;

    }

}
