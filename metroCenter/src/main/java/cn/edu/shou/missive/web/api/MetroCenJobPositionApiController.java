package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.service.MetroCenJobPositionRepository;
import cn.edu.shou.missive.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by XQQ on 2014/8/5.
 */



@RestController
@RequestMapping(value = "/api/jobPosition")
    public class MetroCenJobPositionApiController {
    @Autowired
    private MetroCenJobPositionRepository jobPositionRepository;

    @Autowired
    UserRepository user;




    }

