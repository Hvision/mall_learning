package com.mall.controller.backend;

import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.pojo.Category;
import com.mall.pojo.User;
import com.mall.service.ICategoryService;
import com.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpSession;
import java.util.List;


/**
 * Created by wxhong on 2018/5/2.
 */
@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加品类
     * @param session
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0")int parentId){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iCategoryService.addCategory(categoryName, parentId);
    }

    /**
     * 设置品类名字
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse<String> SetCategoryName(HttpSession session, Integer categoryId, String categoryName){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<String> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    /**
     * 获取品类下的子品类
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<?> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        //查询子节点的category信息，并且不递归，保持平级
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     *  获取品类的所有子品类
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse<List<Integer>> getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (null == user){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        //校验下是否是管理员
        ServerResponse<?> response = iUserService.checkAdminRole(user);
        if(!response.isSuccess()){
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        //查询子节点的category信息，并且递归
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }
}
