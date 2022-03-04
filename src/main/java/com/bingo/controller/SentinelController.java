package com.bingo.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.bingo.service.SelectService;
import com.bingo.service.spring.SelectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SentinelController {

    @Autowired
    SelectService selectService;

    /**
     * 限流实现方式一: 抛出异常的方式定义资源
     *
     * @return
     */
    @RequestMapping(value = "/query1")
    @ResponseBody
    public Map select1() {
        Entry entry = null;
        // 资源名
        String resourceName = SelectServiceImpl.KEY;
        try {
            // entry可以理解成入口登记
            entry = SphU.entry(resourceName);
            return selectService.select1();
        } catch (BlockException e) {
            e.printStackTrace();
            return new HashMap() {
                {
                    put("result1", "select1 is limit");
                }
            };
        } finally {
            // SphU.entry(xxx) 需要与 entry.exit() 成对出现,否则会导致调用链记录异常
            if (entry != null) {
                entry.exit();
            }
        }
    }

    @RequestMapping(value = "/query2")
    @ResponseBody
    public Map select2() {

        return selectService.select2();
    }

    @RequestMapping(value = "/query3")
    @ResponseBody
    @SentinelResource("testKey")
    public Map select3(@RequestBody String param) {
        JSONObject obj = JSONObject.parseObject(param);
        System.out.println(obj.toJSONString());
        return new HashMap() {
            {
                put("result3", "select3 is success");
            }
        };
    }

    @RequestMapping(value = "/query4")
    @ResponseBody
    public String select4(MultipartFile file, HttpServletRequest request, @RequestBody String param) {
        JSONObject obj = JSONObject.parseObject(param);
        System.out.println(obj.toJSONString());
        System.out.println(file.getOriginalFilename());
        return "success4";
    }

    @RequestMapping(value = "/query5")
    @ResponseBody
    public String select5(MultipartFile file, HttpServletRequest request, String param) throws IOException {
        if (file.isEmpty()) {
            System.out.println("文件为空");
            return "file is empty!!!";
        }
        //文件后缀
        String base_path = "D:\\qf_dev\\upload";

        String fileFolder = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        File folder = new File(base_path + File.separator + fileFolder);
        if (!folder.exists()) {
            // 创建文件夹
            folder.mkdirs();
        }
        File target = new File(folder, file.getOriginalFilename());
        file.transferTo(target);
        return "success5";
    }

}
