package com.qyj;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by QYJ on 2017/12/20.
 */
@Slf4j
public abstract class ExcelBase<D> implements Closeable {

    //操作的文档
    private final Workbook workbook;
    //第一张工作表
    private final Sheet sheet;
    //输出的流
    private final OutputStream output;
    //数据总共行数
    private final int dataNum;
    //当前写入行数 用于验证是否完成
    private final AtomicInteger endWriteNum;
    //列
    private final String[] fields;

    protected ExcelBase(Workbook workbook, OutputStream outputStream, int dataNum,
        String[] fields) {
        this.workbook = workbook;
        this.sheet = workbook.createSheet();
        this.output = outputStream;  //输出的流
        this.dataNum = dataNum;
        this.endWriteNum = new AtomicInteger();
        this.fields = fields;
    }

    //获取首行单元格样式
    protected CellStyle getFirstRowStyle() {
        CellStyle ztStyle = workbook.createCellStyle();
        ztStyle.setAlignment(HorizontalAlignment.CENTER);
        ztStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        Font ztFont = workbook.createFont();
        ztFont.setFontHeightInPoints((short) 11);    // 将字体大小设置为18px
        ztFont.setFontName("微软雅黑");             // 将“华文行楷”字体应用到当前单元格上
        ztFont.setColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
        ztStyle.setFont(ztFont);                    // 将字体应用到样式上面
        return ztStyle;
    }

    //初始化写入Excel
    public void initWriteExcel() {
        Row firstRow = sheet.createRow(0);
        CellStyle firstRowStyle = getFirstRowStyle();
        for (int x = 0; x < fields.length; x++) {
            Cell cell = firstRow.createCell(x);
            cell.setCellValue(fields[x]);
            cell.setCellStyle(firstRowStyle);               // 样式应用到该单元格上
        }
    }

    //把余下的数据全部写入本地文件
    public void writeLocalFile() throws IOException {
        log.info("准备写本地文件");
        workbook.write(output);
    }

    protected abstract void setRowData(Row row, D d);

    //并发写入Excel数据
    public void writeData(Iterator<D> ls) {
        while (ls.hasNext()) {
            int rowNum = endWriteNum.incrementAndGet();
            Row row = sheet.createRow(rowNum);
            setRowData(row, ls.next());
        }
    }

    //是否写入完毕
    public boolean isWriteDone() {
        boolean b = dataNum == endWriteNum.get();
        log.info("dataNum:" + dataNum + "== endWriteNum:" + endWriteNum.get() + "  " + b);
        if (b) {
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        IOException e = null;
        try {
            workbook.close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            e = ex;
        }
        try {
            output.close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            e = ex;
        }
        if (e != null) {
            throw e;
        }
    }


}
