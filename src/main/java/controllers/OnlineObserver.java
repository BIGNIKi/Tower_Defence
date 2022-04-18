package controllers;

import components.Component;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;

import java.io.IOException;


public class OnlineObserver extends Component
{
    private transient OurWebRequest www = null;

    @Override
    public void OnStartScene()
    {
        WWWForm form = new WWWForm();
        //form.AddField("name", "John Doe");

        try
        {
            www = OurWebRequest.Post("http://abobnik228.ru/main/sendHello.php", form);
            www.SendWebRequest();
            //String response = www.GetResponseBody();
            System.out.println("blabla");
            //System.out.println(response);
        } catch(IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void update(float dt)
    {
        if(www.CheckError() == OurWebRequest.Status.Success)
        {
            System.out.println(www.GetResponseBody());
        }

    }
}
