package com.mall.controller.backend;

import com.google.common.collect.Maps;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Product;
import com.mall.pojo.User;
import com.mall.service.IFileService;
import com.mall.service.IProductService;
import com.mall.service.IUserService;
import com.mall.util.PropertiesUtil;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wxhong on 2018/5/3.
 */
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * 保存或更新产品
     * @param session
     * @param product
     * @return
     */
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.SaveOrUpdateProduct(product);
    }

    /**
     * 设置产品状态
     * @param session
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.setSaleStatus(productId, status);
    }

    /**
     * 获取产品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.manageProductDetail(productId);
    }

    /**
     * 查询所有产品信息
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.getProductList(pageNum, pageSize);
    }

    /**
     * 通过条件查找商品
     * @param session
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1")int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.searchProduct(productName,productId,pageNum,pageSize);
    }

    /**
     * 上传文件接口
     * @param file
     * @param request
     * @return
     */
    @RequestMapping("upload.do")
    @ResponseBody
    public  ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        String path = request.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        Map fileMap = new HashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);
    }

    /**
     * 上传富文本
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public  Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            resultMap.put("error", false);
            resultMap.put("msg", "请登录管理员");
            return resultMap;
        }
        //校验下是否是管理员
        ServerResponse<String> res = iUserService.checkAdminRole(user);
        if(!res.isSuccess()){
            resultMap.put("error", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }

        String path = request.getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        if(StringUtils.isBlank(targetFileName)){
            resultMap.put("error", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }

        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);

        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");//simditor插件返回要求

        return resultMap;
    }
}
