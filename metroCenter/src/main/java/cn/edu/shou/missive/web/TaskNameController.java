package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.MetroCenProcessType;
import cn.edu.shou.missive.domain.MetroCenTaskName;
import cn.edu.shou.missive.service.MetroCenProcessTypeRepository;
import cn.edu.shou.missive.service.MetroCenTaskNameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/19.
 */
@RestController
@RequestMapping(value="/taskname")
public class TaskNameController {

    @Autowired
    private MetroCenTaskNameRepository tnr;
    @Autowired
    private MetroCenProcessTypeRepository processTypeRepository;

    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response){


        List<MetroCenTaskName> tn=tnr.findAll();

        String taskNameList="[";

        for(MetroCenTaskName tt:tn){

            String taskName=tt.getTaskName();
            long processTypeId=tt.getProcessTypeId();
            MetroCenProcessType processType=processTypeRepository.findOne(processTypeId);
            String name=processType.getTypeName();

            taskNameList+="{taskName:'"+taskName+"',name:'"+name+"'},";

        }

        taskNameList= taskNameList.substring(0,taskNameList.length()-1)+"]";

        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(taskNameList);
        }catch (Exception ee){
            // throw ee;
            ee.printStackTrace();
        }

    }

}
