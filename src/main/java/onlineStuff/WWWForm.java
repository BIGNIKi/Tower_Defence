package onlineStuff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

public class WWWForm
{
    private final HashMap<String, String> Values = new HashMap<>();

    public void AddField(String fieldName, String value)
    {
        Values.put(fieldName, value);
    }

    public String GetRequestString() throws JsonProcessingException
    {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(Values);
    }
}
