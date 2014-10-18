package com.letv.watchball.bean;

import com.letv.http.bean.LetvBaseBean;

/**
 * 用户信息对象
 * 请求：
 * {@link LetvHttpApi#login(int updataId , String loginname , String password , String registService , String profile ,LetvMainParser<T, D> parser)}
 * {@link LetvHttpApi#requestUserInfo(int updataId , String uid , LetvMainParser<T, D> parser)}
 * 解析：
 * {@link com.letv.watchball.parse.UserParser}
 * */
public class User implements LetvBaseBean{

	public static final String ISVIP_YES = "1";
	public static final String ISVIP_NO = "0";
	
	private String uid = "";//用户id
	private String username = "";
	private String status = "";//状态0表示获取数据失败1表示获取数据成功
	private String gender = "";//性别
	private String qq = "";
	private String registIp = "";
	private String registTime = "";//注册时间
	private String lastModifyTime = "";//修改用户时间
	private String birthday = "";//生日
	private String nickname = "";//昵称
	private String msn = "";
	private String registService = "";//注册方式，移动端为mapp，标志位免激活
	private String email = "";//邮箱
	private String mobile = "";//手机
	private String province = "";//省份
	private String city = "";//城市
	private String postCode = "";
	private String address = "";
	private String mac = "";
	private String picture = "";
	private String name = "";
	private String contactEmail = "";
	private String delivery = "";
	private String point = "";
	private String level_id = "";
	private String isvip = "";//是否是vip
	private String education = "";
	private String industry = "";
	private String job = "";
	private String income = "";
	private String lastLoginTime = "";
	private String lastLoginIp = "";
	private String tv_token ;
	
	private VipInfo vipInfo ; 
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public String getRegistIp() {
		return registIp;
	}
	public void setRegistIp(String registIp) {
		this.registIp = registIp;
	}
	public String getRegistTime() {
		return registTime;
	}
	public void setRegistTime(String registTime) {
		this.registTime = registTime;
	}
	public String getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(String lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getMsn() {
		return msn;
	}
	public void setMsn(String msn) {
		this.msn = msn;
	}
	public String getRegistService() {
		return registService;
	}
	public void setRegistService(String registService) {
		this.registService = registService;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getDelivery() {
		return delivery;
	}
	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getLevel_id() {
		return level_id;
	}
	public void setLevel_id(String level_id) {
		this.level_id = level_id;
	}
	public String getIsvip() {
		return isvip;
	}
	public void setIsvip(String isvip) {
		this.isvip = isvip;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getIncome() {
		return income;
	}
	public void setIncome(String income) {
		this.income = income;
	}
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public String getLastLoginIp() {
		return lastLoginIp;
	}
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	
	public VipInfo getVipInfo() {
		return vipInfo;
	}
	public void setVipInfo(VipInfo vipInfo) {
		this.vipInfo = vipInfo;
	}

	public String getTv_token() {
		return tv_token;
	}
	public void setTv_token(String tv_token) {
		this.tv_token = tv_token;
	}

	public static class VipInfo {
		private String id ;
		private String username ;
		private long canceltime ;
		private int orderFrom ;
		private int productid ;
		private int vipType ;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public long getCanceltime() {
			return canceltime;
		}
		public void setCanceltime(long canceltime) {
			this.canceltime = canceltime;
		}
		public int getOrderFrom() {
			return orderFrom;
		}
		public void setOrderFrom(int orderFrom) {
			this.orderFrom = orderFrom;
		}
		public int getProductid() {
			return productid;
		}
		public void setProductid(int productid) {
			this.productid = productid;
		}
		public int getVipType() {
			return vipType;
		}
		public void setVipType(int vipType) {
			this.vipType = vipType;
		}
		
	}
	
	@Override
	public String toString() {
		return "LetvUser [uid=" + uid + ", username=" + username + ", status="
				+ status + ", gender=" + gender + ", qq=" + qq + ", registIp="
				+ registIp + ", registTime=" + registTime + ", lastModifyTime="
				+ lastModifyTime + ", birthday=" + birthday + ", nickname="
				+ nickname + ", msn=" + msn + ", registService="
				+ registService + ", email=" + email + ", mobile=" + mobile
				+ ", province=" + province + ", city=" + city + ", postCode="
				+ postCode + ", address=" + address + ", mac=" + mac
				+ ", picture=" + picture + ", name=" + name + ", contactEmail="
				+ contactEmail + ", delivery=" + delivery + ", point=" + point
				+ ", level_id=" + level_id + ", isvip=" + isvip
				+ ", education=" + education + ", industry=" + industry
				+ ", job=" + job + ", income=" + income + ", lastLoginTime="
				+ lastLoginTime + ", lastLoginIp=" + lastLoginIp + "]";
	}
}
