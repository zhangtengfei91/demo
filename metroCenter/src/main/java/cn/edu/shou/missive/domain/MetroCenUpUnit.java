package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by zhengxl on 15/7/2.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenUpUnit extends BaseEntity {


    @Getter @Setter
    private String upUnit;//该上级主管单位名称

    @Getter @Setter
    private String clientId;//该上级主管管辖内的所有客户单位
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUpUnit() {
        return upUnit;
    }

    public void setUpUnit(String upUnit) {
        this.upUnit = upUnit;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
