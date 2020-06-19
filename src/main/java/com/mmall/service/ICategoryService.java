package com.mmall.service;

import com.mmall.common.serverResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {

    serverResponse addCategory(String categoryName, Integer parentId);
    serverResponse updateCategoryName(Integer categoryId,String categoryName);
    serverResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);
    serverResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
