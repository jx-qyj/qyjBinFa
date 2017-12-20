package com.qyj;

import com.qyj.ExcelTest.TestObj;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/19.
 */
@Slf4j
public class DbUtil {

    public interface CursorFunc {

        void Run(List<TestObj> dateList);
    }

    //拟造的数据量
    int dataNum;
    //随机数
    static Random random = new Random();
    //数据缓存量
    int dataBufferNum = 2000;

    public DbUtil() {
        dataNum = random.nextInt(5000) + 100000;
    }

    public DbUtil(int dataBufferNum) {
        dataNum = random.nextInt(5000) + 100000;
        dataBufferNum = dataBufferNum;
    }

    public void CursorHandle(CursorFunc cursorFunc) {
        int index = 0;
        List<TestObj> bufferData = new ArrayList<>();
        for (int i = 0; i < dataNum; i++) {
            index++;
            TestObj testObj = new TestObj();
            testObj.setName(String.valueOf((char) (random.nextInt(10000) + 0xA440)));
            testObj.setNum(random.nextInt(1000) + 1000);
            bufferData.add(testObj);
            if (index >= dataBufferNum) {
                log.info("获取到数据！数量：" + index);
                try {
                    //休眠一下 模拟数据查询效率
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cursorFunc.Run(bufferData);
                bufferData = new ArrayList<>();
                index = 0;
            }
        }
        //遗留数据
        if (index != 0) {
            log.info("遗留数据！数量：" + index);
            try {
                //休眠一下 模拟数据查询效率
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cursorFunc.Run(bufferData);
        }
    }
}
