package com.muy.common.configuration;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 17:17
 */
public abstract class ProjectConfiguration<T extends ProjectConfiguration, U> implements PersistentStateComponent<T> {


    public abstract List<U> getEntrances();

    @Override
    public @Nullable T getState() {
        return (T) this;
    }

    @Override
    public void loadState(@NotNull T t) {
        XmlSerializerUtil.copyBean(t, this);
    }

    public void removeData(U u) {
        getEntrances().remove(u);
    }

    public void addData(U u) {
        boolean find = false;
        for (U ut : getEntrances()) {
            if (ut.equals(u)) {
                find = true;
                return;
            }
        }
        getEntrances().add(u);
    }

    public void updateData(U preU, U updateU) {
        if (preU.equals(updateU)) {
            return;
        }

        int index = getDataIndex(preU);
        getEntrances().set(index, updateU);
    }

    private int getDataIndex(U u) {
        int index = 0;
        for (U ut : getEntrances()) {
            if (ut.equals(u)) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Unable to find the configuration to updated");
    }

}
