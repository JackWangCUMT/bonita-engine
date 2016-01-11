/*
 * Copyright (C) 2016 Bonitasoft S.A.
 * Bonitasoft, 32 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA 02110-1301, USA.
 */
package org.bonitasoft.engine.api.impl.transaction.expression.bdm;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import org.assertj.core.api.Assertions;
import org.bonitasoft.engine.bdm.Entity;
import org.bonitasoft.engine.business.data.proxy.ServerLazyLoader;
import org.bonitasoft.engine.business.data.proxy.ServerProxyfier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Romain Bioteau
 * @author Laurent Leseigneur
 */
@RunWith(MockitoJUnitRunner.class)
public class ServerProxyfierTest {

    @Mock
    private ServerLazyLoader lazyLoader;
    private ServerProxyfier serverProxyfier;

    @Before
    public void setUp() throws Exception {
        serverProxyfier = new ServerProxyfier(lazyLoader);
    }

    @Test
    public void should_proxify_an_entity() throws Exception {
        PersonEntity proxy = serverProxyfier.proxify(new PersonEntity());

        assertThat(proxy).isInstanceOf(ProxyObject.class);
        Assertions.assertThat(proxy.getClass().getSuperclass()).isEqualTo(PersonEntity.class);
    }

    @Test
    public void should_not_reproxify_a_server_proxy() throws Exception {
        PersonEntity originalProxy = serverProxyfier.proxify(new PersonEntity());
        PersonEntity proxy = serverProxyfier.proxify(originalProxy);

        assertThat(proxy).isSameAs(originalProxy);
    }

    @Test
    public void should_reproxify_an_hibernate_proxy() throws Exception {
        final ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(PersonEntity.class);
        Entity aProxy = (Entity) factory.create(new Class[0], new Object[0], new MethodHandler() {

            @Override
            public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
                return null;
            }
        });
        PersonEntity proxy = (PersonEntity) serverProxyfier.proxify(aProxy);

        assertThat(proxy).isNotSameAs(aProxy);
    }

}
