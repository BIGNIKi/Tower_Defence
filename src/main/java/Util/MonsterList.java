package Util;

import SyncStuff.MonsterClass;

import java.util.ArrayList;

public class MonsterList extends ArrayList<MonsterClass>
{
    private int size = 0;

    public MonsterClass get(int index) {
        return (MonsterClass) super.get(index);
    }

    public boolean add(MonsterClass e) {
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
                this.add(new MonsterClass());
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
        MonsterList sL = (MonsterList) super.clone();
        return sL;
    }
}
