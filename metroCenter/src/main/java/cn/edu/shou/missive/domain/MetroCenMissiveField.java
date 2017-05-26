package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by Administrator on 2015/3/16 0016.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenMissiveField extends BaseEntity {
    @Getter @Setter
    private String fieldName;
    @Getter @Setter
    private long processTypeId;
    @Getter @Setter
    private long taskId;
    @Getter @Setter
    private String inputId;
    @Getter @Setter
    private String inputType;
    @Getter @Setter
    private String isDel;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public long getProcessTypeId() {
        return processTypeId;
    }

    public void setProcessTypeId(long processTypeId) {
        this.processTypeId = processTypeId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getInputId() {
        return inputId;
    }

    public void setInputId(String inputId) {
        this.inputId = inputId;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getIsDel() {
        return isDel;
    }

    public void setIsDel(String isDel) {
        this.isDel = isDel;
    }
}
