package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.SignType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by sqhe18 on 14-9-5.
 */
public interface SignTypeRespository extends PagingAndSortingRepository<SignType,Long> {
    public List<SignType> findAll();
    public SignType findOne(Long id);
}
