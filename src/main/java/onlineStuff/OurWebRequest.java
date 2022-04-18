package onlineStuff;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class OurWebRequest
{
    HttpClient client = null;
    HttpRequest request = null;

    private String _body = null;

    private CompletableFuture<HttpResponse<String>> _response = null;

    private int _statusCode = -1;


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
        _response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApplyAsync(
        resp -> {
            _statusCode = resp.statusCode();
            return resp;
        }).whenComplete(
                (resp,t) -> {
                    if(t != null){
                        //t.printStackTrace();
                    }else{
                        _body = resp.body();
                    }
                }
        );
    }

    public String GetResponseBody()
    {
        //return _response.join();
        return _body;
    }

    public Status CheckError()
    {
        if(_statusCode == -1)
            return Status.InProgress;

        if(_statusCode == 200)
            return Status.Success;

        return Status.Error;
    }

    public enum Status{
        Error,
        InProgress,
        Success
    }

}
