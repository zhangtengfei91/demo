package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by seky on 15/1/6.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenCertificate extends BaseEntity {

    @Getter @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String taskId;//任务编号
    @Getter @Setter
    private String inspectionUnit;//送检单位
    @Getter @Setter
    private String sampleName;//计量器具名称√
    @Getter @Setter
    private String sampleModel;//型号/规格
    @Getter @Setter
    private String serialNumber;//出厂编号
    @Getter @Setter
    private String manufactUnit;//制造单位
    @Getter @Setter
    private String certificateNo;//证书编号√

    @Getter @Setter
    private long countNo;//证书编号计数
    @Getter @Setter
    private String verificatReg;//检定依据
    @Getter @Setter
    private String conclusion;//检定结论√
    @Getter @Setter
    private long approved;//批准人√
    @Getter @Setter
    private long checked;//核验员√
    @Getter @Setter
    private long verified;//检定员√
    @Getter @Setter
    private String certificateType;//证书类别√
    @Getter @Setter
    private String verifiedDate;//检定日期√
    @Getter @Setter
    private String validityDate;//有效期至
    @Getter @Setter
    private long sampleId;//分发样品样品Id√
    @Getter @Setter
    private long distributionId;//分发人员√
    @Getter @Setter
    private String certificatePath;//证书路径
    @Getter @Setter
    private long certificateCheckId;//证书审核人员
    @Getter @Setter
    private long authorize;//授权
    @Getter @Setter
    private long modelId;//证书模板ID
    @Setter @Getter
    private String uncertainty;//扩展不确定度
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="client")
    private MetroCenClient client;//客户信息

    @Getter @Setter
    private String remark;//备注
    @Getter @Setter
    private long locationId;//检定地点Id
    @Getter @Setter
    private long resultId;//结果说明Id
    @Getter @Setter
    private long retroactive;//是否补办证书
    @Getter @Setter
    private String returnCer;//是否交接证书

    public long getRetroactive() {
        return retroactive;
    }

    public void setRetroactive(long retroactive) {
        this.retroactive = retroactive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getCertificatePath() {
        return certificatePath;
    }

    public void setCertificatePath(String certificatePath) {
        this.certificatePath = certificatePath;
    }

    public MetroCenClient getClient() {
        return client;
    }

    public void setClient(MetroCenClient client) {
        this.client = client;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(long distributionId) {
        this.distributionId = distributionId;
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

    public String getUncertainty() {
        return uncertainty;
    }

    public void setUncertainty(String uncertainty) {
        this.uncertainty = uncertainty;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getResultId() {
        return resultId;
    }

    public void setResultId(long resultId) {
        this.resultId = resultId;
    }

    public long getCountNo() {
        return countNo;
    }

    public void setCountNo(long countNo) {
        this.countNo = countNo;
    }

    public String getReturnCer() {
        return returnCer;
    }

    public void setReturnCer(String returnCer) {
        this.returnCer = returnCer;
    }
}
