package com.test.enums;

public enum AppHttpCodeEnum {

    SUCCESS(200, "操作成功"),
    ERROR_STORE_ID(404, "没有这个门店"),
    ERROR_RULE_ID(404, "没有这个规则"),
    ERROR_USER_ID(404, "没有这个员工的信息"),
    LACK_PARAM(400, "参数不能为空"),
    NULL_PARAM(400, "缺少必要参数"),
    HAVE_USER(405,"该邮箱已被注册" ),
    ERROR_INSERT_USER(404, "插入时出错"),
    NEED_LOGIN(401, "需要登陆"),
    LOGIN_ERROE(403, "登陆信息错误"),
    SYSTEM_ERROR(500, "系统异常"),
    ERROR_ROLE_ID(404, "没有这个角色"),
    HAVE_RULE(403, "该门店已经有规则了"),
    CANT_BE_ADMIN(403, "不能新增管理员"), HASE_DATA(405, "当前排班已经存在");


    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
