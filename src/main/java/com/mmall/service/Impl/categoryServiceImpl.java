package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.serverResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class categoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    private Logger logger = LoggerFactory.getLogger(categoryServiceImpl.class);

    @Override
    public serverResponse addCategory(String categoryName,Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return serverResponse.createByErrorMessage("category parameter error");
        }

        Category category = new Category();
        category .setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);

        int rowCount = categoryMapper.insert(category);
        if(rowCount > 0){
            return serverResponse.createBySuccess("Succeed to add category");
        }
        return serverResponse.createByErrorMessage("Failed to add category");
    }

    @Override
    public serverResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId ==null || StringUtils.isBlank(categoryName)){
            return serverResponse.createByErrorMessage("Wrong parameter");
        }
        Category category = new Category();
        category.setParentId(categoryId);
        category .setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount > 0){
            return serverResponse.createBySuccess("Succeed to update category");
        }
        return serverResponse.createByErrorMessage("Failed to update category");
    }

    @Override
    public serverResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("Failed to find the sublist of current list");
        }
        return serverResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id和孩子节点的id
     * @param categoryId
     * @return
     */
    @Override
    public serverResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId !=null){
            for(Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return serverResponse.createBySuccess(categoryIdList);
    }

    //递归方法
    //重写hashcode方法，equal方法.
    private Set<Category> findChildCategory(Set<Category>categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category !=null){
            categorySet.add(category);
        }
        //mybatis即使查不到，不会返回null对象
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for(Category item:categoryList){
            findChildCategory(categorySet,item.getId());
        }
        return categorySet;
    }
}
