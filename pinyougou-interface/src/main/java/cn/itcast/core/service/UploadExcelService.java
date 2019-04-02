package cn.itcast.core.service;

import java.util.List;

public interface UploadExcelService {
    void addExcelToTable(String name, List<List<Object>> list);
}
