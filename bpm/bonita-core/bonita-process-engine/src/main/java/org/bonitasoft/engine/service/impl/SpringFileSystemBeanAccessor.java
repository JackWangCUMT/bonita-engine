/**
 * Copyright (C) 2015 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 **/
package org.bonitasoft.engine.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.bonitasoft.engine.exception.BonitaHomeNotSetException;
import org.bonitasoft.engine.exception.BonitaRuntimeException;
import org.bonitasoft.engine.home.BonitaHomeServer;
import org.bonitasoft.platform.configuration.model.BonitaConfiguration;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;

/**
 * @author Charles Souillard
 */
public abstract class SpringFileSystemBeanAccessor {

    static final BonitaHomeServer BONITA_HOME_SERVER = BonitaHomeServer.getInstance();
    private final ApplicationContext parent;
    private AbsoluteFileSystemXmlApplicationContext context;

    public SpringFileSystemBeanAccessor(ApplicationContext parent) {
        this.parent = parent;
    }

    protected String[] getActiveProfiles() throws IOException, BonitaHomeNotSetException {
        final Properties properties = BONITA_HOME_SERVER.getPlatformInitProperties();
        final String activeProfiles = (String) properties.get("activeProfiles");
        return activeProfiles.split(",");
    }

    public <T> T getService(final Class<T> serviceClass) {
        return getContext().getBean(serviceClass);
    }

    public <T> T getService(String name, Class<T> clazz) {
        return getContext().getBean(name, clazz);
    }

    public <T> T getService(String serviceName) {
        return (T) getContext().getBean(serviceName);
    }

    public ApplicationContext getContext() {
        if (context == null) {
            try {
                context = new AbsoluteFileSystemXmlApplicationContext(new String[] {}, parent);
                List<String> classPathResources = getClassPathResources(BONITA_HOME_SERVER.getPlatformProperties());
                for (String classPathResource : classPathResources) {
                    context.addClassPathResource(classPathResource);
                }
                for (BonitaConfiguration bonitaConfiguration : getConfiguration()) {
                    context.addByteArrayResource(bonitaConfiguration);
                }
                final PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
                final Properties properties = getProperties();
                configurer.setProperties(properties);
                context.addBeanFactoryPostProcessor(configurer);
                final String[] activeProfiles = getActiveProfiles();
                context.getEnvironment().setActiveProfiles(activeProfiles);
                context.refresh();
            } catch (IOException | BonitaHomeNotSetException e) {
                throw new BonitaRuntimeException(e);
            }
        }
        return context;
    }

    public void destroy() {
        if (context != null) {
            context.close();
            context = null;
        }
    }

    protected abstract Properties getProperties() throws IOException;

    protected abstract List<BonitaConfiguration> getConfiguration() throws IOException;

    protected abstract List<String> getClassPathResources(Properties properties);

    String getPropertyWithPlaceholder(Properties properties, String key, String defaultValue) {
        String property = properties.getProperty(key, defaultValue);
        if (property.startsWith("${") && property.endsWith("}")) {
            property = property.substring(2, property.length() - 1);
            String sysPropertyKey = property.substring(0, property.indexOf(':'));
            String sysPropertyDefaultValue = property.substring(property.indexOf(':')+1, property.length());
            return System.getProperty(sysPropertyKey, sysPropertyDefaultValue);
        }
        return property;
    }
}
