package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MissiveSign;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by sqhe18 on 14-9-5.
 */
public interface MissiveSignRespository extends CrudRepository<MissiveSign, Long> {
    public MissiveSign findByProcessID(long processID);
    public List<MissiveSign> findAll();
}
