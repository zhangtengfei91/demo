package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by Administrator on 2015/3/11 0011.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenTaskName extends BaseEntity {
    @Getter @Setter
    private String taskName;//任务名称
    @Getter @Setter
    private long processTypeId;//流程类型

    @Getter
    @Setter
    @ManyToOne(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="processType")
    private ProcessType processType;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public long getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(long processTypeId) {
        this.processTypeId = processTypeId;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }
}
