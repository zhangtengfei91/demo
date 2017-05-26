package cn.edu.shou.missive.domain.missiveDataForm;
import lombok.Getter;
import lombok.Setter;
import cn.edu.shou.missive.domain.BaseEntity;

/**
 * Created by 张腾飞 on 2016/1/8.
 */
public class MetroCenMessageModelForm extends BaseEntityForm {
    @Getter @Setter
    public String taskName;//任务状态
    public String msgContent;//短信模板内容

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
}
