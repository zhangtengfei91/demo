package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * Created by seky on 15/3/3.
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class MetroCenReslutIllustrate extends BaseEntity{

    @Getter @Setter
    private String results;//结果内容
    @Getter @Setter
    private long certificateId;//证书编号
    @Getter @Setter
    private int nextPage;//是否需要显示续页
    @Getter @Setter
    private int pageHeight;//续页高度
    @Getter @Setter
    private String remark1;//备用字段
    @Getter @Setter
    private String remark2;//备用字段
    @Getter @Setter
    private String remark3;//备用字段

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public long getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(long certificateId) {
        this.certificateId = certificateId;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }
}
