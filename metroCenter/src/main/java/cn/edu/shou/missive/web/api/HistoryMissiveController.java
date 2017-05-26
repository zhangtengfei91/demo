package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.service.ActivitiService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by jiliwei on 2014/9/5.
 */
@RestController
@RequestMapping(value = "/process/history")
public class HistoryMissiveController {

    @Autowired
    ActivitiService activitiService;


    @RequestMapping(value = "/instance",method = RequestMethod.POST)
    public List<HistoricActivityInstance> getHistoryInstance(@RequestParam("instanceId") Long instanceId){

        List<HistoricActivityInstance> historyResult=this.activitiService.getHistoryByProcessId(instanceId);

        return historyResult;
    }

    @RequestMapping(value = "/documentation",method = RequestMethod.POST)
    public String getProcessDocumentation(@RequestParam("instanceId")Long instanceId){

        String documentation=this.activitiService.getDocumentationByProcessId(instanceId);

        return documentation;
    }


}
