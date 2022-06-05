package Util;

import SyncStuff.TowerClass;

import java.util.ArrayList;

// данные, которые используеются для синхронизации в процессе игры
public class TowerList extends ArrayList<TowerClass>
{
    private int size = 0;

    public TowerClass get(int index) {
        return (TowerClass) super.get(index);
    }

    public boolean add(TowerClass e) {
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
                this.add(new TowerClass());
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
        TowerList sL = (TowerList) super.clone();
        sL.size = size;
        return sL;
    }
}
