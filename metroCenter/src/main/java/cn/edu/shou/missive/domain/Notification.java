package cn.edu.shou.missive.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by TISSOT on 2014/7/17.
 */
@Entity
@Table(name="notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter  @Setter private Long id;//通知编号

    @Getter  @Setter private String writer;//通知作者
    @Getter  @Setter private String title;//通知标题

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter  @Setter private String content;//通知文本内容

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Getter  @Setter private String contentHtml;//通知网页内容

    @Getter  @Setter private String time;//通知发布时间
    @Getter  @Setter private Date createTime;//创建时间
    @Getter  @Setter private Date updateTime;//更新时间
    @Getter  @Setter private String isDel;//删除标识
    //@Getter  @Setter  private boolean status; //已读标志


    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "notification")
    @JsonIgnore
    private List<Notification_User> notification_users;


    public Notification() {
    }

    public Notification(Long id, String writer, String title,String time, String content,String contentHtml, Date createTime, Date updateTime, String isDel) {
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.contentHtml=contentHtml;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDel = isDel;
        this.time=time;
    }
}

