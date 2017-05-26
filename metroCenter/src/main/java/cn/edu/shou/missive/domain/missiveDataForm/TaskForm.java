package cn.edu.shou.missive.domain.missiveDataForm;

import com.sun.jmx.snmp.tasks.Task;

import java.util.List;

/**
 * Created by TISSOT on 2014/9/23.
 */
public class TaskForm {
    private Long id;
    private String name;
    private String missiveVersion;//公文版本号
    private String missiveType;//公文类型
    private String missiveTitle;//公文标题
    private  Long lastTaskId;//上一步任务ID
    private Long processInstanceId;
    private String processDefinitionId;
    private String urgencyLevel;//公文紧急程度
    private List<NextTask> taskOperate;//当前任务操作选项
    private String taskState;
    private String intelTime;
    private String taskUrl;
    private String taskStartTime;
    private int versionNum;
    private String typeTitle;
    private String taskAssName;
    private String delayWarm;



    private String delayNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public String getMissiveVersion() {
        return missiveVersion;
    }

    public void setMissiveVersion(String missiveVersion) {
        this.missiveVersion = missiveVersion;
    }

    public String getMissiveType() {
        return missiveType;
    }

    public void setMissiveType(String missiveType) {
        this.missiveType = missiveType;
    }

    public String getMissiveTitle() {
        return missiveTitle;
    }

    public void setMissiveTitle(String missiveTitle) {
        this.missiveTitle = missiveTitle;
    }

    public Long getLastTaskId() {
        return lastTaskId;
    }

    public void setLastTaskId(Long lastTaskId) {
        this.lastTaskId = lastTaskId;
    }

    public List<NextTask> getTaskOperate() {
        return taskOperate;
    }

    public void setTaskOperate(List<NextTask> taskOperate) {
        this.taskOperate = taskOperate;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getIntelTime() {
        return intelTime;
    }

    public void setIntelTime(String intelTime) {
        this.intelTime = intelTime;
    }

    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
    }

    public String getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(String taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public int getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(int versionNum) {
        this.versionNum = versionNum;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTaskAssName() {
        return taskAssName;
    }

    public void setTaskAssName(String taskAssName) {
        this.taskAssName = taskAssName;
    }

    public String getDelayWarm() {
        return delayWarm;
    }

    public void setDelayWarm(String delayWarm) {
        this.delayWarm = delayWarm;
    }
    public String getDelayNum() {
        return delayNum;
    }

    public void setDelayNum(String delayNum) {
        this.delayNum = delayNum;
    }

}
