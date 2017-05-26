package cn.edu.shou.missive.web.api;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.DeployRepository;
import cn.edu.shou.missive.service.MissiveTypeRepository;
import cn.edu.shou.missive.service.SecretLevelRepository;
import cn.edu.shou.missive.service.UrgentLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XQQ on 2014/9/7.
 */
@RestController
@RequestMapping(value = "/api/config")
public class ConfigApiController {
    @Autowired
    MissiveTypeRepository mt;
    @Autowired
    SecretLevelRepository sl;
    @Autowired
    UrgentLevelRepository ul;
    @Autowired
    DeployRepository de;

    //read获取所有公文类型
    @RequestMapping(value = "/missiveType",method = RequestMethod.GET)
    public List<MissiveType> getMissiveType(){
        return  mt.findAll();

    }
    //read获取所有密级
    @RequestMapping(value = "/secretLevel",method = RequestMethod.GET)
    public List<SecretLevel> getSecretLevel(){
        return sl.findAll();
    }

    //read获取所有紧急程度
    @RequestMapping(value = "/urgentLevel",method = RequestMethod.GET)
    public List<UrgentLevel> getUrgentLevel(){
        return ul.findAll();
    }
    //read获取所有 设置
    @RequestMapping(value = "/deploy",method = RequestMethod.GET)
    public List<Deploy> getDeploy(){
        return de.findAll();
    }



    //创建公文类型
    @RequestMapping(value = "/createMissiveType",method = RequestMethod.GET)
    public List<MissiveType> createMissiveType(@RequestParam String typeName){
        MissiveType mm=new MissiveType();
        mm.setTypeName(typeName);
        //mm.setDeleted(false);

        mt.save(mm);
        List<MissiveType> list=new ArrayList<MissiveType>();
        list.add(mm);
        return list;
    }
    //创建密级
    @RequestMapping(value = "/createSecretLevel",method =RequestMethod.GET)
    public List<SecretLevel> createSecretLevel(@RequestParam String secretLevelName){
        SecretLevel ss=new SecretLevel();
        ss.setSecretLevelName(secretLevelName);
        sl.save(ss);
        List<SecretLevel> list=new ArrayList<SecretLevel>();
        list.add(ss);
        return list;

    }

    //创建紧急程度
    @RequestMapping(value = "/createUrgentLevel",method =RequestMethod.GET)
    public List<UrgentLevel> createUrgentLevel(@RequestParam String value){
        UrgentLevel uu=new UrgentLevel();
        uu.setValue(value);
        ul.save(uu);
        List<UrgentLevel> list=new ArrayList<UrgentLevel>();
        list.add(uu);
        return list;

    }
    //创建 设置
    @RequestMapping(value = "/createDeploy",method =RequestMethod.GET)
    public List<Deploy> createDeploy(@RequestParam String name,@RequestParam String value){
        Deploy dd=new Deploy();
        dd.setName(name);
        dd.setValue(value);
        de.save(dd);
        List<Deploy> list=new ArrayList<Deploy>();
        list.add(dd);
        return list;

    }
    //更新公文类型
    @RequestMapping(value = "updateMissiveType")
    public List<MissiveType> updateMissiveType(@RequestParam long id,@RequestParam String typeName){
        MissiveType mm=mt.findOne(id);
        mm.setTypeName(typeName);
        //mm.setDeleted(false);
        MissiveType updateMissiveType=mt.save(mm);
        List<MissiveType> missiveTypeList=new ArrayList<MissiveType>();
        missiveTypeList.add(updateMissiveType);
        return missiveTypeList;
    }
    //更新密级
    @RequestMapping(value = "updateSecretLevel")
    public List<SecretLevel> updateSecretLevel(@RequestParam long id,@RequestParam String secretLevelName){
        SecretLevel ss=sl.findOne(id);
        ss.setSecretLevelName(secretLevelName);
        SecretLevel updateSecretLevel=sl.save(ss);
        List<SecretLevel> SecretLevelList=new ArrayList<SecretLevel>();
        SecretLevelList.add(updateSecretLevel);
        return SecretLevelList;
    }
    //更新紧急程度
    @RequestMapping(value = "updateUrgentLevel")
    public List<UrgentLevel> updateUrgentLevel(@RequestParam long id,@RequestParam String value){
        UrgentLevel uu=ul.findOne(id);
        uu.setValue(value);
        UrgentLevel updateUrgentLevel=ul.save(uu);
        List<UrgentLevel> UrgentLevelList=new ArrayList<UrgentLevel>();
        UrgentLevelList.add(updateUrgentLevel);
        return UrgentLevelList;
    }
    //更新设置
    @RequestMapping(value = "updateDeploy")
    public List<Deploy> updateDeploy(@RequestParam long id,@RequestParam String name,@RequestParam String value){
        Deploy dd=de.findOne(id);
        dd.setName(name);
        dd.setValue(value);
        Deploy updateDeploy=de.save(dd);
        List<Deploy> DeployList=new ArrayList<Deploy>();
        DeployList.add(updateDeploy);
        return DeployList;
    }

    //删除公文类型
    @RequestMapping(value = "deleteMissiveType")
    public List<MissiveType> deleteMissiveType(@RequestParam long id){
        MissiveType mm=mt.findOne(id);
        mt.delete(mm);
        List<MissiveType> missiveTypeList=new ArrayList<MissiveType>();
        missiveTypeList.add(mm);
        return missiveTypeList;

    }
    //删除密级
    @RequestMapping(value = "deleteSecretLevel")
    public List<SecretLevel> deleteSecretLevel(@RequestParam long id){
        SecretLevel ss=sl.findOne(id);
        sl.delete(ss);
        List<SecretLevel> SecretLevelList=new ArrayList<SecretLevel>();
        SecretLevelList.add(ss);
        return SecretLevelList;

    }

    //删除紧急程度
    @RequestMapping(value = "deleteUrgentLevel")
    public List<UrgentLevel> deleteUrgentLevel(@RequestParam long id){
        UrgentLevel uu=ul.findOne(id);
        ul.delete(uu);
        List<UrgentLevel> UrgentLevelList=new ArrayList<UrgentLevel>();
        UrgentLevelList.add(uu);
        return UrgentLevelList;

    }
    //删除设置
    @RequestMapping(value = "deleteDeploy")
    public List<Deploy> deleteDeploy(@RequestParam long id){
        Deploy dd=de.findOne(id);
        de.delete(dd);
        List<Deploy> DeployList=new ArrayList<Deploy>();
        DeployList.add(dd);
        return DeployList;

    }
}
