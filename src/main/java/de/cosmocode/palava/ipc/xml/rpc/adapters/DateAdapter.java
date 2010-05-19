/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.xml.rpc.adapters;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.commons.Calendars;
import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;
import de.cosmocode.palava.ipc.xml.rpc.generated.Value;

/**
 * A {@link DateTimeIso8601} to {@link Date} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class DateAdapter implements Adapter<Value, Date> {

    static final TypeLiteral<Adapter<Value, Date>> LITERAL =
        new TypeLiteral<Adapter<Value, Date>>() { };

    private final ObjectFactory factory;
    private final DatatypeFactory datatypeFactory;
    
    @Inject
    public DateAdapter(@XmlRpc ObjectFactory factory, @XmlRpc DatatypeFactory datatypeFactory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
        this.datatypeFactory = Preconditions.checkNotNull(datatypeFactory, "DatatypeFactory");
    }

    @Override
    public Date decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        return input.getDateTimeIso8601().toGregorianCalendar().getTime();
    }
    
    @Override
    public Value encode(Date input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        final GregorianCalendar calendar = Calendars.of(input);
        final XMLGregorianCalendar xmlCalendar = datatypeFactory.newXMLGregorianCalendar(calendar);
        value.setDateTimeIso8601(xmlCalendar);
        return value;
    }
    
}
