package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenCharacterService;
import cn.edu.shou.missive.domain.MetroCenServiceWay;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by seky on 15/1/28.
 */
public interface MetroCenCharacterServiceRepository extends PagingAndSortingRepository<MetroCenCharacterService,Long> {

    public List<MetroCenCharacterService> findAll();
    //根据Id查找名称
    @Query("select characterService from MetroCenCharacterService characterService where characterService.id=:characterId")
    public MetroCenCharacterService getServiceNameById(@Param("characterId")long characterId);
}
