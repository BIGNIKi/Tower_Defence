package controllers;

import components.Component;
import onlineStuff.OurWebRequest;
import onlineStuff.WWWForm;

import java.nio.charset.StandardCharsets;
import java.util.Random;


public class OnlineObserver extends Component
{
    private transient OurWebRequest www = null;

    private transient String _sessionId = null;
    private transient int _numPlayer = -1;

    public String get_sessionId()
    {
        return _sessionId;
    }

    public int get_numPlayer()
    {
        return _numPlayer;
    }

    @Override
    public void OnStartScene()
    {
        WWWForm form = new WWWForm();
        //form.AddField("name", "John Doe");

        www = OurWebRequest.Post("http://abobnik228.ru/main/findSession.php", form);
        www.SendWebRequest();
    }

    @Override
    public void update(float dt)
    {
        if(_sessionId == null && www != null && www.CheckError() == OurWebRequest.Status.Success)
        {
            if(www.GetResponseBody().equals("Nope"))
            {
                byte[] array = new byte[7]; // length is bounded by 7
                new Random().nextBytes(array);
                _sessionId = new String(array, StandardCharsets.UTF_8);
                _numPlayer = 1;
                WWWForm form = new WWWForm();
                form.AddField("id", _sessionId);
                www = OurWebRequest.Post("http://abobnik228.ru/main/createSession.php", form);
                www.SendWebRequest();
            }
            else
            {
                _numPlayer = 2;
                _sessionId = www.GetResponseBody();
                System.out.println(_sessionId);
            }

            www = null;
        }

        
    }
}
