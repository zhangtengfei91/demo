package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/3/10.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenCertificateBackGround extends BaseEntity {

    @Setter @Getter
    private String mainBackGround;//主要的背景图片
    @Setter @Getter
    private String equipBackGround;//装置及器具背景
    @Setter @Getter
    private long modelId;//证书模板Id
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

    public String getMainBackGround() {
        return mainBackGround;
    }

    public void setMainBackGround(String mainBackGround) {
        this.mainBackGround = mainBackGround;
    }

    public String getEquipBackGround() {
        return equipBackGround;
    }

    public void setEquipBackGround(String equipBackGround) {
        this.equipBackGround = equipBackGround;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
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
