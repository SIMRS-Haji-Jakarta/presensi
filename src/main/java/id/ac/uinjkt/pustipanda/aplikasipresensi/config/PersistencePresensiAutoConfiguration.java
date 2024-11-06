package id.ac.uinjkt.pustipanda.aplikasipresensi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(basePackages = "id.ac.uinjkt.pustipanda.aplikasipresensi.dao.presensi", entityManagerFactoryRef = "presensiEntityManager", transactionManagerRef = "presensiTransactionManager")
public class PersistencePresensiAutoConfiguration {
    @Autowired
    private Environment env;

    public PersistencePresensiAutoConfiguration() {
        super();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean presensiEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(presensiDataSource());
        em.setPackagesToScan("id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi");

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        properties.put("hibernate.dialect", env.getProperty("spring.jpa.database-platform"));
        em.setJpaPropertyMap(properties);

        return em;
    }

    @Primary @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource presensiDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary @Bean
    public PlatformTransactionManager presensiTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(presensiEntityManager().getObject());
        return transactionManager;
    }
}
