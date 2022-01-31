package org.playuniverse.minecraft.wildcard.core.util.placeholder;

public interface TemplateStore {

    void setTemplate(Template value);

    Template getTemplate(String key);

    Template[] templateArray();

}
