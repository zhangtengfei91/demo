package cn.edu.shou.missive.service;

import cn.edu.shou.missive.domain.MetroCenPost;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Administrator on 2015/5/26 0026.
 */
public interface MetroCenPostRepository  extends PagingAndSortingRepository<MetroCenPost,Long> {
    List<MetroCenPost> findAll();
}
