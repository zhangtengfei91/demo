package cn.edu.shou.missive.web;

import cn.edu.shou.missive.service.TaskDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by sqhe on 14-8-14.
 */
@RequestMapping(value = "/process")
@Controller
public class ProcessController {

    @Autowired
    TaskDAO taskDao;


    @RequestMapping(value = "/{processInstanceId}/{taskId}")
    public String getTask(Long processInstanceId,Long taskId)
    {

        List<String> editableFields = taskDao.getCurrentEditableFieldsByTaskId(taskId);
        String taskName = taskDao.getTaskNameById(taskId);


        return null;
    }


    @RequestMapping(value = "/new")
    public String startProcess()
    {

        return null;
    }




}
