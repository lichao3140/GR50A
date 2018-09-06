package com.runvision.bean;

import java.io.File;

import com.runvision.core.Const;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/4/9.
 */

public class AppData {

	private String MessageId; // 消息ID
	private String MethodId;// 接口ID
	private String DeviceNo;// 设备编号
	private String Sign;// 签名
	private String RetCode;// 是否接收成功
	private String Message;// 消息说明
	private String DeviceKey;
	private String IP;// 设备Ip 地址
	private int Port;
	private String UpperIp;// 上位机IP
	private int UpperPort;
	private String ImgReceiveUrl;// 过往人员抓拍图片接收（地址）
	private String ParkingDeviceNo;// 车场设备编号
	private String Time;// 时 间
	private String GUID;// 生成的唯一ID
	private String Plate;// 车牌号
	private String Qrcode;// 二维码内容
	private int Status;
	private String DisplayContent;// 显示文字内容
	private String UyghurDisplayContent;
	private String VoiceContent;// 播报语音
	private String UyghurVoiceContent;// 维吾尔语音内容
	private int MessageLevel;// 消息级别
	private Boolean CompareResult;// 比对结果
	private int CardType;// 证件类型
	private String CardNo;// 证件号
	private String Name;// 姓名
	private int Gender;// 性别
	private String Nation;// 名族代码
	private String Nationality;// 国籍
	private String Birthday;// 生日
	private String Address;// 地址
	private String CardStartTime;// 证件上的有效开始时间
	private String CardEndTime;// 证件上的有效结束时间
	private String IssueDepartment;// 证件上的发证机构
	private Boolean HasFinger;// 是否有指纹
	private String FingerFeature0;// 证件上的左手指纹信息
	private String FingerFeature1;// 证件上的右手指纹信息
	private String CollectFinger;// 人证设备采集的指纹信息
	private int QualityScore;// 人证比对的质量分数
	private int CompareScore;// 人证比对的比分
	private int CompareResult_1;// 向公安平台请求验证证件黑白名单结果；0:校验不通过；1：校验通过
	private String VoiceFileName;
	private String UyghurVoiceFileName;
	private int flag = Const.FLAG_CLEAN;
	//图片上传角度
	private String imageType;
	private File faceFile;
	private File cardFile;
	private String Version;//软件版本号
	private String DownloadLink;//apk下载地址

	private String PortalSysName;
	private String PortalUyghurSysName;
	private String PortalImgUrl;
	private boolean isHaveFace;




	public boolean isHaveFace() {
		return isHaveFace;
	}

	public void setHaveFace(boolean isHaveFace) {
		this.isHaveFace = isHaveFace;
	}

	public static AppData mAppData = new AppData();

	public static AppData getAppData() {
		return mAppData;
	}

	public String getPortalSysName() {
		return PortalSysName;
	}


	public void setPortalSysName(String portalSysName) {
		PortalSysName = portalSysName;
	}


	public String getPortalUyghurSysName() {
		return PortalUyghurSysName;
	}


	public void setPortalUyghurSysName(String portalUyghurSysName) {
		PortalUyghurSysName = portalUyghurSysName;
	}


	public String getPortalImgUrl() {
		return PortalImgUrl;
	}


	public void setPortalImgUrl(String portalImgUrl) {
		PortalImgUrl = portalImgUrl;
	}

	public String getDeviceKey() {
		return DeviceKey;
	}

	public void setDeviceKey(String deviceKey) {
		DeviceKey = deviceKey;
	}

	public File getFaceFile() {
		return faceFile;
	}

	public void setFaceFile(File faceFile) {
		this.faceFile = faceFile;
	}

	public File getCardFile() {
		return cardFile;
	}

	public void setCardFile(File cardFile) {
		this.cardFile = cardFile;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	private Bitmap faceBmp;//人脸图片

	public Bitmap getFaceBmp() {
		return faceBmp;
	}

	public void setFaceBmp(Bitmap faceBmp) {
		this.faceBmp = faceBmp;
	}

	public Bitmap getCardBmp() {
		return cardBmp;
	}

	public void setCardBmp(Bitmap cardBmp) {
		this.cardBmp = cardBmp;
	}

	private Bitmap cardBmp;//身份证图片


	public String getMessageId() {
		return MessageId;
	}

	public void setMessageId(String messageId) {
		MessageId = messageId;
	}

	public String getMethodId() {
		return MethodId;
	}

	public void setMethodId(String methodId) {
		MethodId = methodId;
	}

	public String getDeviceNo() {
		return DeviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		DeviceNo = deviceNo;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getRetCode() {
		return RetCode;
	}

	public void setRetCode(String retCode) {
		RetCode = retCode;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String IP) {
		this.IP = IP;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int Port) {
		this.Port = Port;
	}

	public String getUpperIp() {
		return UpperIp;
	}

	public void setUpperIp(String upperIp) {
		UpperIp = upperIp;
	}

	public int getUpperPort() {
		return UpperPort;
	}

	public void setUpperPort(int upperPort) {
		this.UpperPort = upperPort;
	}

	public String getImgReceiveUrl() {
		return ImgReceiveUrl;
	}

	public void setImgReceiveUrl(String imgReceiveUrl) {
		ImgReceiveUrl = imgReceiveUrl;
	}

	public String getParkingDeviceNo() {
		return ParkingDeviceNo;
	}

	public void setParkingDeviceNo(String parkingDeviceNo) {
		ParkingDeviceNo = parkingDeviceNo;
	}

	public int getQualityScore() {
		return QualityScore;
	}

	public void setQualityScore(int qualityScore) {
		QualityScore = qualityScore;
	}

	public int getCompareScore() {
		return CompareScore;
	}

	public void setCompareScore(int compareScore) {
		CompareScore = compareScore;
	}

	public int getCompareResult_1() {
		return CompareResult_1;
	}

	public void setCompareResult_1(int compareResult_1) {
		CompareResult_1 = compareResult_1;
	}

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public String getGUID() {
		return GUID;
	}

	public void setGUID(String GUID) {
		this.GUID = GUID;
	}

	public String getPlate() {
		return Plate;
	}

	public void setPlate(String plate) {
		Plate = plate;
	}

	public String getQrcode() {
		return Qrcode;
	}

	public void setQrcode(String qrcode) {
		Qrcode = qrcode;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int Status) {
		this.Status = Status;
	}

	public String getDisplayContent() {
		return DisplayContent;
	}

	public void setDisplayContent(String displayContent) {
		DisplayContent = displayContent;
	}

	public String getUyghurDisplayContent() {
		return UyghurDisplayContent;
	}

	public void setUyghurDisplayContent(String UyghurDisplayContent) {
		this.UyghurDisplayContent = UyghurDisplayContent;
	}

	public String getVoiceContent() {
		return VoiceContent;
	}

	public void setVoiceContent(String voiceContent) {
		VoiceContent = voiceContent;
	}

	public String getUyghurVoiceContent() {
		return UyghurVoiceContent;
	}

	public void setUyghurVoiceContent(String uyghurVoiceContent) {
		UyghurVoiceContent = uyghurVoiceContent;
	}

	public int getMessageLevel() {
		return MessageLevel;
	}

	public void setMessageLevel(int messageLevel) {
		MessageLevel = messageLevel;
	}

	public Boolean getCompareResult() {
		return CompareResult;
	}

	public void setCompareResult(Boolean compareResult) {
		CompareResult = compareResult;
	}

	public int getCardType() {
		return CardType;
	}

	public void setCardType(int cardType) {
		CardType = cardType;
	}

	public String getCardNo() {
		return CardNo;
	}

	public void setCardNo(String cardNo) {
		CardNo = cardNo;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getGender() {
		return Gender;
	}

	public void setGender(int gender) {
		Gender = gender;
	}

	public String getNation() {
		return Nation;
	}

	public void setNation(String nation) {
		Nation = nation;
	}

	public String getNationality() {
		return Nationality;
	}

	public void setNationality(String nationality) {
		Nationality = nationality;
	}

	public String getBirthday() {
		return Birthday;
	}

	public void setBirthday(String birthday) {
		Birthday = birthday;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getCardStartTime() {
		return CardStartTime;
	}

	public void setCardStartTime(String cardStartTime) {
		CardStartTime = cardStartTime;
	}

	public String getCardEndTime() {
		return CardEndTime;
	}

	public void setCardEndTime(String cardEndTime) {
		CardEndTime = cardEndTime;
	}

	public String getIssueDepartment() {
		return IssueDepartment;
	}

	public void setIssueDepartment(String issueDepartment) {
		IssueDepartment = issueDepartment;
	}

	public Boolean getHasFinger() {
		return HasFinger;
	}

	public void setHasFinger(Boolean hasFinger) {
		HasFinger = hasFinger;
	}

	public String getFingerFeature0() {
		return FingerFeature0;
	}

	public void setFingerFeature0(String fingerFeature0) {
		FingerFeature0 = fingerFeature0;
	}

	public String getFingerFeature1() {
		return FingerFeature1;
	}

	public void setFingerFeature1(String fingerFeature1) {
		FingerFeature1 = fingerFeature1;
	}

	public String getCollectFinger() {
		return CollectFinger;
	}

	public void setCollectFinger(String collectFinger) {
		CollectFinger = collectFinger;
	}

	public String getVoiceFileName() {
		return VoiceFileName;
	}

	public void setVoiceFileName(String voicefileName) {
		VoiceFileName = voicefileName;
	}

	public String getUyghurVoiceFileName() {
		return UyghurVoiceFileName;
	}

	public void setUyghurVoiceFileName(String uyghurVoiceFileName) {
		UyghurVoiceFileName = uyghurVoiceFileName;
	}

	public String getVersion() {
		return Version;
	}

	public void setVersion(String version) {
		Version = version;
	}

	public String getDownloadLink() {
		return DownloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		DownloadLink = downloadLink;
	}


}
