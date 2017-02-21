package fi.ooproject;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Backend launcher.
 *
 * @author Juuso Pakarinen
 * @version 2016.1115
 * @since 1.8
 */
@ApplicationPath("/api")
public class StartApplication extends Application {
    
    /**
     * Loads classes.
     */
    @Override public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ShoppingResource.class); 
        return classes;
    }
}
