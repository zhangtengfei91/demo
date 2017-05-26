package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
/**
 * Created by seky on 14-8-1.
 */
public class faxCableReciveClass {
    @Setter @Getter
    public String faxCableID;//公文ID
    @Setter @Getter
    public String  processID;//流程ID
    @Setter @Getter
    public String taskID;//任务ID
    @Setter @Getter
    public String secretLv;//密级
    @Setter @Getter
    public String secretLvName;//密级名称
    @Setter @Getter
    public String faxType;//传真电报类别
    @Setter @Getter
    public String faxTypeName;//传真电报类别
    @Setter @Getter
    public String faxID;//传真电报ID
    @Setter @Getter
    public String signIssue_Person;//会签人员
    @Setter @Getter
    public String signIssue_PersonName;//会签人员
    @Setter @Getter
    public String signIssue_img;//会签手写图片内容地址
    @Setter @Getter
    public String signIssue_30url;//会签手写图片内容
    @Setter @Getter
    public String signIssue_Content;//会签输入内容
    @Setter @Getter
    public String leaderSign_Person;//处室领导人员
    @Setter @Getter
    public String leaderSign_PersonName;//处室领导人员
    @Setter @Getter
    public String leaderSign_Content;//处室领导输入内容
    @Setter @Getter
    public String leaderSign_img;//处室领导手写图片链接地址
    @Setter @Getter
    public String  leaderSign_30url;//处室领导手写图片内容
    @Setter @Getter
    public String mainSend_Person;//主送部门
    @Setter @Getter
    public String copyTo_Person;//抄送部门
    @Setter @Getter
    public String drafter;//拟稿人
    @Setter @Getter
    public String phoneNum;//电话号码
    @Setter @Getter
    public String composeUser;//排版
    @Setter @Getter
    public String officeCheck;//办公室复核
    @Setter @Getter
    public String printCount;//打印份数
    @Setter @Getter
    public String CheckReader;//校对人
    @Setter @Getter
    public String govPublic;//政府公开信息
    @Setter @Getter
    public String FaxCableTitle;//传真标题
    @Setter @Getter
    public String FaxCableAttrTittle;//附件标题
    @Setter @Getter
    public String MissiveID;//公文相关信息编号
    @Getter @Setter
    public String SigCommentID;//签发人员ID
    @Setter @Getter
    public String DepChkID;//处室领导人员ID
    @Setter @Getter
    public String AttachmentID;//附件ID
    @Setter @Getter
    public String VersionNum;//版本号
    @Setter @Getter
    public String AttrNameList;//附件名称字符串
    @Setter @Getter
    public String currentUserID;//当前用户
    @Setter @Getter
    public String MissiveTitle;//公文标题
    @Setter @Getter
    public String AuthorName;//作者
    @Setter @Getter
    public String MissiveDateTime;//公文日期
    @Setter @Getter
    public String MissivePDFContent;//公文附件PDF内容
    @Setter @Getter
    public String MissiveType;//公文类型
    @Setter @Getter
    public String MissiveNum;//公文编号
    @Setter @Getter
    public String mainSend_PersonName;//主送部门
    @Setter @Getter
    public String copyTo_PersonName;//抄送部门
    @Setter @Getter
    public String drafterName;//拟稿人
    @Setter @Getter
    public String composeUserName;//排版
    @Setter @Getter
    public String officeCheckName;//办公室复核
    @Setter @Getter
    public String CheckReaderName;//校对人

    public String getMissiveTitle() {
        return MissiveTitle;
    }

    public void setMissiveTitle(String missiveTitle) {
        MissiveTitle = missiveTitle;
    }
    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }
    public String getMissiveType() {
        return MissiveType;
    }
    public String getMissiveDateTime() {
        return MissiveDateTime;
    }

    public String getMissivePDFContent() {
        return MissivePDFContent;
    }

    public void setMissiveDateTime(String missiveDateTime) {
        MissiveDateTime = missiveDateTime;
    }
    public void setMissivePDFContent(String missivePDFContent) {
        MissivePDFContent = missivePDFContent;
    }
    public void setMissiveType(String missiveType) {
        MissiveType = missiveType;
    }
    public String getMissiveNum() {
        return MissiveNum;
    }

    public void setMissiveNum(String missiveNum) {
        MissiveNum = missiveNum;
    }

    public String getFaxCableID() {
        return faxCableID;
    }

    public void setFaxCableID(String faxCableID) {
        this.faxCableID = faxCableID;
    }

    public String getProcessID() {
        return processID;
    }

    public void setProcessID(String processID) {
        this.processID = processID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getSecretLv() {
        return secretLv;
    }

    public void setSecretLv(String secretLv) {
        this.secretLv = secretLv;
    }

    public String getFaxType() {
        return faxType;
    }

    public void setFaxType(String faxType) {
        this.faxType = faxType;
    }

    public String getFaxID() {
        return faxID;
    }

    public void setFaxID(String faxID) {
        this.faxID = faxID;
    }

    public String getSignIssue_Person() {
        return signIssue_Person;
    }

    public void setSignIssue_Person(String signIssue_Person) {
        this.signIssue_Person = signIssue_Person;
    }

    public String getSignIssue_img() {
        return signIssue_img;
    }

    public void setSignIssue_img(String signIssue_img) {
        this.signIssue_img = signIssue_img;
    }

    public String getSignIssue_30url() {
        return signIssue_30url;
    }

    public void setSignIssue_30url(String signIssue_30url) {
        this.signIssue_30url = signIssue_30url;
    }

    public String getSignIssue_Content() {
        return signIssue_Content;
    }

    public void setSignIssue_Content(String signIssue_Content) {
        this.signIssue_Content = signIssue_Content;
    }

    public String getLeaderSign_Person() {
        return leaderSign_Person;
    }

    public void setLeaderSign_Person(String leaderSign_Person) {
        this.leaderSign_Person = leaderSign_Person;
    }

    public String getLeaderSign_Content() {
        return leaderSign_Content;
    }

    public void setLeaderSign_Content(String leaderSign_Content) {
        this.leaderSign_Content = leaderSign_Content;
    }

    public String getLeaderSign_img() {
        return leaderSign_img;
    }

    public void setLeaderSign_img(String leaderSign_img) {
        this.leaderSign_img = leaderSign_img;
    }

    public String getLeaderSign_30url() {
        return leaderSign_30url;
    }

    public void setLeaderSign_30url(String leaderSign_30url) {
        this.leaderSign_30url = leaderSign_30url;
    }

    public String getMainSend_Person() {
        return mainSend_Person;
    }

    public void setMainSend_Person(String mainSend_Person) {
        this.mainSend_Person = mainSend_Person;
    }

    public String getCopyTo_Person() {
        return copyTo_Person;
    }

    public void setCopyTo_Person(String copyTo_Person) {
        this.copyTo_Person = copyTo_Person;
    }

    public String getDrafter() {
        return drafter;
    }

    public void setDrafter(String drafter) {
        this.drafter = drafter;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getComposeUser() {
        return composeUser;
    }

    public void setComposeUser(String composeUser) {
        this.composeUser = composeUser;
    }

    public String getOfficeCheck() {
        return officeCheck;
    }

    public void setOfficeCheck(String officeCheck) {
        this.officeCheck = officeCheck;
    }

    public String getPrintCount() {
        return printCount;
    }

    public void setPrintCount(String printCount) {
        this.printCount = printCount;
    }

    public String getCheckReader() {
        return CheckReader;
    }

    public void setCheckReader(String checkReader) {
        CheckReader = checkReader;
    }

    public String getGovPublic() {
        return govPublic;
    }

    public void setGovPublic(String govPublic) {
        this.govPublic = govPublic;
    }

    public String getFaxCableTitle() {
        return FaxCableTitle;
    }

    public void setFaxCableTitle(String faxCableTitle) {
        FaxCableTitle = faxCableTitle;
    }

    public String getFaxCableAttrTittle() {
        return FaxCableAttrTittle;
    }

    public void setFaxCableAttrTittle(String faxCableAttrTittle) {
        FaxCableAttrTittle = faxCableAttrTittle;
    }

    public String getMissiveID() {
        return MissiveID;
    }

    public void setMissiveID(String missiveID) {
        MissiveID = missiveID;
    }

    public String getSigCommentID() {
        return SigCommentID;
    }

    public void setSigCommentID(String sigCommentID) {
        SigCommentID = sigCommentID;
    }

    public String getDepChkID() {
        return DepChkID;
    }

    public void setDepChkID(String depChkID) {
        DepChkID = depChkID;
    }

    public String getAttachmentID() {
        return AttachmentID;
    }

    public void setAttachmentID(String attachmentID) {
        AttachmentID = attachmentID;
    }

    public String getVersionNum() {
        return VersionNum;
    }

    public void setVersionNum(String versionNum) {
        VersionNum = versionNum;
    }

    public String getAttrNameList() {
        return AttrNameList;
    }

    public void setAttrNameList(String attrNameList) {
        AttrNameList = attrNameList;
    }

    public String getCurrentUserID() {
        return currentUserID;
    }

    public void setCurrentUserID(String currentUserID) {
        this.currentUserID = currentUserID;
    }
    public String getSecretLvName() {
        return secretLvName;
    }

    public void setSecretLvName(String secretLvName) {
        this.secretLvName = secretLvName;
    }
    public String getFaxTypeName() {
        return faxTypeName;
    }

    public void setFaxTypeName(String faxTypeName) {
        this.faxTypeName = faxTypeName;
    }
    public String getSignIssue_PersonName() {
        return signIssue_PersonName;
    }

    public void setSignIssue_PersonName(String signIssue_PersonName) {
        this.signIssue_PersonName = signIssue_PersonName;
    }
    public String getLeaderSign_PersonName() {
        return leaderSign_PersonName;
    }

    public void setLeaderSign_PersonName(String leaderSign_PersonName) {
        this.leaderSign_PersonName = leaderSign_PersonName;
    }
    public String getMainSend_PersonName() {
        return mainSend_PersonName;
    }

    public void setMainSend_PersonName(String mainSend_PersonName) {
        this.mainSend_PersonName = mainSend_PersonName;
    }
    public String getCopyTo_PersonName() {
        return copyTo_PersonName;
    }

    public void setCopyTo_PersonName(String copyTo_PersonName) {
        this.copyTo_PersonName = copyTo_PersonName;
    }

    public String getDrafterName() {
        return drafterName;
    }

    public void setDrafterName(String drafterName) {
        this.drafterName = drafterName;
    }

    public String getComposeUserName() {
        return composeUserName;
    }

    public void setComposeUserName(String composeUserName) {
        this.composeUserName = composeUserName;
    }

    public String getOfficeCheckName() {
        return officeCheckName;
    }

    public void setOfficeCheckName(String officeCheckName) {
        this.officeCheckName = officeCheckName;
    }

    public String getCheckReaderName() {
        return CheckReaderName;
    }

    public void setCheckReaderName(String checkReaderName) {
        CheckReaderName = checkReaderName;
    }
}
