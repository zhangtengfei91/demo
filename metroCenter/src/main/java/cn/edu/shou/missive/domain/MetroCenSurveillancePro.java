package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/28.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenSurveillancePro extends BaseEntity{

    private String surveillanceName;//服务名称

    public String lab;//实验室

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurveillanceName() {
        return surveillanceName;
    }

    public void setSurveillanceName(String surveillanceName) {
        this.surveillanceName = surveillanceName;
    }
}
