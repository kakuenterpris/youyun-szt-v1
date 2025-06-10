package com.ustack.global.common.utils;

import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: IdUtil
 * @Date: 2025-02-17 09:56
 */
public class IdUtil {

    public static String ulid(){
        Ulid monotonicUlid = UlidCreator.getMonotonicUlid();
        return monotonicUlid.toString();
    }
}
