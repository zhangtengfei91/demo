package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/1/5.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenClient extends BaseEntity{



    @Getter @Setter
    private String contractNo;//委托合同编号
    @Getter @Setter
    private String certiCode;//证书单位组织机构代码
    @Getter @Setter
    private String unitName;//委托单位全称
    @Getter @Setter
    private String certiName;//证书单位全称
    @Getter @Setter
    private String unitAddress;//委托方单位地址
    @Getter @Setter
    private String contacts;//委托方联系人
    @Getter @Setter
    private String phone;//手机
    @Getter @Setter
    private String telephone;//联系电话
    @Getter @Setter
    private String email;//电子邮件
    @Getter @Setter
    private String contactSecond;//第二个联系人
    @Getter @Setter
    private String enterpriseNature;//企业性质

    @Getter @Setter
    private String account;//年结
    @Getter @Setter
    private String upUnit;//上级主管单位
    @Getter @Setter
    private String postCode;//邮编

    public String getPostCode() {

        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getContactSecond() {
        return contactSecond;
    }

    public void setContactSecond(String contactSecond) {
        this.contactSecond = contactSecond;
    }

    public String getEnterpriseNature() {
        return enterpriseNature;
    }

    public void setEnterpriseNature(String enterpriseNature) {
        this.enterpriseNature = enterpriseNature;
    }

    public String getUpUnit() {
        return upUnit;
    }

    public void setUpUnit(String upUnit) {
        this.upUnit = upUnit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getCertiCode() {
        return certiCode;
    }

    public void setCertiCode(String certiCode) {
        this.certiCode = certiCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getCertiName() {
        return certiName;
    }

    public void setCertiName(String certiName) {
        this.certiName = certiName;
    }

    public String getUnitAddress() {
        return unitAddress;
    }

    public void setUnitAddress(String unitAddress) {
        this.unitAddress = unitAddress;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
