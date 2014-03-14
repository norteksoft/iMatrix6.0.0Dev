package com.norteksoft.product.web.struts2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaData {
    /**
     * 系统编码
     * @return
     */
    String systemCode();
    /**
     * 是否受权限控制
     * @return
     */
    boolean isAuth() default true;
    /**
     * 当前资源是否是菜单资源
     * @return
     */
    boolean isMenu() default false;
    /**
     * 描述
     * @return
     */
    String describe();
}
