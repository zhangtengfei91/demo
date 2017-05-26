package cn.edu.shou.missive.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/18.
 */
@Entity
public class TaskName {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskName(){

    }

    public TaskName(String taskName){

        this.taskName=taskName;

    }



    @Getter
    @Setter
    @ManyToOne(cascade=CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name="processTypeId")
    private ProcessType processType;

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }

    @Getter
    @Setter
    @OneToMany(cascade=CascadeType.ALL,fetch = FetchType.EAGER,mappedBy = "taskName")
    private List<MissiveField> missiveFields;


}
