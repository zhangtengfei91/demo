package cn.edu.shou.missive.domain.missiveDataForm;

import java.util.List;

/**
 * Created by sqhe18 on 14-9-5.
 */
public class SignTypeForm extends BaseEntityForm {
    public String typeName;
    public List<MissiveSignForm> missiveSignList;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<MissiveSignForm> getMissiveSignList() {
        return missiveSignList;
    }

    public void setMissiveSignList(List<MissiveSignForm> missiveSignList) {
        this.missiveSignList = missiveSignList;
    }
}
