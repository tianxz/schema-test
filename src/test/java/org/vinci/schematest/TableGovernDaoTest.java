package org.vinci.schematest;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.vinci.schematest.module.lessschema.TableGovernDao;

/**
 * Created by Jao on 2017/9/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TableGovernDaoTest {
    final Logger logger = LoggerFactory.getLogger(TableGovernDaoTest.class);
    final boolean isSingleSchemaTest = true;

    @Autowired
    private TableGovernDao tableGovernDao;

    @Test
    public void testMockQueryTable() throws InterruptedException {
        final Long[] l = {0L};
        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    //测试内容
                    String tableCategory = StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1, 3)), 2, "0");
                    String tenant = StringUtils.leftPad(String.valueOf(RandomUtils.nextInt(1, 1000)), 4, "0");
                    String tableName;
                    String schemaName;
                    if (isSingleSchemaTest) {
                        tableName = String.format("TABLE_%s_%s", tableCategory, tenant);
                        schemaName = "SCHEMA_TEST";
                    } else {
                        tableName = String.format("TABLE_%s", tableCategory);
                        schemaName = String.format("SCHEMA_TEST_%s", tenant);
                    }

                    tableGovernDao.mockQueryTable(schemaName, tableName);
                    l[0]++;
                }
            });
            thread.start();
        }

        Thread timerThread = new Thread(() -> {
            long l1;
            long total = 0;
            long i = 0;
            while (++i < Long.MAX_VALUE) {
                l1 = l[0];
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                long currentCount = l[0] - l1;
                total += currentCount;
                logger.info("每秒访问表次数: " + currentCount + ", 平均每秒访问次数: " + (total / i));
            }
        });

        timerThread.start();

        Thread.sleep(1000 * 60);
    }

    @Test
    public void testCreateTable() {
        for (int i = 1; i <= 1000; i++) {
            for (int j = 1; j <= 3; j++) {
                String tableCategory = StringUtils.leftPad(String.valueOf(j), 2, "0");
                String tableTenant = StringUtils.leftPad(String.valueOf(i), 4, "0");
                String tableName = String.format("TABLE_%s_%s", tableCategory, tableTenant);
                tableGovernDao.createTable("SCHEMA_TEST", tableName);
            }
        }
    }

    @Test
    public void createSchema() {
        for (int i = 1; i <= 1000; i++) {
            String schemaTenant = StringUtils.leftPad(String.valueOf(i), 4, "0");
            String schemaName = String.format("SCHEMA_TEST_%s", schemaTenant);
            tableGovernDao.createSchema(schemaName);
            for (int j = 1; j <= 3; j++) {
                String tableCategory = StringUtils.leftPad(String.valueOf(j), 2, "0");
                String tableName = String.format("TABLE_%s", tableCategory);
                tableGovernDao.createTable(schemaName, tableName);
            }
        }
    }

    @Test
    public void deleteSchema() {
        for (int i = 1; i <= 1000; i++) {
            String schemaTenant = StringUtils.leftPad(String.valueOf(i), 4, "0");
            String schemaName = String.format("SCHEMA_TEST_%s", schemaTenant);
            tableGovernDao.deleteSchema(schemaName);
        }
    }
}
