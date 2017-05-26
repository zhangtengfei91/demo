package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/26.
 */
@Entity
public class MetroCenStatus extends BaseEntity {

    @Getter @Setter
    private  String statusName;//状态名称

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
