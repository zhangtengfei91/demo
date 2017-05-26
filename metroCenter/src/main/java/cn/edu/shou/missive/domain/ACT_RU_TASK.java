package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by seky on 14-7-28.
 */
@Entity
public class ACT_RU_TASK {
    public String getID_() {
        return ID_;
    }

    public void setID_(String ID_) {
        this.ID_ = ID_;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Getter  @Setter private String ID_;//任务ID

    public String getPROC_INST_ID_() {
        return PROC_INST_ID_;
    }

    public void setPROC_INST_ID_(String PROC_INST_ID_) {
        this.PROC_INST_ID_ = PROC_INST_ID_;
    }

    @Getter  @Setter private String PROC_INST_ID_;//流程实例ID

    public String getNAME_() {
        return NAME_;
    }

    public void setNAME_(String NAME_) {
        this.NAME_ = NAME_;
    }

    @Getter  @Setter private String NAME_;//任务名称

    @Getter @Setter
    private String PROC_DEF_ID_;//流程类型
    @Getter @Setter
    private String ASSIGNEE_;//操作人

    public String getPROC_DEF_ID_() {
        return PROC_DEF_ID_;
    }

    public void setPROC_DEF_ID_(String PROC_DEF_ID_) {
        this.PROC_DEF_ID_ = PROC_DEF_ID_;
    }

    public String getASSINGEE_() {
        return ASSIGNEE_;
    }

    public void setASSIGNEE_(String ASSINGEE_) {
        this.ASSIGNEE_ = ASSIGNEE_;
    }
}
