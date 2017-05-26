package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenStandardEquipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/3/13.
 */
public interface MetroCenStandardEquipmentRepository extends PagingAndSortingRepository<MetroCenStandardEquipment,Long> {

    //根据模板Id，返回计量器具、装置等相关信息
    @Query("select equip from MetroCenStandardEquipment equip where equip.modelId=:modelId")
    public List<MetroCenStandardEquipment> getEquipMentByModelId(@Param("modelId")long modelId);
}
