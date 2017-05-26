package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/3/3.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenStandardEquipment extends BaseEntity{

    @Getter @Setter
    private String equipmentName;//装置名称
    @Getter @Setter
    private String measureRange;//测量范围
    @Getter @Setter
    private String equipCertificateNo;//装置证书编号
    @Getter @Setter
    private String dueDate;//有效期至
    @Getter @Setter
    private String accuracy;//准确度等级、不确定度、最大允许误差
    @Getter @Setter
    private long modelId;//模板编号
    @Getter @Setter
    private long parentModelId;//
    @Getter @Setter
    private String instrumentNo;//仪器编号
    @Getter @Setter
    private int type;//类型,(1:是装置,2:是器具)
    @Getter @Setter
    private String accuracyCN;
    @Getter @Setter
    private String accuracyEN;
    @Getter @Setter
    private String instrumentAccuracyCN;
    @Getter @Setter
    private String instrumentAccuracyEN;

    @Getter @Setter
    private String remark1;//备用字段
    @Getter @Setter
    private String remark2;//备用字段
    @Getter @Setter
    private String remark3;//备用字段


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getMeasureRange() {
        return measureRange;
    }

    public void setMeasureRange(String measureRange) {
        this.measureRange = measureRange;
    }

    public String getEquipCertificateNo() {
        return equipCertificateNo;
    }

    public void setEquipCertificateNo(String equipCertificateNo) {
        this.equipCertificateNo = equipCertificateNo;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public String getInstrumentNo() {
        return instrumentNo;
    }

    public void setInstrumentNo(String instrumentNo) {
        this.instrumentNo = instrumentNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    public String getAccuracyCN() {
        return accuracyCN;
    }

    public void setAccuracyCN(String accuracyCN) {
        this.accuracyCN = accuracyCN;
    }

    public String getAccuracyEN() {
        return accuracyEN;
    }

    public void setAccuracyEN(String accuracyEN) {
        this.accuracyEN = accuracyEN;
    }

    public String getInstrumentAccuracyCN() {
        return instrumentAccuracyCN;
    }

    public void setInstrumentAccuracyCN(String instrumentAccuracyCN) {
        this.instrumentAccuracyCN = instrumentAccuracyCN;
    }

    public String getInstrumentAccuracyEN() {
        return instrumentAccuracyEN;
    }

    public void setInstrumentAccuracyEN(String instrumentAccuracyEN) {
        this.instrumentAccuracyEN = instrumentAccuracyEN;
    }


    public long getParentModelId() {
        return parentModelId;
    }

    public void setParentModelId(long parentModelId) {
        this.parentModelId = parentModelId;
    }
}
