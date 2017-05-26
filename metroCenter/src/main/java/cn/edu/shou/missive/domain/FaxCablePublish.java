package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import javax.persistence.Entity;

/**
 * Created by seky on 14-7-24.
 */
@Entity
public class FaxCablePublish {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Getter @Setter
    public long processID;//實例ID
    @Getter @Setter
    public long taskID;//任務ID

    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="missiveInfo")
    public Missive missiveInfo;//公文相關數據，收文號、密級、類型

    @Getter @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="FaxCablePublish_signUser", joinColumns={@JoinColumn(name="FaxCablePublishId")}, inverseJoinColumns={@JoinColumn(name="UserId")})
    public List<User> signIssueUsers;   //签发人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="signIssueContent")
    public CommentContent signIssueContent;//签发内容

    @Getter @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="faxCablePublish_leaderSignUser", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="UserId")})
    public List<User> CounterSignUsers;//处室领导人员
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="leaderSignContent")
    public CommentContent leaderSignContent;//处室领导内容

    @ManyToMany
    @JoinTable(name="faxCablePublish_mainSendGroups", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="GroupId")})
    @Getter @Setter
    public List<Group> MainSendGroups;   //主送部门

    @ManyToMany
    @JoinTable(name="faxCablePublish_copytoGroups", joinColumns={@JoinColumn(name="missivePublishId")}, inverseJoinColumns={@JoinColumn(name="GroupId")})
    @Getter @Setter
    public List<Group> CopytoGroups;   //抄送部门


    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="DrafterUser")
    public User DrafterUser;//拟稿人
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="ComposeUser")
    public User ComposeUser;//排版人
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="OfficeCheckUser")
    public User OfficeCheckUser;//办公室复核
    @Getter @Setter
    public int printCount;//打印份数
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL)
    @JoinColumn(name="CheckReader")
    public User CheckReader;//校对
    @Getter @Setter
    public int Gov_info_attr;//政府信息属性
    @Getter @Setter
    public String missiveTittle;//标题
    @Getter @Setter
    public String attachmentTittle;//附件标题
    @Getter @Setter
    public String bgPngPath;//背景图片路径

    public Missive getMissiveInfo() {
        return missiveInfo;
    }

    public void setMissiveInfo(Missive missiveInfo) {
        this.missiveInfo = missiveInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public List<User> getSignIssueUsers() {
        return signIssueUsers;
    }

    public void setSignIssueUsers(List<User> signIssueUsers) {
        this.signIssueUsers = signIssueUsers;
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

    public CommentContent getLeaderSignContent() {
        return leaderSignContent;
    }

    public void setLeaderSignContent(CommentContent leaderSignContent) {
        this.leaderSignContent = leaderSignContent;
    }

    public List<Group> getMainSendGroups() {
        return MainSendGroups;
    }

    public void setMainSendGroups(List<Group> mainSendGroups) {
        MainSendGroups = mainSendGroups;
    }

    public List<Group> getCopytoGroups() {
        return CopytoGroups;
    }

    public void setCopytoGroups(List<Group> copytoGroups) {
        CopytoGroups = copytoGroups;
    }

    public User getDrafterUser() {
        return DrafterUser;
    }

    public void setDrafterUser(User drafterUser) {
        DrafterUser = drafterUser;
    }

    public User getComposeUser() {
        return ComposeUser;
    }

    public void setComposeUser(User composeUser) {
        ComposeUser = composeUser;
    }

    public User getOfficeCheckUser() {
        return OfficeCheckUser;
    }

    public void setOfficeCheckUser(User officeCheckUser) {
        OfficeCheckUser = officeCheckUser;
    }

    public int getPrintCount() {
        return printCount;
    }

    public void setPrintCount(int printCount) {
        this.printCount = printCount;
    }

    public User getCheckReader() {
        return CheckReader;
    }

    public void setCheckReader(User checkReader) {
        CheckReader = checkReader;
    }

    public int getGov_info_attr() {
        return Gov_info_attr;
    }

    public void setGov_info_attr(int gov_info_attr) {
        Gov_info_attr = gov_info_attr;
    }

    public String getMissiveTittle() {
        return missiveTittle;
    }

    public void setMissiveTittle(String missiveTittle) {
        this.missiveTittle = missiveTittle;
    }

    public String getAttachmentTittle() {
        return attachmentTittle;
    }

    public void setAttachmentTittle(String attachmentTittle) {
        this.attachmentTittle = attachmentTittle;
    }

    public String getBgPngPath() {
        return bgPngPath;
    }

    public void setBgPngPath(String bgPngPath) {
        this.bgPngPath = bgPngPath;
    }
}
