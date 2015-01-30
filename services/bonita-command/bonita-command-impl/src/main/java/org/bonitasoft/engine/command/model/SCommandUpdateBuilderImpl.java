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
package org.bonitasoft.engine.command.model;

import org.bonitasoft.engine.recorder.model.EntityUpdateDescriptor;

/**
 * @author Matthieu Chaffotte
 */
public class SCommandUpdateBuilderImpl implements SCommandUpdateBuilder {

    private final EntityUpdateDescriptor descriptor;
    
    public SCommandUpdateBuilderImpl(final EntityUpdateDescriptor descriptor) {
        super();
        this.descriptor = descriptor;
    }
    
    @Override
    public SCommandUpdateBuilder updateName(final String name) {
        this.descriptor.addField(SCommandBuilderFactoryImpl.NAME, name);
        return this;
    }

    @Override
    public SCommandUpdateBuilder updateDescription(final String description) {
        this.descriptor.addField(SCommandBuilderFactoryImpl.DESCRIPTION, description);
        return this;
    }

    @Override
    public EntityUpdateDescriptor done() {
        return this.descriptor;
    }

}
