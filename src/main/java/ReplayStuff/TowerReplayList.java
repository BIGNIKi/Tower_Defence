package ReplayStuff;

import SyncStuff.TowerClass;
import Util.TowerList;

import java.util.ArrayList;

public class TowerReplayList extends ArrayList<TowerReplayClass>
{
    private int size = 0;

    public TowerReplayClass get(int index) {
        return (TowerReplayClass) super.get(index);
    }

    public boolean add(TowerReplayClass e) {
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
                this.add(new TowerReplayClass());
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
        TowerReplayList sL = (TowerReplayList) super.clone();
        sL.size = size;
        return sL;
    }
}
