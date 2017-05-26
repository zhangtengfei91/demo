package cn.edu.shou.missive.domain.missiveDataForm;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by seky on 15/1/15.
 */
public class MetroCenSampleForm extends BaseEntityForm{

    @Getter @Setter
    private String contractNo;//委托合同编号
    @Getter @Setter
    private String certiCode;//证书单位组织机构代码
    @Getter @Setter
    private String unitName;//委托单位全称
    @Getter @Setter
    private String certiName;//证书单位全称
    @Getter @Setter
    private String unitAddress;//委托方单位地址
    @Getter @Setter
    private String contacts;//委托方联系人
    @Getter @Setter
    private String phone;//手机
    @Getter @Setter
    private String telephone;//联系电话
    @Getter @Setter
    private String email;//电子邮件

    @Getter @Setter
    private String sampleName;//样品名称
    @Getter @Setter
    private String sampleCode;//样品编号/唯一性标识
    @Getter @Setter
    private String accuracyLevel;//准确度等级
    @Getter @Setter
    private String measureRange;//测量范围
    @Getter @Setter
    private String specificateModel;//规格型号
    @Getter @Setter
    private String factoryName;//制造厂
    @Getter @Setter
    private int sampleNum;//样品数量
    @Getter @Setter
    private long serviceType;//服务类型
    @Getter @Setter
    private long serviceWay;//服务方式
    @Getter @Setter
    private String sampleTest;//样品检测
    @Getter @Setter
    private String sampleSurface;//样品外观
    @Getter @Setter
    private String sampleElectric;//通电检查
    @Getter @Setter
    private String sampleSpecial;//特殊检查
    @Getter @Setter
    private String principalRequre;//委托方需求
    @Getter @Setter
    private String principalTestRequre;//特殊检测要求
    @Getter @Setter
    private String principalTestBaseOn;//特殊检测依据
    @Getter @Setter
    private String principalOther;//特殊其他要求
    @Getter @Setter
    private long sampleMethod;//取样方式
    @Getter @Setter
    private long statusName;//样品状态

    @Getter @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String taskId;//任务编号
    @Getter @Setter
    private String remark;//备注
    @Getter @Setter
    private long receptId;//收发人员
    @Getter @Setter
    private long distributionId;//分发人员
    @Setter @Getter
    private long characterService;//特色服务
    @Getter @Setter
    private String surveillancePro;//检测项目
    @Setter @Getter
    private String sampleDetected;//已检测项目
    @Getter @Setter
    private String factoryCode;//出厂编号
    @Getter @Setter
    private String factoryCodes;//出厂编号集合
    @Getter @Setter
    private String createDate;//创建日期

@Getter @Setter
private  String messageSend;

    public String getMessageSend() {
        return messageSend;
    }

    public void setMessageSend(String messageSend) {
        this.messageSend = messageSend;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCertiCode() {
        return certiCode;
    }

    public void setCertiCode(String certiCode) {
        this.certiCode = certiCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getCertiName() {
        return certiName;
    }

    public void setCertiName(String certiName) {
        this.certiName = certiName;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public String getAccuracyLevel() {
        return accuracyLevel;
    }

    public void setAccuracyLevel(String accuracyLevel) {
        this.accuracyLevel = accuracyLevel;
    }

    public String getMeasureRange() {
        return measureRange;
    }

    public void setMeasureRange(String measureRange) {
        this.measureRange = measureRange;
    }

    public String getSpecificateModel() {
        return specificateModel;
    }

    public void setSpecificateModel(String specificateModel) {
        this.specificateModel = specificateModel;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public int getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(int sampleNum) {
        this.sampleNum = sampleNum;
    }

    public long getServiceType() {
        return serviceType;
    }

    public void setServiceType(long serviceType) {
        this.serviceType = serviceType;
    }

    public long getServiceWay() {
        return serviceWay;
    }

    public void setServiceWay(long serviceWay) {
        this.serviceWay = serviceWay;
    }

    public String getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(String sampleTest) {
        this.sampleTest = sampleTest;
    }

    public String getPrincipalRequre() {
        return principalRequre;
    }

    public void setPrincipalRequre(String principalRequre) {
        this.principalRequre = principalRequre;
    }

    public String getPrincipalTestRequre() {
        return principalTestRequre;
    }

    public void setPrincipalTestRequre(String principalTestRequre) {
        this.principalTestRequre = principalTestRequre;
    }

    public String getPrincipalTestBaseOn() {
        return principalTestBaseOn;
    }

    public void setPrincipalTestBaseOn(String principalTestBaseOn) {
        this.principalTestBaseOn = principalTestBaseOn;
    }

    public String getPrincipalOther() {
        return principalOther;
    }

    public void setPrincipalOther(String principalOther) {
        this.principalOther = principalOther;
    }

    public long getSampleMethod() {
        return sampleMethod;
    }

    public void setSampleMethod(long sampleMethod) {
        this.sampleMethod = sampleMethod;
    }

    public long getStatusName() {
        return statusName;
    }

    public void setStatusName(long statusName) {
        this.statusName = statusName;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getReceptId() {
        return receptId;
    }

    public void setReceptId(long receptId) {
        this.receptId = receptId;
    }

    public long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(long distributionId) {
        this.distributionId = distributionId;
    }

    public long getCharacterService() {
        return characterService;
    }

    public void setCharacterService(long characterService) {
        this.characterService = characterService;
    }

    public String getSurveillancePro() {
        return surveillancePro;
    }

    public void setSurveillancePro(String surveillancePro) {
        this.surveillancePro = surveillancePro;
    }

    public String getSampleDetected() {
        return sampleDetected;
    }

    public void setSampleDetected(String sampleDetected) {
        this.sampleDetected = sampleDetected;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public String getFactoryCodes() {
        return factoryCodes;
    }

    public void setFactoryCodes(String factoryCodes) {
        this.factoryCodes = factoryCodes;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSampleSurface() {
        return sampleSurface;
    }

    public void setSampleSurface(String sampleSurface) {
        this.sampleSurface = sampleSurface;
    }

    public String getSampleElectric() {
        return sampleElectric;
    }

    public void setSampleElectric(String sampleElectric) {
        this.sampleElectric = sampleElectric;
    }

    public String getSampleSpecial() {
        return sampleSpecial;
    }

    public void setSampleSpecial(String sampleSpecial) {
        this.sampleSpecial = sampleSpecial;
    }
}
