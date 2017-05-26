package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by 张腾飞 on 2015/12/22.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenMessageModel extends BaseEntity{
       @Getter @Setter
    public  String taskName;//模板名称

    public String getTaskName() {
        return taskName;
    }


    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    @Getter @Setter

    public  String msgContent;//模板内容

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



}
//张腾飞 2015 12 22