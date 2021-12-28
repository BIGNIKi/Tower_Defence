package utils;

import org.joml.Vector2f;

// модуль с некоторой математикой
public class SmartCalc
{
    // удерживает значение переменной между 0 и 1
    public static float Clamp01(float value)
    {
        if(value < 0f)
        {
            return 0f;
        }

        if(value > 1f)
        {
            return 1f;
        }

        return value;
    }

    // линейная интерполяция вектора
    public static Vector2f Lerp(Vector2f a, Vector2f b, float t)
    {
        t = Clamp01(t);
        return new Vector2f(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }

    // линейная интерполяция значения типа float
    public static float Lerp(float a, float b, float t)
    {
        t = Clamp01(t);

        float aNorm = getNorm(a);
        float bNorm = getNorm(b);
        if(Math.abs(bNorm - aNorm) > 180)
        {
            if(aNorm > bNorm)
            {
                aNorm = -180-(180-aNorm);
            }
            else if(aNorm < bNorm)
            {
                bNorm = -180-(180-bNorm);
            }
        }
        return aNorm + (bNorm - aNorm) * t;
    }

    // поворот угла a к углу b с шагом t
    public static float rotateAtoBwithStepT(float a, float b, float t)
    {
        float aNorm = getNorm(a);
        float bNorm = getNorm(b);
        if(Math.abs(bNorm - aNorm) > 180)
        {
            if(aNorm > bNorm)
            {
                aNorm = -180-(180-aNorm);
            }
            else if(aNorm < bNorm)
            {
                bNorm = -180-(180-bNorm);
            }
        }

        if(aNorm > bNorm)
        {
            aNorm-=t;
            if(aNorm < bNorm)
            {
                aNorm = bNorm;
            }
        }
        else if(aNorm < bNorm)
        {
            aNorm+=t;
            if(aNorm > bNorm)
            {
                aNorm = bNorm;
            }
        }
        return aNorm;
    }

    // получает угол правильного размера (от -180 до 180)
    public static float getNorm(float angle)
    {
        if(angle > 180)
        {
            while(angle > 180)
            {
                angle -= 360;
            }
            return angle;
        }
        while(angle < -180)
        {
            angle += 360;
        }
        return angle;
    }

    // получает угол между двумя векторами
    public static float getAngleToVec(Vector2f from, Vector2f to)
    {
        var h = to.x - from.x;
        var w = to.y - from.y;

        var atan = Math.atan(h/w) / Math.PI * 180;
        if (w < 0 || h < 0)
            atan += 180;
        if (w > 0 && h < 0)
            atan -= 180;
        if (atan < 0)
            atan += 360;

        return -(float)(atan % 360);
    }
}
