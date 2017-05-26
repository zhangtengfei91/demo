package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.ProcessType;
import cn.edu.shou.missive.service.ProcessTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/18.
 */
@RestController
@RequestMapping(value="/processType")
public class ProcessTypeController {
    @Autowired
    private ProcessTypeRepository pr;

    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response){


        List<ProcessType> ptype=pr.findAll();

        String ProcessTypeList="[";

        for(ProcessType pp:ptype){

            String name=pp.getName();

            ProcessTypeList+="{name:'"+name+"'},";
        }

        ProcessTypeList= ProcessTypeList.substring(0,ProcessTypeList.length()-1)+"]";

        response.setCharacterEncoding("UTF-8");

        try{
            response.getWriter().write(ProcessTypeList);
        }catch (Exception ee){
            // throw ee;
            ee.printStackTrace();
        }

    }

}
