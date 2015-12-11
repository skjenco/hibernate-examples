package com.iskj.app;

import com.iskj.model.Address;
import com.iskj.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Simple standalone JPA app.
 * Will load the user inserted by the script import-users.sql
 *
 * @author Geoffroy Warin (https://github.com/geowarin)
 */
public class App {

    private static Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("persistence");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User found = entityManager.find(User.class, 2L);
        log.info(found.getName());
        log.info("found=" + found);
        log.info("the person address = " + found.getAddress().getStreet());

        Address address = entityManager.find(Address.class, 1L);

        log.info(address.getStreet());

    }
}
