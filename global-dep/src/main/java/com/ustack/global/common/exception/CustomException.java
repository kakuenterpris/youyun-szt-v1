package com.ustack.global.common.exception;

import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: CustomException
 * @Date: 2023-12-19 16:02
 */
@Getter
public class CustomException extends RuntimeException{

    private Integer code;

    private String msg;

    public CustomException(ErrorCode errorCode, String ...msg) {
        super(errorCode.fmtMsg(msg));
        this.code = errorCode.getCode();
        this.msg =  errorCode.fmtMsg(msg);
    }

    public CustomException(Integer code, String msg){
        super(msg);
        this.code = code;
        this.msg = msg;
    }

}
