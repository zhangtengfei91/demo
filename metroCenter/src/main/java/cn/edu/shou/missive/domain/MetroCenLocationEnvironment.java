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
public class MetroCenLocationEnvironment extends BaseEntity{


    @Getter @Setter
    private String location;//地点
    @Getter @Setter
    private String temperature;//温度
    @Getter @Setter
    private String relativeHum;//相对湿度
    @Getter @Setter
    private String other;//其他
    @Getter @Setter
    private long certificateId;//证书编号
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

    public long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(long certificateId) {
        this.certificateId = certificateId;
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
}
