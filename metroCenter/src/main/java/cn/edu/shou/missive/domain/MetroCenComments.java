package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/9.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenComments extends BaseEntity {

    @Getter @Setter
    private String context;//意见内容
    @Getter @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String processType;//流程类型
    @Getter @Setter
    private long userId;//意见创建者
    @Getter @Setter
    private int isAbnormal;//是否有异常


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getIsAbnormal() {
        return isAbnormal;
    }

    public void setIsAbnormal(int isAbnormal) {
        this.isAbnormal = isAbnormal;
    }
}
