package com.ustack.global.common.validation;

import jakarta.validation.GroupSequence;

/**
 * @Description: 校验组，用于区分同一个对象不同场景下校验字段不同的场景
 * @author：linxin
 * @ClassName: ValidGroup
 * @Date: 2023-12-27 10:44
 */
public class ValidGroup {
    public interface Insert {
    }

    public interface Update {
    }

    public interface Delete {
    }

    public interface Page {
    }

    public interface Update1 {
    }

    public interface Update2 {
    }

    public interface Update3 {
    }

    public interface Update4 {
    }

    public interface Update5 {
    }

    public interface Calc {
    }

    public interface Calc1 {
    }

    @GroupSequence({Insert.class, Update.class, Delete.class, Page.class, Update1.class, Update2.class, Update3.class, Update4.class, Update5.class, Calc.class, Calc1.class})
    public interface All {
    }

}
