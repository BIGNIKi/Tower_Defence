package ReplayStuff;

import SyncStuff.MonsterClass;
import Util.MonsterList;

import java.util.ArrayList;

public class MonsterReplayList extends ArrayList<MonsterReplayClass>
{
    private int size = 0;

    public MonsterReplayClass get(int index) {
        return (MonsterReplayClass) super.get(index);
    }

    public boolean add(MonsterReplayClass e) {
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
                this.add(new MonsterReplayClass());
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
        MonsterReplayList sL = (MonsterReplayList) super.clone();
        return sL;
    }
}
