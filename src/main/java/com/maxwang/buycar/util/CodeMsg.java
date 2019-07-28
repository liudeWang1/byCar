package com.maxwang.buycar.util;

public class CodeMsg {
	
	private int code;
	private String msg;
	
	//通用的错误码
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
	//登录注册模块 5002XX
	public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
	public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211, "登录密码不能为空");
	public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212, "手机号不能为空");
	public static CodeMsg MOBILE_ERROR = new CodeMsg(500213, "手机号格式错误");
	public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号不存在");
	public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
	public static CodeMsg REGISTER_ERROR = new CodeMsg(500216, "注册失败");
	public static CodeMsg USERNAME_IS_EXIST = new CodeMsg(500217, "用户名已存在");
	public static CodeMsg TEL_IS_EXIST = new CodeMsg(500218, "手机号已存在");
	public static CodeMsg AUTHORITY_NOT_ENOUGHT = new CodeMsg(500219, "权限不够");
	public static CodeMsg USERNAME_NOT_EXIST = new CodeMsg(500220, "用户名不存在");
	public static CodeMsg USERNAME_IS_NULL = new CodeMsg(500221, "用户名为空");

	
	//汽车模块 5003XX
	public static CodeMsg ADD_CAR_ERROR = new CodeMsg(500301, "注册失败");
	public static CodeMsg SEARCH_ERROR = new CodeMsg(500301, "查找失败");
	public static CodeMsg SEARCH_ADMIN_CAR_ERROR = new CodeMsg(500301, "后台查找汽车失败");
	public static CodeMsg CAR_DETAIL_NOT_FOUND = new CodeMsg(500302, "汽车详情未找到");
	public static CodeMsg CAR_UPDATE_ERROR = new CodeMsg(500303, "汽车更新失败");
	public static CodeMsg EVALUATE_UPDATE_ERROR = new CodeMsg(500304, "汽车标准更新失败");
	public static CodeMsg EVALUATE_DELETE_ERROR = new CodeMsg(500305, "汽车标准删除失败");
	public static CodeMsg CARCOMPLETEVO_IS_NULL = new CodeMsg(500305, "CARCOMPLETEVO查询失败");

	//用户模块 5004XX
	public static CodeMsg SEARCH_USER_LIST_ERROR = new CodeMsg(500401, "查找用户列表失败");
	public static CodeMsg USERID_NOT_FOUND = new CodeMsg(500402, "用户ID不存在");
	public static CodeMsg UPDATE_ERROR = new CodeMsg(500403, "更新失败");
	public static CodeMsg DELETE_ERROR = new CodeMsg(500404, "删除失败");

	//文章模块 5005XX
	public static CodeMsg ESSAY_SEARCH_ERROR = new CodeMsg(500500, "文章列表查询失败");
	public static CodeMsg ESSAY_SEARCH_DETAIL_ERROR = new CodeMsg(500502, "文章详情查询失败");
	public static CodeMsg ESSAY_NOT_EXIST = new CodeMsg(500501, "文章不存在");
	public static CodeMsg ESSAY_UPDATE_ERROR = new CodeMsg(500503, "更新文章失败");
	public static CodeMsg ESSAY_DELETE_ERROR = new CodeMsg(500503, "删除文章失败");

	private CodeMsg( ) {
	}
			
	private CodeMsg( int code,String msg ) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
	
	
}
