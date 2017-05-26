package cn.edu.shou.missive.domain.missiveDataForm;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by chenault1 on 15/10/30.
 */
public class MetroCenSampleHistoryForm extends BaseEntityForm {
    @Getter @Setter
    private String sampleName;//样品名称
    @Getter @Setter
    private String sampleCode;//样品编号
    @Getter @Setter
    private String sampleStatus;//样品状态
    @Getter @Setter
    private String sampleCharge;//样品负责人
    @Getter @Setter
    private String certiStatus;//证书状态
    @Getter @Setter
    private String certiCharge;//证书负责人
    @Getter @Setter
    private String identifier;//样品接收单号
    @Getter @Setter
    private String certificateNo;//证书编号
    @Getter @Setter
    private String statusPercent;//进度百分比

    public String getStatusPercent() {
        return statusPercent;
    }

    public void setStatusPercent(String statusPercent) {
        this.statusPercent = statusPercent;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
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

    public String getSampleStatus() {
        return sampleStatus;
    }

    public void setSampleStatus(String sampleStatus) {
        this.sampleStatus = sampleStatus;
    }

    public String getSampleCharge() {
        return sampleCharge;
    }

    public void setSampleCharge(String sampleCharge) {
        this.sampleCharge = sampleCharge;
    }

    public String getCertiStatus() {
        return certiStatus;
    }

    public void setCertiStatus(String certiStatus) {
        this.certiStatus = certiStatus;
    }

    public String getCertiCharge() {
        return certiCharge;
    }

    public void setCertiCharge(String certiCharge) {
        this.certiCharge = certiCharge;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
