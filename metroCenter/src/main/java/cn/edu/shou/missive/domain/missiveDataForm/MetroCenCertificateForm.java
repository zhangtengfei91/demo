package cn.edu.shou.missive.domain.missiveDataForm;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by seky on 15/3/3.
 */
public class MetroCenCertificateForm extends BaseEntityForm  {

    @Getter @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String taskId;//任务编号
    @Getter @Setter
    private String inspectionUnit;//送检单位
    @Getter @Setter
    private String sampleName;//计量器具名称
    @Getter @Setter
    private String sampleModel;//型号/规格
    @Getter @Setter
    private String serialNumber;//出厂编号
    @Getter @Setter
    private String manufactUnit;//制造单位
    @Getter @Setter
    private String certificateNo;//证书编号
    @Getter @Setter
    private long countNo;//证书编号计数
    @Getter @Setter
    private String verificatReg;//检定依据
    @Getter @Setter
    private String conclusion;//检定结论
    @Getter @Setter
    private long approved;//批准人
    @Getter @Setter
    private long checked;//核验员
    @Getter @Setter
    private long verified;//检定员
    @Getter @Setter
    private String certificateType;//证书类别
    @Getter @Setter
    private String verifiedDate;//检定日期
    @Getter @Setter
    private String validityDate;//有效期至
    @Getter @Setter
    private long sampleId;//样品编号
    @Getter @Setter
    private long distributionId;//分发人员
    @Getter @Setter
    private String certificatePath;//证书路径
    @Getter @Setter
    private long certificateCheckId;//证书审核人员
    @Getter @Setter
    private long authorize;//授权
    @Getter @Setter
    private long modelId;//证书大小类模板ID
    @Getter @Setter
    private long modelParentId;//证书大类模板ID
    @Getter @Setter
    private long client;//客户信息
    @Getter @Setter
    private long locationEnvId;//地点及环境条件Id
    @Getter @Setter
    private long resultId;//检定结果与说明Id
    @Getter @Setter
    private long nextPage;//续页标志
    @Getter @Setter
    private int pageHeight;//续页高度
    @Setter @Getter
    private String uncertainty;//扩展不确定度
    //领导意见
    @Getter @Setter
    private String comments;

    //检定地点及环境条件
    @Getter @Setter
    private String location;//地点
    @Setter @Getter
    private String temperature;//温度
    @Setter @Getter
    private String relativeHum;//相对湿度
    @Setter @Getter
    private String other;//其他
    //检定结果与说明
    @Setter @Getter
    private String results;//其他
    @Setter @Getter
    private String certificateBackGroundParent;//证书大类背景图片
    @Setter @Getter
    private String getCertificateBackGroundChild;//证书小类背景图片
    @Setter @Getter
    private long retroactive;//是否出具证书标示
    @Setter @Getter
    private String returnCer;//标示是否交接

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

    public String getInspectionUnit() {
        return inspectionUnit;
    }

    public void setInspectionUnit(String inspectionUnit) {
        this.inspectionUnit = inspectionUnit;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleModel() {
        return sampleModel;
    }

    public void setSampleModel(String sampleModel) {
        this.sampleModel = sampleModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufactUnit() {
        return manufactUnit;
    }

    public void setManufactUnit(String manufactUnit) {
        this.manufactUnit = manufactUnit;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getVerificatReg() {
        return verificatReg;
    }

    public void setVerificatReg(String verificatReg) {
        this.verificatReg = verificatReg;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public long getApproved() {
        return approved;
    }

    public void setApproved(long approved) {
        this.approved = approved;
    }

    public long getChecked() {
        return checked;
    }

    public void setChecked(long checked) {
        this.checked = checked;
    }

    public long getVerified() {
        return verified;
    }

    public void setVerified(long verified) {
        this.verified = verified;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(String verifiedDate) {
        this.verifiedDate = verifiedDate;
    }

    public String getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(String validityDate) {
        this.validityDate = validityDate;
    }

    public long getSampleId() {
        return sampleId;
    }

    public void setSampleId(long sampleId) {
        this.sampleId = sampleId;
    }

    public long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(long distributionId) {
        this.distributionId = distributionId;
    }

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public long getCertificateCheckId() {
        return certificateCheckId;
    }

    public void setCertificateCheckId(long certificateCheckId) {
        this.certificateCheckId = certificateCheckId;
    }

    public long getAuthorize() {
        return authorize;
    }

    public void setAuthorize(long authorize) {
        this.authorize = authorize;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public long getClient() {
        return client;
    }

    public void setClient(long client) {
        this.client = client;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getRelativeHum() {
        return relativeHum;
    }

    public void setRelativeHum(String relativeHum) {
        this.relativeHum = relativeHum;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public long getLocationEnvId() {
        return locationEnvId;
    }

    public void setLocationEnvId(long locationEnvId) {
        this.locationEnvId = locationEnvId;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public long getNextPage() {
        return nextPage;
    }

    public void setNextPage(long nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public String getCertificateBackGroundParent() {
        return certificateBackGroundParent;
    }

    public void setCertificateBackGroundParent(String certificateBackGroundParent) {
        this.certificateBackGroundParent = certificateBackGroundParent;
    }

    public String getGetCertificateBackGroundChild() {
        return getCertificateBackGroundChild;
    }

    public void setGetCertificateBackGroundChild(String getCertificateBackGroundChild) {
        this.getCertificateBackGroundChild = getCertificateBackGroundChild;
    }

    public long getModelParentId() {
        return modelParentId;
    }

    public void setModelParentId(long modelParentId) {
        this.modelParentId = modelParentId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(String uncertainty) {
        this.uncertainty = uncertainty;
    }

    public long getCountNo() {
        return countNo;
    }

    public void setCountNo(long countNo) {
        this.countNo = countNo;
    }

    public long getRetroactive() {
        return retroactive;
    }

    public void setRetroactive(long retroactive) {
        this.retroactive = retroactive;
    }

    public String getReturnCer() {
        return returnCer;
    }

    public void setReturnCer(String returnCer) {
        this.returnCer = returnCer;
    }
}
