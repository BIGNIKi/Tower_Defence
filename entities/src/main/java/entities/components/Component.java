package entities.components;

import utils.StringList;
import entities.job.GameObject;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Component
{
    private static int ID_COUNTER = 0;
    private int uid = -1;

    public transient GameObject gameObject = null;

    public void start()
    {

    }

    public void update(float dt)
    {

    }

    public void editorUpdate(float dt) {

    }

    // здесь написано, как инспектор должен отображать тот или иной тип данных
    public void imgui()
    {
        try
        {
            Field[] fields = this.getClass().getDeclaredFields();
            for(Field field : fields)
            {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if(isTransient) continue;

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if(isPrivate)
                {
                    field.setAccessible(true);
                }

                Class<?> type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if(type == int.class)
                {
                    int val = (int)value;
                    field.set(this, JImGui.dragInt(name, val));
                }
                else if(type == float.class)
                {
                    float val = (float)value;
                    field.set(this, JImGui.dragFloat(name, val));
                }
                else if(type == boolean.class)
                {
                    boolean val = (boolean)value;
                    if(ImGui.checkbox(name + ": ", val))
                    {
                        field.set(this, !val);
                    }
                }
                else if(type == Vector2f.class)
                {
                    Vector2f val = (Vector2f)value;
                    JImGui.drawVec2Control(name, val);
                }
                else if(type == Vector3f.class)
                {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if(ImGui.dragFloat3(name + ": ", imVec))
                    {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                }
                else if(type == Vector4f.class)
                {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if(ImGui.dragFloat4(name + ": ", imVec))
                    {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }
                else if (type.isEnum())
                {
                    String[] enumValues = getEnumValues((Class)type);
                    String enumType = ((Enum)value).name();
                    ImInt index = new ImInt(indexOf(enumType, enumValues));
                    if (ImGui.combo(field.getName(), index, enumValues, enumValues.length))
                    {
                        field.set(this, type.getEnumConstants()[index.get()]);
                    }
                }
                else if (type == String.class)
                {
                    field.set(this,
                            JImGui.inputText(field.getName() + ": ",
                                    (String)value));
                }
                // отображает лист стрингов
                else if(type == StringList.class)
                {
                    StringList sL = (StringList) value;
                    sL.setSize(JImGui.dragInt("size : ", sL.getSize()));
                    for(int i = 0; i<sL.size(); i++)
                    {
                        sL.set(i, JImGui.inputText(field.getName() + i + ": ", sL.get(i)));
                    }
                }

                if(isPrivate)
                {
                    field.setAccessible(false);
                }
            }
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public void generateId()
    {
        if(this.uid == -1)
        {
            this.uid = ID_COUNTER++;
        }
    }

    private <T extends Enum<T>> String[] getEnumValues(Class<T> enumType) {
        String[] enumValues = new String[enumType.getEnumConstants().length];
        int i = 0;
        for (T enumIntegerValue : enumType.getEnumConstants()) {
            enumValues[i] = enumIntegerValue.name();
            i++;
        }
        return enumValues;
    }

    private int indexOf(String str, String[] arr) {
        for (int i=0; i < arr.length; i++) {
            if (str.equals(arr[i])) {
                return i;
            }
        }

        return -1;
    }

    public int getUid()
    {
        return this.uid;
    }

    public static void init(int maxId)
    {
        ID_COUNTER = maxId;
    }

    public void destroy() {

    }
}
