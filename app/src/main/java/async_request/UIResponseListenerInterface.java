package async_request;

import java.util.Map;

public interface UIResponseListenerInterface {
    public void prepareRequest(METHOD method, Map<String, String> params);
    public void decodeResponse(String stringResponse);
}
