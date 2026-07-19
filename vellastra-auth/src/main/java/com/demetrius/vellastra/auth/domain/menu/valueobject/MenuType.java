package com.demetrius.vellastra.auth.domain.menu.valueobject;

/**
 * <p>Title: MenuType</p>
 * <p>Description: 菜单类型枚举（目录/菜单/按钮）</p>
 * <p>项目名称: Vellastra</p>
 *
 * @author wanqiu
 * @since 1.1
 * @createTime 2026-07-18
 * @updateTime 2026-07-19
 *
 * Copyright © 2026 wanqiu All rights reserved
 
 */
public enum MenuType {

    /** 目录 */
    DIRECTORY(1),
    /** 菜单 */
    MENU(2),
    /** 按钮 */
    BUTTON(3);

    private final int code;

    MenuType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MenuType fromCode(int code) {
        for (MenuType t : values()) {
            if (t.code == code) return t;
        }
        return MENU;
    }
}