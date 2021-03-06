package cn.edu.shou.missive.service;
import cn.edu.shou.missive.domain.Schedule;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
/*import org.springframework.data.rest.core.annotation.RepositoryRestResource;*/
import java.util.List;

/**
 * Created by TISSOT on 2014/7/13.
 */
public interface ScheduleRepository extends CrudRepository <Schedule,Long> {
    @Query("select a from Schedule a where a.user = :userName and  a.isDel=:isDelor")
    public List<Schedule> findByUser(@Param("userName") String userName, @Param("isDelor") String isDelor);

}
