package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.PropertiesUtil;
import com.mmall.utility.RedisPoolUtil;
import com.mmall.utility.jsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manage/product")
public class productManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    public serverResponse productSave(HttpServletRequest request, Product product) {
//      User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.saveOrUpdateProduct(product);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public serverResponse setSaleStatus(HttpServletRequest request, Integer productId, Integer status) {
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    @RequestMapping("details.do")
    @ResponseBody
    public serverResponse getDetail(HttpServletRequest request, Integer productId) {
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    //动态分页：pageHelper 其内部通过AOP实现
    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse getList(HttpServletRequest request, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    //动态分页：pageHelper 其内部通过AOP实现
    @RequestMapping("search.do")
    @ResponseBody
    public serverResponse productSearch(HttpServletRequest request, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    @RequestMapping("upload.do")
    @ResponseBody
    //ftp服务器文件上传
    public serverResponse upload(@RequestParam(value = "upload _file", required = false) MultipartFile file, HttpServletRequest request) {
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr, User.class);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), "The user need to login");
        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            //需要创建文件夹
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return serverResponse.createBySuccess(fileMap);
        } else {
            return serverResponse.createByErrorMessage("No privilege to operate");
        }
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    //ftp服务器文件上传
    public Map richtext_img_upload(@RequestParam(value = "upload _file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            resultMap.put("success", false);
            resultMap.put("msg", "please login as admin");
            return resultMap;
        }
            String userJsonStr = RedisPoolUtil.get(loginToken);
            User user = jsonUtil.string2Obj(userJsonStr, User.class);
            if (user == null) {
                resultMap.put("success", false);
                resultMap.put("msg", "please login as admin");
                return resultMap;
            }
            //富文本对于返回值有要求，按照simditor要求格式返回
            //{
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
            if (iUserService.checkAdminRole(user).isSuccess()) {
                //需要创建文件夹
                String path = request.getSession().getServletContext().getRealPath("upload");
                String targetFileName = iFileService.upload(file, path);
                if (StringUtils.isBlank(targetFileName)) {
                    resultMap.put("success", false);
                    resultMap.put("msg", "Failed to upload file");
                    return resultMap;
                }
                String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

                resultMap.put("success", true);
                resultMap.put("msg", "Succeed to upload file");
                resultMap.put("file_path", url);
                response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
                return resultMap;
            } else {
                resultMap.put("success", false);
                resultMap.put("msg", "No privilege to operate");
                return resultMap;
            }
        }
}

