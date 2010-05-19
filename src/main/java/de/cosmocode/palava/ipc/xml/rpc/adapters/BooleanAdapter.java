package de.cosmocode.palava.ipc.xml.rpc.adapters;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.Boolean;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;

/**
 * A {@link Boolean} to {@link java.lang.Boolean} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class BooleanAdapter implements Adapter<Boolean, java.lang.Boolean> {

    static final TypeLiteral<Adapter<Boolean, java.lang.Boolean>> LITERAL =
        new TypeLiteral<Adapter<Boolean, java.lang.Boolean>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public BooleanAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }

    @Override
    public java.lang.Boolean decode(Boolean input) {
        Preconditions.checkNotNull(input, "Input");
        return input.isBoolean();
    }
    
    @Override
    public Boolean encode(java.lang.Boolean input) {
        Preconditions.checkNotNull(input, "Input");
        final Boolean b = factory.createBoolean();
        b.setBoolean(input);
        return b;
    }
    
}
