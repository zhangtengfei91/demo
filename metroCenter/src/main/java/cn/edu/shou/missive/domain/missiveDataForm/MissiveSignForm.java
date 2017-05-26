package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;

/**
 * Created by sqhe18 on 14-9-4.
 */
public class MissiveSignForm extends BaseEntityForm {
    public long processID;//實例ID
    public long taskID;//任務ID

    public SignTypeForm signType;

    public MissiveFrom missiveInfo;//公文相關數據，收文號、密級、類型、

    public UserFrom Dep_LeaderCheckUser;//处(室)领导核稿
    public CommentContentFrom Dep_LeaderCheckContent;//处(室)领导核稿内容

    public UserFrom OfficeCheckUser;//办公室复核
    public CommentContentFrom OfficeCheckContent;//办公室复核内容

    public UserFrom SignIssueUser;   //签发人员
    public CommentContentFrom signIssueContent;//签发内容

    public List<UserFrom> CounterSignUsers;//会签人员
    public CommentContentFrom counterSignContent;//会签内容

    public UserFrom DrafterUser;//拟稿人

    public String missiveTittle;//标题

    public String backgroudImage;

    public long getProcessID() {
        return processID;
    }

    public void setProcessID(long processID) {
        this.processID = processID;
    }

    public long getTaskID() {
        return taskID;
    }

    public void setTaskID(long taskID) {
        this.taskID = taskID;
    }

    public SignTypeForm getSignType() {
        return signType;
    }

    public void setSignType(SignTypeForm signType) {
        this.signType = signType;
    }

    public MissiveFrom getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(MissiveFrom missiveInfo) {
        this.missiveInfo = missiveInfo;
    }

    public UserFrom getDep_LeaderCheckUser() {
        return Dep_LeaderCheckUser;
    }

    public void setDep_LeaderCheckUser(UserFrom dep_LeaderCheckUser) {
        Dep_LeaderCheckUser = dep_LeaderCheckUser;
    }

    public CommentContentFrom getDep_LeaderCheckContent() {
        return Dep_LeaderCheckContent;
    }

    public void setDep_LeaderCheckContent(CommentContentFrom dep_LeaderCheckContent) {
        Dep_LeaderCheckContent = dep_LeaderCheckContent;
    }

    public UserFrom getOfficeCheckUser() {
        return OfficeCheckUser;
    }

    public void setOfficeCheckUser(UserFrom officeCheckUser) {
        OfficeCheckUser = officeCheckUser;
    }

    public CommentContentFrom getOfficeCheckContent() {
        return OfficeCheckContent;
    }

    public void setOfficeCheckContent(CommentContentFrom officeCheckContent) {
        OfficeCheckContent = officeCheckContent;
    }

    public UserFrom getSignIssueUser() {
        return SignIssueUser;
    }

    public void setSignIssueUser(UserFrom signIssueUser) {
        SignIssueUser = signIssueUser;
    }

    public CommentContentFrom getSignIssueContent() {
        return signIssueContent;
    }

    public void setSignIssueContent(CommentContentFrom signIssueContent) {
        this.signIssueContent = signIssueContent;
    }

    public List<UserFrom> getCounterSignUsers() {
        return CounterSignUsers;
    }

    public void setCounterSignUsers(List<UserFrom> counterSignUsers) {
        CounterSignUsers = counterSignUsers;
    }

    public CommentContentFrom getCounterSignContent() {
        return counterSignContent;
    }

    public void setCounterSignContent(CommentContentFrom counterSignContent) {
        this.counterSignContent = counterSignContent;
    }

    public UserFrom getDrafterUser() {
        return DrafterUser;
    }

    public void setDrafterUser(UserFrom drafterUser) {
        DrafterUser = drafterUser;
    }

    public String getMissiveTittle() {
        return missiveTittle;
    }

    public void setMissiveTittle(String missiveTittle) {
        this.missiveTittle = missiveTittle;
    }

    public String getBackgroudImage() {
        return backgroudImage;
    }

    public void setBackgroudImage(String backgroudImage) {
        this.backgroudImage = backgroudImage;
    }
}
