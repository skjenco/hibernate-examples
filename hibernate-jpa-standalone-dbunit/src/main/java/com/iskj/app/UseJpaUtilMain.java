package com.iskj.app;

import com.iskj.model.User;
import com.iskj.utils.JpaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

/**
 * Created by skjenco on 12/10/15.
 */
public class UseJpaUtilMain {
    private static Logger log = LoggerFactory.getLogger(UseJpaUtilMain.class);
    public static void main(String[] args) {

        EntityManager entityManager = JpaUtil.getEntityManager();
        User found = entityManager.find(User.class, 2L);
        log.info(found.getName());
        log.info("found=" + found);
        log.info("the person address = " + found.getAddress().getStreet());


    }
}
