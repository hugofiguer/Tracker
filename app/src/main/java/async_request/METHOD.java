package async_request;

/**
 * Created by raymundo.piedra on 22/01/15.
 */

/*
    Dando de alta un nuevo elemento....
    -
 */
public enum METHOD {
    LOGIN ("login"),
    GET_WORKPLAN ("get_workplan"),
    GET_PDV_INFO ("get_pdv_info"),
    GET_CASHING ("get_cashing"),
    GET_PRODUCTS ("get_products")
    ;

    private final String name;

    private METHOD(String s) {
        name = s;
    }

    public boolean equalsName(String otherName){
        return (otherName == null)? false:name.equals(otherName);
    }

    public String toString(){
        return name;
    }

}


