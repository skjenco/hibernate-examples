package com.iskj.hibernate.jpa.tools;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jdbc.Work;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class JpaUtil {
    private static final Logger LOGGER = Logger.getLogger(JpaUtil.class);
    private static final String USER = "user";
    private static final String ENTITY_MANAGER = "entityManager";
    private static final String PERSISTENCE_UNIT = "persistence";
    private static ThreadLocal threadLocal = new ThreadLocal();
    private static EntityManagerFactory entityManagerFactory = null;
    private static boolean loading = false;
    private static final boolean TRACK_LEAKS = false;
    private static Map<String, Exception> trackLeaks = new HashMap();

    private JpaUtil() {
    }

    private static EntityManager existingEntityManager() {
        return (EntityManager)getCurrentMap().get("entityManager");
    }

    private static void setEntityManager(EntityManager entityManager) {
        getCurrentMap().put("entityManager", entityManager);
    }

    public static EntityManagerFactory resetEntityManagerFactory(Map properties) {
        Class var1 = JpaUtil.class;
        synchronized(JpaUtil.class) {
            clearEntityManager(existingEntityManager());
            if(entityManagerFactory != null) {
                entityManagerFactory.close();
            }

            loading = true;
            entityManagerFactory = Persistence.createEntityManagerFactory("persistence", properties);
            loading = false;
            return entityManagerFactory;
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        Class var0 = JpaUtil.class;
        synchronized(JpaUtil.class) {
            if(entityManagerFactory == null) {
                entityManagerFactory = resetEntityManagerFactory(Collections.emptyMap());
            }
        }

        return entityManagerFactory;
    }

    private static Map<String, Object> getCurrentMap() {
        Object current = (Map)threadLocal.get();
        if(current == null) {
            current = new HashMap();
            threadLocal.set(current);
        }

        return (Map)current;
    }

    public static void setExternalEntityManager(EntityManager entityManager) {
        setEntityManager(entityManager);
    }

    private static void clearEntityManager(EntityManager entityManager) {
        if(entityManager != null) {
            LOGGER.debug("Closing entity manager ************");
            //trackLeaksClose();
            entityManager.close();
            setEntityManager((EntityManager)null);
        }

    }



    public static String getUser() {
        return (String)getCurrentMap().get("user");
    }

    public static void setUser(String user) {
        getCurrentMap().put("user", user);
    }

    public static Session getSession() {
        return (Session)getEntityManager().unwrap(Session.class);
    }

    public static EntityManager getEntityManager() throws PersistenceException {
        EntityManager entityManager = existingEntityManager();
        if(entityManager == null) {
            LOGGER.debug("New entity manager ************");
            entityManager = getEntityManagerFactory().createEntityManager();
            setEntityManager(entityManager);
            //trackLeaksOpen();
        }

        return entityManager;
    }

    public static EntityManager getSelfManagedEntityManager() throws PersistenceException {
        return getEntityManagerFactory().createEntityManager();
    }

    public static void verifyNoEntityManager(String persistenceUnit) {
        if(existingEntityManager() != null) {
            LOGGER.error("Entity manager should not exist.  Persistence: " + persistenceUnit + ", Thread: " + Thread.currentThread().getName());
        }

    }

    public static void ensureInitialized() {
        getEntityManagerFactory();
    }


    public static void closeEntityManager() {
        rollback();
        EntityManager entityManager = existingEntityManager();
        clearEntityManager(entityManager);
    }

    public static void rollback() throws HibernateException {
        EntityManager entityManager = existingEntityManager();
        if(entityManager != null) {
            try {
                if(entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            } catch (PersistenceException var2) {
                clearEntityManager(entityManager);
                throw var2;
            }
        }

    }

    public static void commit() throws HibernateException {
        EntityManager entityManager = existingEntityManager();
        if(entityManager != null) {
            try {
                entityManager.getTransaction().begin();
                entityManager.flush();
                entityManager.getTransaction().commit();
            } catch (PersistenceException var2) {
                closeEntityManager();
                throw var2;
            }
        }

    }

    public static void save(Object... persistables) {
        save((Collection)Arrays.asList(persistables));
    }

    public static void save(Collection<? extends Object> persistables) {
        saveWithoutCommit(persistables);
        commit();
    }

    public static void saveWithoutCommit(Object... persistables) {
        saveWithoutCommit((Collection)Arrays.asList(persistables));
    }

    public static void saveWithoutCommit(Collection<? extends Object> persistables) {
        try {
            Iterator e = persistables.iterator();

            while(e.hasNext()) {
                Object persistable = (Object)e.next();
                getSession().saveOrUpdate(persistable);
            }

        } catch (PersistenceException var3) {
            closeEntityManager();
            throw var3;
        }
    }

    public static void remove(Object... persistables) {
        remove((Collection)Arrays.asList(persistables));
    }

    public static void removeWithoutCommit(Collection<? extends Object> persistables) {
        Iterator var1 = persistables.iterator();

        while(var1.hasNext()) {
            Object persistable = (Object)var1.next();
            EntityManager entityManager = getEntityManager();

            try {
                if(getSession().contains(persistable)) {
                    entityManager.remove(persistable);
                } else {
                    //entityManager.remove(getSession().createCriteria(persistable.getClass()).add(Restrictions.eq("id", persistable.getId())).uniqueResult());
                }
            } catch (PersistenceException var5) {
                closeEntityManager();
                throw var5;
            }
        }

    }

    public static void remove(Collection<? extends Object> persistables) {
        removeWithoutCommit(persistables);
        commit();
    }

    public static boolean isLoading() {
        return loading;
    }

    public static <T extends Object> T findById(Class<T> clazz, Serializable id) throws ObjectNotFoundException {
        //return (Object)getEntityManager().find(clazz, id);
        return null;
    }

    public static <T extends Object> T find(Class<T> persistable, Criterion... criterions) {
//        Criteria criteria = getSession().createCriteria(persistable);
//        criteria.setMaxResults(1);
//        Criterion[] var3 = criterions;
//        int var4 = criterions.length;
//
//        for(int var5 = 0; var5 < var4; ++var5) {
//            Criterion criterion = var3[var5];
//            criteria.add(criterion);
//        }
//
//        return criteria.uniqueResult();
        return null;
    }

    public static <T extends Object> List<T> findAll(Class<T> persistable) {
        return getSession().createCriteria(persistable).list();
    }

    public static <T extends Object> List<T> findAll(Class<T> persistable, Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(persistable);
        Criterion[] var3 = criterions;
        int var4 = criterions.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Criterion criterion = var3[var5];
            criteria.add(criterion);
        }

        return criteria.list();
    }

    public static void doWork(Work work) {
        getSession().doWork(work);
    }

    public static Connection getUnManagedConnection() throws SQLException {
        Session session = getSession();
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)session.getSessionFactory();
        //Might be a wrong connectionProvider they they may have rolled their own
        ConnectionProvider cp = sessionFactory.getJdbcServices().getConnectionProvider();
        return cp.getConnection();
    }

//    public static <T extends Object> List<T> find(Object searchObject) {
//        //return LegacyPersistence.find(searchObject);
//        return null;
//    }
//
//    public static SearchList find(AggregateSearchObject aggregateSearchObject) {
//        return LegacyPersistence.find(aggregateSearchObject);
//    }
//
//    public static SearchList findWhat(SearchObject searchObj) {
//        return LegacyPersistence.findWhat(searchObj);
//    }
//
//    public static void executePreparedStatementSQL(String sql, Object... parameters) throws PersistenceException, SQLException {
//        LegacyPersistence.executePreparedStatementSQL(sql, parameters);
//    }
//
//    public static int executeUpdateSQL(String sql) throws PersistenceException, SQLException {
//        return LegacyPersistence.executeUpdateSQL(sql);
//    }
}
