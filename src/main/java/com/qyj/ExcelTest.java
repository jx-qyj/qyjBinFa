package com.qyj;

import com.qyj.ExcelTest.TestObj;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by QYJ on 2017/12/20.
 */
@Slf4j
public class ExcelTest extends ExcelBase<TestObj> {

    static final String[] fileds = new String[]{
        "姓名",
        "数量"
    };

    public ExcelTest(String fileName, int dataNum) throws FileNotFoundException {
        super(new SXSSFWorkbook(10000), new FileOutputStream(new File(fileName)), dataNum, fileds);
    }


    @Override
    protected void setRowData(Row row, TestObj d) {
        row.createCell(0).setCellValue(d.getName());
        row.createCell(1).setCellValue(d.getNum());
    }

    @Data
    public static class TestObj {

        private String name;
        private int num;

    }
}
