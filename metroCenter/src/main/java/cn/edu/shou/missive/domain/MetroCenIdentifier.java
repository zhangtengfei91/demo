package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/4/23.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenIdentifier extends BaseEntity {
    @Getter @Setter
    private String serialNumber;//编号
    @Getter @Setter
    private String type;//类别
    @Getter @Setter
    private long sampleId;//样品Id

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSampleId() {
        return sampleId;
    }

    public void setSampleId(long sampleId) {
        this.sampleId = sampleId;
    }
}
