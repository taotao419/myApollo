package com.example.my.apollo.biz.service;

import com.example.my.apollo.biz.AbstractIntegrationTest;
import com.example.my.apollo.biz.entity.Audit.OP;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 * 因为在AbstractIntegrationTest中 设置了@Transactional,默认在@Test之后回滚
 * 现在在方法上面 强行写上 @Rollback(false) 关闭了测试回滚功能.
 */
public class AuditServiceTest extends AbstractIntegrationTest {

    @Autowired
    private AuditService auditService;
    
    @Test
    @Rollback(false)
    public void testAudit(){
        String entityName="demo.example.my.unittest.Entity";
        Long entityId=  1002L;
        OP op=OP.INSERT;
        String owner="unitTestOwner";

        auditService.audit(entityName, entityId, op, owner);
    }
}