#type vertex
#version 330 core

//a in start of a variable means "attribute"
//При помощи спецификатора layout можно задать расположение (location)
//для атрибута вершины прямо в вершинном шейдере.
//Ниже приводится пример такого описания, атрибуту location присваивается расположение по индексу 0
//http://steps3d.narod.ru/tutorials/layout-tutorial.html
layout (location = 0) in vec3 aPos; //position that we will send to the shader
layout (location = 1) in vec4 aColor; //color that we will send to the shader
layout (location = 2) in vec2 aTexCoords;
layout (location = 3) in float aTexId;
layout (location = 4) in float aEntityId;

uniform mat4 uProjection;
uniform mat4 uView;

//variable that we are going to pass to the fragment shader
out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;
out float fEntityId;

//all shaders must have main function
void main()
{
    fColor = aColor; //pass the color to the fragment shader
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    fEntityId = aEntityId;
    //Variables that start with gl_ are special global variables.
    //gl_Position, является выходным вектором вершинного шейдера, задающим вектор положения в пространстве отсечения.
    //Установка значения gl_Position является необходимым условием для вывода чего-либо на экран.
    gl_Position = uProjection * uView * vec4(aPos, 1.0); //значение координат пространства отсечения
    //про пространство отсечения хорошо написано здесь https://habr.com/ru/post/324968/
}

    #type fragment
    #version 330 core

//in means a value that it takes in
in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;
in float fEntityId;

uniform sampler2D uTextures[8];

out vec3 color; //it is a color we are outputting

void main()
{
    vec4 texColor = vec4(1,1,1,1);
    /*
        the way to make picture black and white
        float avg = (fColor.r + fColor.g + fColor.b) / 3;
        color = vec4(avg, avg, avg, 1);
    */
    if(fTexId > 0)
    {
        int id = int(fTexId);
        texColor = fColor * texture(uTextures[id], fTexCoords);
    }

    if(texColor.a < 0.5)
    {
        discard;
    }
    color = vec3(fEntityId, fEntityId, fEntityId);
}