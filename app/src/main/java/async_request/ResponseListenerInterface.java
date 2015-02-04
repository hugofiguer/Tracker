package async_request;

import org.json.JSONObject;

public interface ResponseListenerInterface {
    public void responseServiceToManager(JSONObject jsonResponse);
}
