package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;
/**
 * 手机短信下行接口实体
 * 请求：
 * {@link s_sendMobile(int updataId , String mobile , LetvMainParser<T, D> parser)}
 * 解析：
 * {@link com.letv.watchball.parse.S_SendMobileResultParser}
 * */
public class S_SendMobileResult implements LetvBaseBean {

	/**
	 * 发送成功
	 */
	public static final int VALUE_SEND_SUCCESS = 200;
	/**
	 * 参数不正确
	 */
	public static final int VALUE_PARAMETER_ERROR = 1001;
	/**
	 * 发送失败,内容存在非法词，或内容太长。
	 */
	public static final int VALUE_SEND_ERROR = 1002;
	
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "S_SendMobileResult [code=" + code + "]";
	}
}
