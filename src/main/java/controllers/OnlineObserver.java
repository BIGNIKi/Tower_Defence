package controllers;

import components.Component;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;

import java.io.IOException;


public class OnlineObserver extends Component
{
    @Override
    public void OnStartScene()
    {
        WWWForm form = new WWWForm();
        //form.AddField("name", "John Doe");

        try
        {
            OurWebRequest www = OurWebRequest.Post("http://abobnik228.ru/main/sendHello.php", form);
            www.SendWebRequest();
            String response = www.GetResponseBody();
            System.out.println(response);
        } catch(IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
