package com.lib.base.adapter;

/**
 * PackageName  com.lib.base.adapter
 * ProjectName  TempleteProject-java
 * Date         2023/4/21.
 *
 * @author dave
 */

public interface MultiItemEntity {
    default int itemType() {
        return 0;
    }
}
