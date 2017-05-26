package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;


/**
 * Created by zhengxl on 15/6/25.
 */

@Entity

public class userAuthority extends BaseEntity {
    @Getter
    @Setter
    private String userId;


    @Getter
    @Setter
    private String authorityName;

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
