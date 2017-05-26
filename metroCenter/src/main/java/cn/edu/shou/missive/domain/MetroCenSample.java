package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.restlet.resource.Get;

import javax.persistence.*;

/**
 * Created by seky on 15/1/5.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenSample extends BaseEntity{

    @Getter @Setter
    private String sampleName;//样品名称
    @Getter @Setter
    private String sampleCode;//样品编号/唯一性标识
    @Getter @Setter
    private String accuracyLevel;//准确度等级
    @Getter @Setter
    private String measureRange;//测量范围
    @Getter @Setter
    private String specificateModel;//规格型号
    @Getter @Setter
    private String factoryName;//制造厂
    @Getter @Setter
    private int sampleNum;//样品数量
    @Getter @Setter
    @OneToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceType")
    private MetroCenServiceType serviceType;//服务类型
    @Getter @Setter
    @OneToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "serviceWay")
    private MetroCenServiceWay serviceWay;//服务方式
    @Getter @Setter
    private String sampleTest;//样品检测
    @Getter @Setter
    private String principalRequre;//委托方需求

    @Getter @Setter
    private String principalTestRequre;//特殊检测要求
    @Getter @Setter
    private String principalTestBaseOn;//特殊检测依据
    @Getter @Setter
    private String principalOther;//特殊其他要求
    @Getter @Setter
    @OneToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "sampleMethod")
    private MetroCenSampleMethod sampleMethod;//取样方式
    @Getter @Setter
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "statusName")
    private MetroCenStatus statusName;//样品状态
    @Setter @Getter
    private String surveillancePro;//检测项目
    @Setter @Getter
    private String sampleDetected;//已检测项目
    @Setter @Getter
    @OneToOne(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "characterService")
    private MetroCenCharacterService characterService;//特色服务
    @Getter @Setter
    private String processId;//流程编号
    @Getter @Setter
    private String taskId;//任务编号
    @Getter @Setter
    @ManyToOne(cascade= CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name="client")
    private MetroCenClient client;//客户信息
    @Getter @Setter
    private String remark;//备注
    @Getter @Setter
    private long receptId;//收发人员
    @Getter @Setter
    private long distributionId;//分发人员
    @Getter @Setter
    private String factoryCode;//出厂编号


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public String getSampleCode() {
        return sampleCode;
    }

    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    public String getAccuracyLevel() {
        return accuracyLevel;
    }

    public void setAccuracyLevel(String accuracyLevel) {
        this.accuracyLevel = accuracyLevel;
    }

    public String getMeasureRange() {
        return measureRange;
    }

    public void setMeasureRange(String measureRange) {
        this.measureRange = measureRange;
    }

    public String getSpecificateModel() {
        return specificateModel;
    }

    public void setSpecificateModel(String specificateModel) {
        this.specificateModel = specificateModel;
    }

    public String getFactoryName() {
        return factoryName;
    }

    public void setFactoryName(String factoryName) {
        this.factoryName = factoryName;
    }

    public int getSampleNum() {
        return sampleNum;
    }

    public void setSampleNum(int sampleNum) {
        this.sampleNum = sampleNum;
    }

    public MetroCenServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(MetroCenServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public MetroCenServiceWay getServiceWay() {
        return serviceWay;
    }

    public void setServiceWay(MetroCenServiceWay serviceWay) {
        this.serviceWay = serviceWay;
    }

    public String getSampleTest() {
        return sampleTest;
    }

    public void setSampleTest(String sampleTest) {
        this.sampleTest = sampleTest;
    }

    public String getPrincipalRequre() {
        return principalRequre;
    }

    public void setPrincipalRequre(String principalRequre) {
        this.principalRequre = principalRequre;
    }

    public String getPrincipalTestRequre() {
        return principalTestRequre;
    }

    public void setPrincipalTestRequre(String principalTestRequre) {
        this.principalTestRequre = principalTestRequre;
    }

    public String getPrincipalTestBaseOn() {
        return principalTestBaseOn;
    }

    public void setPrincipalTestBaseOn(String principalTestBaseOn) {
        this.principalTestBaseOn = principalTestBaseOn;
    }

    public String getPrincipalOther() {
        return principalOther;
    }

    public void setPrincipalOther(String principalOther) {
        this.principalOther = principalOther;
    }

    public MetroCenSampleMethod getSampleMethod() {
        return sampleMethod;
    }

    public void setSampleMethod(MetroCenSampleMethod sampleMethod) {
        this.sampleMethod = sampleMethod;
    }

    public MetroCenStatus getStatusName() {
        return statusName;
    }

    public void setStatusName(MetroCenStatus statusName) {
        this.statusName = statusName;
    }

    public String getSurveillancePro() {
        return surveillancePro;
    }

    public void setSurveillancePro(String surveillancePro) {
        this.surveillancePro = surveillancePro;
    }

    public MetroCenCharacterService getCharacterService() {
        return characterService;
    }

    public void setCharacterService(MetroCenCharacterService characterService) {
        this.characterService = characterService;
    }

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

    public MetroCenClient getClient() {
        return client;
    }

    public void setClient(MetroCenClient client) {
        this.client = client;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getReceptId() {
        return receptId;
    }

    public void setReceptId(long receptId) {
        this.receptId = receptId;
    }

    public long getDistributionId() {
        return distributionId;
    }

    public void setDistributionId(long distributionId) {
        this.distributionId = distributionId;
    }

    public String getSampleDetected() {
        return sampleDetected;
    }

    public void setSampleDetected(String sampleDetected) {
        this.sampleDetected = sampleDetected;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }
}
