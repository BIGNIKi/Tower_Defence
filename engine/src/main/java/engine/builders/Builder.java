package engine.builders;

import engine.components.Component;

public interface Builder{

    void setName(String name);

    void addComponent(Component component);

}
