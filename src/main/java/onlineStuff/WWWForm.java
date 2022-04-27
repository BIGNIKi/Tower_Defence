package onlineStuff;

import java.util.HashMap;
import java.util.Map;

public class WWWForm
{
    private final Map<String, Object> data = new HashMap<>();

    public void AddField(String fieldName, Object value)
    {
        data.put(fieldName, value);
    }

    protected Map<String, Object> GetPostData()
    {
        return data;
    }
}
