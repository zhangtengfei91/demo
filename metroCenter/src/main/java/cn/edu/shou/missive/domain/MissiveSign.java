package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by sqhe18 on 14-9-4.
 */

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MissiveSign extends BaseEntity {
    @Getter
    @Setter
    private long processID;//實例ID
    @Getter @Setter
    private long taskID;//任務ID

    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="signTypeId")
    private SignType signType;


    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="missiveInfo")
    private Missive missiveInfo;//公文相關數據，收文號、密級、類型、

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="Dep_LeaderCheckUser")
    private User Dep_LeaderCheckUser;//处(室)领导核稿
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="Dep_LeaderCheckContentId")
    private CommentContent Dep_LeaderCheckContent;//处(室)领导核稿内容

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="OfficeCheckUser")
    private User OfficeCheckUser;//办公室复核
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="OfficeCheckContentId")
    private CommentContent OfficeCheckContent;//办公室复核内容

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="SignIssueUserId")
    private User SignIssueUser;   //签发人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="signIssueContent")
    private CommentContent signIssueContent;//签发内容

    @Getter @Setter
    @ManyToMany
    @JoinTable(name="missiveSign_counterSignUser", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="UserId")})
    private List<User> CounterSignUsers;//会签人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="counterSignContent")
    private CommentContent counterSignContent;//会签内容

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="DrafterUser")
    private User DrafterUser;//拟稿人



    private String missiveTittle;//标题

    private String backgroudImage;

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

    public SignType getSignType() {
        return signType;
    }

    public void setSignType(SignType signType) {
        this.signType = signType;
    }

    public Missive getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(Missive missiveInfo) {
        this.missiveInfo = missiveInfo;
    }

    public User getDep_LeaderCheckUser() {
        return Dep_LeaderCheckUser;
    }

    public void setDep_LeaderCheckUser(User dep_LeaderCheckUser) {
        Dep_LeaderCheckUser = dep_LeaderCheckUser;
    }

    public CommentContent getDep_LeaderCheckContent() {
        return Dep_LeaderCheckContent;
    }

    public void setDep_LeaderCheckContent(CommentContent dep_LeaderCheckContent) {
        Dep_LeaderCheckContent = dep_LeaderCheckContent;
    }

    public User getOfficeCheckUser() {
        return OfficeCheckUser;
    }

    public void setOfficeCheckUser(User officeCheckUser) {
        OfficeCheckUser = officeCheckUser;
    }

    public CommentContent getOfficeCheckContent() {
        return OfficeCheckContent;
    }

    public void setOfficeCheckContent(CommentContent officeCheckContent) {
        OfficeCheckContent = officeCheckContent;
    }

    public User getSignIssueUser() {
        return SignIssueUser;
    }

    public void setSignIssueUser(User signIssueUser) {
        SignIssueUser = signIssueUser;
    }

    public CommentContent getSignIssueContent() {
        return signIssueContent;
    }

    public void setSignIssueContent(CommentContent signIssueContent) {
        this.signIssueContent = signIssueContent;
    }

    public List<User> getCounterSignUsers() {
        return CounterSignUsers;
    }

    public void setCounterSignUsers(List<User> counterSignUsers) {
        CounterSignUsers = counterSignUsers;
    }

    public CommentContent getCounterSignContent() {
        return counterSignContent;
    }

    public void setCounterSignContent(CommentContent counterSignContent) {
        this.counterSignContent = counterSignContent;
    }

    public User getDrafterUser() {
        return DrafterUser;
    }

    public void setDrafterUser(User drafterUser) {
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
