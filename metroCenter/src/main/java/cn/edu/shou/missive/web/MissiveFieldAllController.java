package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.MissiveFieldAllRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by jiliwei on 2014/7/20.
 */

@RestController
@RequestMapping(value="/missiveFieldAll")
public class MissiveFieldAllController {

    @Autowired
    private MissiveFieldAllRepository mfar;

    @RequestMapping(value="",method = RequestMethod.GET)
    @ResponseBody
    public void getMissiveFields(HttpServletResponse response) {


        List<MissiveFieldAll> mfa = mfar.findAll();

        String MissiveFieldAllList = "[";

        for (MissiveFieldAll mm : mfa) {

            String fieldName = mm.getFieldName();
            String name=mm.getProcessType().getName();

            MissiveFieldAllList += "{fieldName:'" + fieldName + "',name:'"+name+"'},";
        }

        MissiveFieldAllList = MissiveFieldAllList.substring(0, MissiveFieldAllList.length() - 1) + "]";

        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write(MissiveFieldAllList);
        } catch (Exception ee) {
            // throw ee;
            ee.printStackTrace();
        }

    }

  }
