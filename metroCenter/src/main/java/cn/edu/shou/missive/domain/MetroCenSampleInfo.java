package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/4/9.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenSampleInfo extends BaseEntity {

    @Getter @Setter
    private String sampleName;//样品名称
    @Getter @Setter
    private String accuracyLevel;//准确度等级
    @Getter @Setter
    private String measureRange;//测量范围
    @Getter @Setter
    private String specificateModel;//规格型号
    @Getter @Setter
    private String factoryName;//制造厂
    @Getter @Setter
    private String factoryCode;//出厂编号

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
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

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }
}
