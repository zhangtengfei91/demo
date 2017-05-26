package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/23.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenSampleMethod extends BaseEntity{

    private String sampleMethod;//取样方式

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSampleMethod() {
        return sampleMethod;
    }

    public void setSampleMethod(String sampleMethod) {
        this.sampleMethod = sampleMethod;
    }
}
