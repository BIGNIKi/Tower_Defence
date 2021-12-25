package utils;

import java.util.ArrayList;

// эта обертка нужна, чтобы Component понимал, элементы какого типа хранятся в листе
// нет способа узнать Generic класс стандартного List'а (по крайней мере я таких способов не знаю)
public class StringList extends ArrayList<String>
{
    private int size = 0;

    public String get(int index) {
        return (String) super.get(index);
    }

    public boolean add(String e) {
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
                this.add(new String());
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
        StringList sL = (StringList) super.clone();
        sL.size = size;
        return sL;
    }
}
