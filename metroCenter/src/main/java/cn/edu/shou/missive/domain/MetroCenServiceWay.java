package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/23.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenServiceWay extends BaseEntity{

    private String serviceWay;//服务方式


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServiceWay() {
        return serviceWay;
    }

    public void setServiceWay(String serviceWay) {
        this.serviceWay = serviceWay;
    }
}
