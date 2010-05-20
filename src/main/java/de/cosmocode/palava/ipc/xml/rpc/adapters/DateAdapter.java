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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.commons.DateFormats;
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
    
    public static final DateFormat PSEUDO_ISO_8061 = DateFormats.concurrent(
        new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss")
    );

    static final TypeLiteral<Adapter<Value, Date>> LITERAL =
        new TypeLiteral<Adapter<Value, Date>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public DateAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public Date decode(Value input) {
        Preconditions.checkNotNull(input, "Input");
        try {
            return PSEUDO_ISO_8061.parse(input.getDateTimeIso8601());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    @Override
    public Value encode(Date input) {
        Preconditions.checkNotNull(input, "Input");
        final Value value = factory.createValue();
        value.setDateTimeIso8601(PSEUDO_ISO_8061.format(input));
        return value;
    }
    
}
