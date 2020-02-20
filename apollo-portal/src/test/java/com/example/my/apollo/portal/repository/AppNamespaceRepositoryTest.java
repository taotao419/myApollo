package com.example.my.apollo.portal.repository;

import javax.validation.ConstraintViolationException;

import com.example.my.apollo.common.entity.AppNamespace;
import com.example.my.apollo.portal.StartupApp;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = StartupApp.class)
@RunWith(SpringRunner.class)
public class AppNamespaceRepositoryTest {
    @Autowired
    private AppNamespaceRepository appNamespaceRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void findByNameTest() {
        String namespaceName = "application";
        AppNamespace actual = appNamespaceRepository.findByName(namespaceName);
        Assert.assertNotNull(actual);
    }

    @Test
    public void saveTest() {
        AppNamespace entity = new AppNamespace();
        entity.setName("demo%");// wrong name
        entity.setAppId("SampleApp");
        entity.setFormat("properties");
        entity.setComment("demo comment");
        entity.setDataChangeCreatedBy("default");
        //抛错语句 写在执行方法行的上面
        thrown.expect(ConstraintViolationException.class);
        AppNamespace appNamespace = appNamespaceRepository.save(entity);
    }
}