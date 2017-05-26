package cn.edu.shou.missive.web;

import cn.edu.shou.missive.domain.*;
import cn.edu.shou.missive.service.*;
import cn.edu.shou.missive.service.bpm.HistoryProcessInstanceDiagramCmd;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.ProcessEngineImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Created by sqhe on 14-7-7.
 */
@RestController
@RequestMapping(value="/test")
public class TestController {

    @Autowired
    private UserRepository userr;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private TesterRepository tester;
    @Autowired
    private GroupRepository groupDAO;
    @Autowired
    private MissiveRepository missiveDAO;
    @Autowired
    private MissiveVersionRepository mvDAO;
    @Autowired
    private SendMsgService sendMsgR;
    @Autowired
    private SimpleMailSender simMailSend;
    @Autowired
    private FaxCableRepository faxR;
    @Autowired
    private MissiveRepository missR;
    @Autowired MetroCenServiceTypeRepository serviceTypeRepository;
    @Autowired
    private HistoryService historyService;

    @Value("${spring.main.uploaddir}")
    String fileUploadDir;



    /**
     * 流程跟踪
     *
     * @throws Exception
     */
    @RequestMapping("workspace-graphHistoryProcessInstance/{executionId}")
    public void graphHistoryProcessInstance(
            @PathVariable("executionId") String processInstanceId,
            HttpServletResponse response) throws Exception {
        Command<InputStream> cmd = new HistoryProcessInstanceDiagramCmd(
                processInstanceId);

        InputStream is = processEngine.getManagementService().executeCommand(
                cmd);
        response.setContentType("image/png");

        int len = 0;
        byte[] b = new byte[1024];

        while ((len = is.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    /**
     * 读取带跟踪的图片
     */
    @RequestMapping(value = "/process/trace/auto/{executionId}")
    public void readResource(@PathVariable("executionId") String executionId, HttpServletResponse response)
            throws Exception {
        String processDefId = "";
        if (runtimeService.createProcessInstanceQuery().processInstanceId(executionId).count()>0)
        { ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(executionId).singleResult();
            processDefId = processInstance.getProcessDefinitionId();
        }
        else{
            HistoricProcessInstance hProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(executionId).singleResult();
            processDefId = hProcessInstance.getProcessDefinitionId();
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefId);
        List<String> activeActivityIds = new ArrayList<String>();
        if (runtimeService.createProcessInstanceQuery().processInstanceId(executionId).count()>0)
            activeActivityIds=runtimeService.getActiveActivityIds(executionId);
        // 不使用spring请使用下面的两行代码
        ProcessEngineImpl defaultProcessEngine = (ProcessEngineImpl) ProcessEngines.getDefaultProcessEngine();
        //ProcessEngineConfiguration conf = Context.getProcessEngineConfiguration();
        Context.setProcessEngineConfiguration(defaultProcessEngine.getProcessEngineConfiguration());


        // 使用spring注入引擎请使用下面的这行代码
        //Context.setProcessEngineConfiguration((ProcessEngineConfigurationImpl)processEngine.getProcessEngineConfiguration());

        //InputStream imageStream = new MyProcessDiagramGenerator().generateDiagram(bpmnModel, "png", activeActivityIds);

        //List<String> activeIds = this.runtimeService.getActiveActivityIds(pi.getId());
        InputStream imageStream = new DefaultProcessDiagramGenerator().generateDiagram(
                bpmnModel, "png",
                activeActivityIds, Collections.<String>emptyList(),
                processEngine.getProcessEngineConfiguration().getActivityFontName(),
                processEngine.getProcessEngineConfiguration().getLabelFontName(),
                null, 1.0);


        // 输出资源内容到相应对象

        OutputStream out = null;
        try{
            response.setContentType("image/png;charset=utf-8");

            out = response.getOutputStream();

            out.write(getImgByte(imageStream));
            out.flush();
        }
        catch (Exception ex){
            //logger.error("readResource exception:",ex);


        }
        finally {
            try{
                out.close();
            }
            catch (Exception e){
                //logger.error("readResource-close exception:",e);

            }
        }
        /*byte[] b = new byte[1024];
        int len;
        while ((len = imageStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }*/

    }
    public byte[] getImgByte(InputStream is)throws IOException{
        ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
        int b;
        while((b=is.read())!=-1){
            bytestream.write(b);
        }
        byte[] bs = bytestream.toByteArray();
        bytestream.close();
        return  bs;
    }






    @RequestMapping(value="/engine", method= RequestMethod.GET)
    public @ResponseBody String getUser(HttpServletResponse response) {
        response.setContentType("text/html; charset=UTF-8");
        Map<String,Object> vars = new HashMap<String, Object>();
        List<String> users = new ArrayList<String>();
        users.add("kermit");
        users.add("sqhe18");
        vars.put("counterSigneeList",users);
        identityService.setAuthenticatedUserId("sqhe18");
        runtimeService.startProcessInstanceById("counterSignId:1:1394",vars);

        return "456";
    }

    @RequestMapping(value="/user/test/{userid}", method= RequestMethod.GET)
    public List<Map<String, Object>> getUser2(@PathVariable Long userid) {
        return jdbc.queryForList("select * from users where id=?",new Object[]{userid});
    }

    @RequestMapping(value="/{user}/missive", method=RequestMethod.GET)
    List<Missive> getUserCustomers(@PathVariable Long user) {
        List<Missive> list = new ArrayList<Missive>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object myUser = (auth != null) ? auth.getPrincipal() :  null;

        if (myUser instanceof User) {
            User user2 = (User) myUser;
            //get details from model object
        }

        return list;
    }

    @RequestMapping(value="/{user}", method=RequestMethod.DELETE)
    public String deleteUser(@PathVariable Long user) {
        return null;
    }

    @RequestMapping(value="/user/{user}", method=RequestMethod.GET)
    public List<User> testGetUser(@PathVariable Long user)
    {
        return this.userr.findAll();
    }

    @RequestMapping(value="/group", method=RequestMethod.GET)
    public List<Group> testGetGroupAll()
    {
        List<Group> tempuser = this.groupDAO.findAll();
        return tempuser;
    }

    @RequestMapping(value="/group/{group}", method=RequestMethod.GET)
      public Group testGetGroup(@PathVariable Long group)
    {
        Group tempuser = this.groupDAO.findOne(group);
        return tempuser;
    }


    //测试发送短信
    @RequestMapping(value = "/sendMSG",method = RequestMethod.GET)
    public String sendMSG() throws IOException {
        return sendMsgR.sendMsg("15692166810","您有一份公文需要处理，请注意及时处理。");
    }
    //测试发送邮件
    @RequestMapping(value = "/sendMail",method = RequestMethod.GET)
    public boolean sendMail(){

        return simMailSend.HTMLMailSend("关于*****的通知","http://localhost:8888/FaxCable/57506/57501",new String[]{"xlzheng@shou.edu.cn"});
    }


    @RequestMapping(value="/create", method=RequestMethod.POST)
    public List<Missive> createUser(@RequestBody @Valid final List<Missive> missive) {
        //todo access database
        return missive;
    }

    @RequestMapping(value="/users", method=RequestMethod.GET)
    public List<Map<String,Object>> jdbctest2() {
        //todo access database
        List<Map<String,Object>> temp =  this.jdbc.queryForList("select * from users");
        List<Map<String,Object>> temp2 = this.jdbc.queryForList("select * from groups where id="+temp.get(0).get("group_id"));
        temp.get(0).put("group",temp2.get(0));
        return  temp;

    }

    @RequestMapping(value="/audit", method=RequestMethod.GET)
    public String auditTest(@AuthenticationPrincipal User user) {
        //todo access database
        Tester t = new Tester();
        t.name = "sqhe18";
        this.tester.save(t);
        return  "创建成功！！";

    }
    @RequestMapping(value="/updateaudit", method=RequestMethod.GET)
    public String updateAuditTest() {
        //todo access database

        Tester t = this.tester.findOne(1L);
        //t.setId(1L);
        t.name = "sqhe19";
        this.tester.save(t);

        return  "update ";

    }

    @RequestMapping(value="/missive", method=RequestMethod.GET)
    public Missive missiveTest() {
       Missive m = this.missiveDAO.findOne(1L);
        List<MissiveVersion> mv =  mvDAO.findAll();
        return m;
    }


    @Autowired
    private ActivitiService actService;

    @RequestMapping(value="/testact", method=RequestMethod.GET)
    public String activitiServiceTest() {
        String test =this.actService.getProcessFirstAssignee("PublishMissiveId:1:1417");
        return (test);
    }


    @RequestMapping(value="/teststartprocess", method=RequestMethod.GET)
    public String activitiServiceTest2() {
        String test =this.actService.startProcess("PublishMissiveId:1:1389",123L,this.userr.findOne(1L));
        return (test);
    }

    @RequestMapping(value="/testflowcondition", method=RequestMethod.GET)
    public List<Map<String,? extends Object>> activitiServiceTest3() {
        List<Map<String,? extends Object>> test =this.actService.getNextTaskInfo("5001",10004);
        return test;
    }


    @RequestMapping(value="/select", method=RequestMethod.POST)
    public String selectTest(@RequestParam("test1") ArrayList<String> test1) {
        String temp = Joiner.on(",").join(test1);
        return temp;
    }

    @RequestMapping(value="/pdfmerge", method=RequestMethod.GET)
    public String mergePdfTest(@RequestParam("test1") ArrayList<String> test1) {
        PDFMergerUtility ut = new PDFMergerUtility();
        ut.addSource(fileUploadDir+"1.pdf");
        ut.addSource(fileUploadDir+"2.pdf");
        ut.addSource(fileUploadDir+"3.pdf");
        ut.setDestinationFileName(fileUploadDir+"output.pdf");
        try {
            ut.mergeDocuments();
            return "success!";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }
        return "failed!";
    }

    @RequestMapping(value="/shell", method=RequestMethod.GET)
    public String shellTest() {
        //ExecuteShellComand obj = new ExecuteShellComand();

        String domainName = "google.com";

        //in mac oxs
        String command = "ping -c 3 " + domainName;

        command = "wkhtmltopdf www.baidu.com /Users/sqhe/Desktop/baidu.pdf";
        //in windows
        //String command = "ping -n 3 " + domainName;

        String output = this.executeCommand(command);
        return output;
    }



    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
//            p.waitFor();
//            BufferedReader reader =
//                    new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//            String line = "";
//            while ((line = reader.readLine())!= null) {
//                output.append(line + "\n");
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    @Autowired
    private PdfService pdfService;

    @RequestMapping(value="/overlayerpdf", method=RequestMethod.GET)
      public String overlayerPdfTest() {

        return this.pdfService.overLayerRedTitle();
    }


    @RequestMapping(value="/processDef", method=RequestMethod.GET)
    @ResponseBody
    public String getAllProcessDef() {

        String result = "";
        List<ProcessDefinition> list = this.actService.getAllLastestProcessDefinition();
        for (ProcessDefinition pd : list)
        {
            result += pd.getId() + " | ";

        }
        return result;
    }


    @RequestMapping(value="/processDef2", method=RequestMethod.GET)
    @ResponseBody
    public String getAllProcessDef2() {

        String result = "";
        String searchValue = "签报";

        String requestUrl="http://192.168.182.33:9200/search/process/_search?from="+0+"&size="+2;

        Map<String,Object> request = new HashMap<String, Object>();

        Map<String,Object> requestInner = new HashMap<String, Object>();



        requestInner.put("fields", ImmutableList.of("missiveTitle","author","missiveType","missiveContent"));
        requestInner.put("query",searchValue);
        request.put("query", ImmutableMap.of("multi_match", requestInner));
        //request.put("highlight",ImmutableMap.of("match",ImmutableMap.of("fields",ImmutableMap.of("missiveTitle",ImmutableMap.of()))));

        RestTemplate template = new RestTemplate();
        ResponseEntity<String> temp = template.postForEntity(requestUrl,request,String.class);

        String restCall = temp.getBody();
        return restCall;
    }


    @RequestMapping(value="/history", method=RequestMethod.GET)

    public List<HistoricActivityInstance> getAllProcessDef222() {

        return this.actService.getHistoryByProcessId(5001L);

    }

    //测试获取所有任务
    @RequestMapping(value = "/getTaskList",method = RequestMethod.GET)
    public void getTaskList() throws IOException {

        actService.dealCurrentAllTasksList();
    }
    //测试公文编号是否存在
    @RequestMapping(value = "/missiveNumExit/{missiveNum}",method = RequestMethod.GET)
    public boolean getMissiveByNum(@PathVariable String missiveNum){
        List<Missive> mis=missR.getMissiveByMissiveNum(missiveNum);
        if (mis!=null){
            return false;//该公文号已经存在
        }else {
            return true;//该公文号能使用
        }
    }
    //测试jap分页功能
    @RequestMapping(value = "/pageenable")
    public void enablePage(){
        int pageNum=1;
        int pageSize=30;
        Page<MetroCenServiceType> serviceTypes=serviceTypeRepository.findAll(new PageRequest((pageNum-1)*pageSize,pageNum*pageSize));
        List<MetroCenServiceType> serviceTypes1=serviceTypes.getContent();
        int totalPage=serviceTypes.getTotalPages();//总页码


    }





}