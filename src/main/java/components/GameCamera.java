package components;

import job.Camera;

public class GameCamera extends Component
{
    private transient Camera gameCamera;

    public GameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
    }
}
