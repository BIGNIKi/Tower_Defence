package onlineStuff;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OurWebRequest
{
    HttpClient client = null;
    HttpRequest request = null;

    private String _body = null;

    private OurWebRequest(HttpClient client, HttpRequest request)
    {
        this.client = client;
        this.request = request;
    }

    public static OurWebRequest Post(String uri, WWWForm formData) throws JsonProcessingException
    {
        String requestBody = formData.GetRequestString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://abobnik228.ru/main/sendHello.php"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        return new OurWebRequest(client, request);
    }

    public void SendWebRequest() throws IOException, InterruptedException
    {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        _body = response.body();
    }

    public String GetResponseBody()
    {
        return _body;
    }

}
