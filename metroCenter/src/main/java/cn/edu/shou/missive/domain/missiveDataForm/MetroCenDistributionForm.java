package cn.edu.shou.missive.domain.missiveDataForm;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by seky on 15/1/21.
 */
public class MetroCenDistributionForm extends BaseEntityForm {

    @Getter
    @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String taskId;//任务编号
    @Getter @Setter
    private long sampleId;//样品编号
    @Getter @Setter
    private String sampleName;//样品名称
    @Getter @Setter
    private String factoryCode;//出厂编号
    @Getter @Setter
    private int num;//数量
    @Getter @Setter
    private Date receivedDate;//接收日期
    @Getter @Setter
    private String receivedDateStr;//接收日期字符串
    @Getter @Setter
    private long projectType;//项目类型
    @Getter @Setter
    private long accreditedId;//检定员

    @Getter @Setter
    private Date recReturnDate;//建议归还日期
    @Getter @Setter
    private Date actReturnDate;//实际归还日期

    @Getter @Setter
    private String recReturnDateStr;//建议归还日期字符
    @Getter @Setter
    private String actReturnDateStr;//实际归还日期字符

    @Getter @Setter
    private long receiveId;//收发员

    @Getter @Setter
    private String sampleCode;//唯一性标识
    @Getter @Setter
    private long statusName;//样品状态
    @Getter @Setter
    private String remark;//备注
    @Getter @Setter
    private long labName;//实验室名称
    @Setter @Getter
    private String back;//归还样品
    @Setter @Getter
    private Date backTime;//归还时间
    @Setter @Getter
    private String take;//取样品
    @Getter @Setter
    private Date taskTime;//取样时间
    @Getter @Setter
    private long distributionId;//分发人员
    @Getter @Setter
    private String surveillancePro;//检测项目

    @Getter @Setter
    private int certifiSubmit;//证书是否制作

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public long getSampleId() {
        return sampleId;
    }

    public void setSampleId(long sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public long getProjectType() {
        return projectType;
    }

    public void setProjectType(long projectType) {
        this.projectType = projectType;
    }

    public long getAccreditedId() {
        return accreditedId;
    }

    public void setAccreditedId(long accreditedId) {
        this.accreditedId = accreditedId;
    }

    public Date getRecReturnDate() {
        return recReturnDate;
    }

    public void setRecReturnDate(Date recReturnDate) {
        this.recReturnDate = recReturnDate;
    }

    public Date getActReturnDate() {
        return actReturnDate;
    }

    public void setActReturnDate(Date actReturnDate) {
        this.actReturnDate = actReturnDate;
    }

    public long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(long receiveId) {
        this.receiveId = receiveId;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public long getStatusName() {
        return statusName;
    }

    public void setStatusName(long statusName) {
        this.statusName = statusName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getLabName() {
        return labName;
    }

    public void setLabName(long labName) {
        this.labName = labName;
    }

    public String getReceivedDateStr() {
        return receivedDateStr;
    }

    public void setReceivedDateStr(Date receivedDate) {
        this.receivedDateStr = new SimpleDateFormat("yyyy-MM-dd").format(receivedDate);
    }

    public String getRecReturnDateStr() {
        return recReturnDateStr;
    }

    public void setRecReturnDateStr(Date recReturnDate) {
        this.recReturnDateStr =new SimpleDateFormat("yyyy-MM-dd").format(recReturnDate);
    }

    public String getActReturnDateStr() {
        return actReturnDateStr;
    }

    public void setActReturnDateStr(Date actReturnDate) {
        this.actReturnDateStr = new SimpleDateFormat("yyyy-MM-dd").format(actReturnDate);
    }

    public String getBack() {
        return back;
    }

    public void setBack(String back) {
        this.back = back;
    }

    public Date getBackTime() {
        return backTime;
    }

    public void setBackTime(Date backTime) {
        this.backTime = backTime;
    }

    public String getTake() {
        return take;
    }

    public void setTake(String take) {
        this.take = take;
    }

    public Date getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(Date taskTime) {
        this.taskTime = taskTime;
    }

    public long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(long distributionId) {
        this.distributionId = distributionId;
    }

    public String getSurveillancePro() {
        return surveillancePro;
    }

    public void setSurveillancePro(String surveillancePro) {
        this.surveillancePro = surveillancePro;
    }

    public int getCertifiSubmit() {
        return certifiSubmit;
    }

    public void setCertifiSubmit(int certifiSubmit) {
        this.certifiSubmit = certifiSubmit;
    }
}
