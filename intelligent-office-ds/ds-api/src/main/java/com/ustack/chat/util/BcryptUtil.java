package com.ustack.chat.util;

import cn.hutool.crypto.digest.BCrypt;

public class BcryptUtil {
    //加密
    public static String encode(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt());
    }

    //比较密码
    public static boolean match(String password, String encodePassword){
        return BCrypt.checkpw(password,encodePassword);
    }
}
