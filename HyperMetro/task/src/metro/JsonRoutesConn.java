package metro;

import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

public class JsonRoutesConn {
    public Map<String, List<LinkedTreeMap<String, Object>>> routes;
    JsonRoutesConn(Map<String, List<LinkedTreeMap<String, Object>>> routes) {
        this.routes = routes;
    }
}
