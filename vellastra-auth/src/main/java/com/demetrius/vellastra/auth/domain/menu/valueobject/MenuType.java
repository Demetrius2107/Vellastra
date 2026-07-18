package com.demetrius.vellastra.auth.domain.menu.valueobject;

/**
 * <h3>菜单类型枚举</h3>
 *
 * @author wanqiu
 * @version 1.1
 * @since 2026-07-18
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