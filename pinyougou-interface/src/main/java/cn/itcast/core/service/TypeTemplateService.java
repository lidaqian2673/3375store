package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    void add(TypeTemplate typeTemplate);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate typeTemplate);

    List<Map> findBySpecList(Long id);

    Map findECharts();

    PageResult searchAudit(Integer page, Integer rows, TypeTemplate typeTemplate);

    Result updateStatus(Long[] ids, Integer status);
}
