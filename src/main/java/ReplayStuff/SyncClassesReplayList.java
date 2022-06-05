package ReplayStuff;

import SyncStuff.SyncClasses;

import java.util.ArrayList;

public class SyncClassesReplayList extends ArrayList<SyncReplayClasses>
{
    private int size = 0;

    public SyncReplayClasses get(int index) {
        return (SyncReplayClasses) super.get(index);
    }

    public boolean add(SyncReplayClasses e) {
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
                this.add(new SyncReplayClasses());
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
        SyncClassesReplayList sL = (SyncClassesReplayList) super.clone();
        sL.size = size;
        return sL;
    }
}
