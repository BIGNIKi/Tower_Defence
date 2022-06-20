package ReplayStuff;

import java.util.ArrayList;

public class BulletReplayList extends ArrayList<BulletReplayClass>
{
    private int size = 0;

    public BulletReplayClass get(int index) {
        return (BulletReplayClass) super.get(index);
    }

    public boolean add(BulletReplayClass e) {
        size++;
        return super.add(e);
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int newSize)
    {
        if(newSize < 0)
        {
            newSize = 0;
        }
        if(newSize == size)
        {
            return;
        }
        else if(newSize > size)
        {
            for(int i = size; i<newSize; i++)
            {
                this.add(new BulletReplayClass());
            }
            size = newSize;
        }
        else if(newSize < size)
        {
            for(int i = size-1; i>=newSize; i--)
            {
                remove(i);
            }
            size = newSize;
        }

    }

    @Override
    public Object clone() {
        BulletReplayList sL = (BulletReplayList) super.clone();
        return sL;
    }
}
