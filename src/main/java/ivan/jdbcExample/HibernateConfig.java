package ivan.jdbcExample;

import org.hibernate.cfg.Configuration;

class HibernateConfig {

    public static Configuration prod() {
        return new Configuration().addAnnotatedClass(Message.class);
    }

    private HibernateConfig() {
    }
}