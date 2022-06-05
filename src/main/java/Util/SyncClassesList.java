package Util;

import SyncStuff.SyncClasses;

import java.util.ArrayList;

public class SyncClassesList extends ArrayList<SyncClasses>
{
    private int size = 0;

    public SyncClasses get(int index) {
        return (SyncClasses) super.get(index);
    }

    public boolean add(SyncClasses e) {
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
                this.add(new SyncClasses());
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
        SyncClassesList sL = (SyncClassesList) super.clone();
        sL.size = size;
        return sL;
    }
}
