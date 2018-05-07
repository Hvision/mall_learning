package com.mall.service;

        import com.mall.common.ServerResponse;
        import com.mall.pojo.Category;
        import java.util.List;

/**
 * Created by wxhong on 2018/5/2.
 */
public interface ICategoryService{

    ServerResponse<String>  addCategory(String categoryName, Integer parentId);

    ServerResponse<String>  updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>>  getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>>  selectCategoryAndChildrenById(Integer categoryId);
}
