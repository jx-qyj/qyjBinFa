package com.qyj;

import com.qyj.ExcelTest.TestObj;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

/**
 * Created by QYJ on 2017/12/20.
 */
@Slf4j
public class TestExcel {

    static BlockingQueue<List<TestObj>> listLinkedBlockingQueue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws FileNotFoundException {
        DbUtil dbUtil = new DbUtil();
        final ExcelTest excelTest = new ExcelTest("D:\\test.xlsx", dbUtil.dataNum);
        excelTest.initWriteExcel();
        //启动读取线程
        Thread thread = new Thread(() -> {
            dbUtil.CursorHandle(x -> {
                try {
                    listLinkedBlockingQueue.offer(x, 3, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            });
        });
        thread.start();
        //启动N个线程去写入Excel
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                while (true) {
                    List<TestObj> peek = null;
                    try {
                        peek = listLinkedBlockingQueue.poll(3, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        if(excelTest.isWriteDone()){
                            //如果excel全部写入完毕就结束
                            break;
                        }
                        log.error(e.getMessage(), e);
                        continue;
                    }
                    if(peek==null){
                        if(excelTest.isWriteDone()){
                            //如果excel全部写入完毕就结束
                            break;
                        }
                    }
                    log.info("写入excel 数量：" + peek.size());
                    excelTest.writeData(peek.iterator());
                }
            }).start();
        }

        //启动一个周期性线程去检测是否已经完成写入操作
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean writeDone = excelTest.isWriteDone();
                if (writeDone) {
                    try {
                        excelTest.writeLocalFile();
                        excelTest.close();
                        break;
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

        }).start();


    }


}
