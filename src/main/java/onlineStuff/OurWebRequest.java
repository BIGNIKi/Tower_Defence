package onlineStuff;


import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        //String requestBody = formData.GetRequestString();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(ofFormData(formData.GetPostData()))
                .setHeader("User-Agent", "Java 11 HttpClient Bot") // add request header
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return new OurWebRequest(client, request);
    }

    private static HttpRequest.BodyPublisher ofFormData(Map<String, Object> data) {

        var builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
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
        _response.join();
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
