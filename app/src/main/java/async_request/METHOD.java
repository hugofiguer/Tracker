package async_request;



/*
    Dando de alta un nuevo elemento....
    -
 */
public enum METHOD {
    LOGIN ("login"),
    GET_WORKPLAN ("get_pdv"),
    GET_PDV_INFO ("get_pdv_info"),
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


