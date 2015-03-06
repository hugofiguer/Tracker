package async_request;



/*
    Dando de alta un nuevo elemento....
    -
 */
public enum METHOD {
    LOGIN ("login"),
    GET_USER_INFO("get_user_info"),
    GET_WORKPLAN ("get_pdv"),
    GET_INFO_VISIT ("get_info_visit"),
    GET_ACTIVITIES ("get_activities"),
    SEND_RESCHEDULED_VISIT("reschedule_visit"),
    END_ACTIVITY ("end_activity"),
    GET_CAT_EV("get_cat_ev"),
    SEND_EVIDENCE("send_evidence"),
    GET_FORM("get_form"),
    LOGOUT ("logout")
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


