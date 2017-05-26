package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class SignType extends BaseEntity {

    private String typeName;

    @OneToMany(cascade= CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="signType")
    private List<MissiveSign> missiveSignList;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<MissiveSign> getMissiveSignList() {
        return missiveSignList;
    }

    public void setMissiveSignList(List<MissiveSign> missiveSignList) {
        this.missiveSignList = missiveSignList;
    }
}
