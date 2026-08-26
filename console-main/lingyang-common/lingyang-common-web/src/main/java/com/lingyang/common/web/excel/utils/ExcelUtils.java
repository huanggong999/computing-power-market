package com.lingyang.common.web.excel.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.lingyang.common.core.enums.HttpContextType;
import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.ServletUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.web.excel.annotation.ExcelSelected;
import com.lingyang.common.web.excel.handler.SelectedSheetWriteHandler;
import com.lingyang.common.web.excel.model.ExcelSelectedResolve;
import com.lingyang.common.web.excel.model.ExcelSheetModel;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 导出工具
 * @Author: 王小龙
 * @Date: 2023/11/3 14:24
 */
@Slf4j
public class ExcelUtils {

    /**
     * 创建即将导出的sheet页（sheet页中含有带下拉框的列）
     * @param head 导出的表头信息和配置
     * @param sheetNo sheet索引
     * @param sheetName sheet名称
     * @param <T> 泛型
     * @return sheet页
     */
    public static <T> WriteSheet writeSelectedSheet(Class<T> head, Integer sheetNo, String sheetName) {
        Map<Integer, ExcelSelectedResolve> selectedMap = resolveSelectedAnnotation(head);

        return EasyExcel.writerSheet(sheetNo, sheetName)
                .head(head)
                .registerWriteHandler(new SelectedSheetWriteHandler(selectedMap))
                .build();
    }

    /**
     * 导出excel
     *
     * @param fileName   文件名称，默认uuid
     * @param sheetModel sheet列表
     * @param <T>        sheet数据类型
     */
    public static <T> void exportExcel(String fileName, List<ExcelSheetModel<T>> sheetModel) {
        exportExcel(fileName, sheetModel, null);
    }


    /**
     * 导出excel
     *
     * @param fileName   文件名称，默认uuid
     * @param sheetModel sheet列表
     * @param dataClass  数据模板类型
     * @param <T>        sheet数据类型
     */
    @SuppressWarnings(value = "unchecked")
    public static <T> void exportExcel(String fileName, List<ExcelSheetModel<T>> sheetModel, Class<T> dataClass) {
        if (ObjectUtils.isEmpty(sheetModel)) {
            Result.throwsError(HttpServiceException.class,"导出数据为空");
        }
        if (dataClass == null ) {
            dataClass = (Class<T>) sheetModel.get(0).getData().get(0).getClass();
        }
        HttpServletResponse response = ServletUtils.getResponse();
        try (
                ServletOutputStream outputStream = response.getOutputStream();
                ExcelWriter writer = EasyExcel.write(outputStream, dataClass).build()
        ) {
            fileName = StringUtils.isEmpty(fileName) ? IdUtils.simpleUUID() + ".xlsx" : fileName;
            if (!(fileName.endsWith(".xls") || fileName.endsWith(".xlsx"))) {
                fileName += ".xlsx";
            }
            response.setContentType(HttpContextType.OCTET_STREAM.getValue());
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            for (int i = 0; i < sheetModel.size(); i++) {
                ExcelSheetModel<T> excelSheetModel = sheetModel.get(i);
                if (ObjectUtils.isEmpty(excelSheetModel)) {
                    continue;
                }
                List<T> data = excelSheetModel.getData();
                if (ObjectUtils.isEmpty(data)) {
                    continue;
                }
                WriteSheet writeSheet = EasyExcel.writerSheet(i, excelSheetModel.getSheetName())
                        .registerWriteHandler(new SelectedSheetWriteHandler(resolveSelectedAnnotation(dataClass)))
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                        .build();
                writer.write(data, writeSheet);
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("导出excel异常：", e);
            Result.throwsError(HttpStatus.ERROR);
        }
    }

    public static <T> void readExcel(InputStream inputStream,
                                     int sheet,
                                     Class<T> modelClass,
                                     ReadListener<T> readListener) {
        ReadSheet readSheet = EasyExcel.readSheet(sheet).build();
        try (inputStream; ExcelReader excelReader = EasyExcel.read(inputStream, modelClass, readListener).build()) {
            excelReader.read(readSheet);
        } catch (IOException e) {
            log.error("文件读取异常: ", e);
            Result.throwsError(HttpStatus.ERROR);
        }
    }

    private static <T> Map<Integer, ExcelSelectedResolve> resolveSelectedAnnotation(Class<T> dataClass) {
        Map<Integer, ExcelSelectedResolve> selectedMap = new HashMap<>();
        Field[] fields = dataClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelSelected selected = field.getAnnotation(ExcelSelected.class);
            ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
            if (selected != null) {
                ExcelSelectedResolve resolve = new ExcelSelectedResolve();
                resolve.setColumnName(StringUtil.join(excelProperty.value(), "-"));
                String[] source = resolve.resolveSelectedSource(selected);
                if (ObjectUtils.isNotEmpty(source)) {
                    resolve.setSource(source);
                    resolve.setFirstRow(selected.fistRow());
                    resolve.setLastRow(selected.lasWor());
                    if (excelProperty.index() > 0) {
                        selectedMap.put(excelProperty.index(), resolve);
                    }else {
                        selectedMap.put(i, resolve);
                    }
                }
            }
        }
        return selectedMap;
    }
}
