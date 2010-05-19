package de.cosmocode.palava.ipc.xml.rpc.adapters;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.I4;
import de.cosmocode.palava.ipc.xml.rpc.generated.Int;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;

/**
 * A {@link I4} to {@link Integer} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class IntIntegerAdapter implements Adapter<Int, Integer> {

    static final TypeLiteral<Adapter<Int, Integer>> LITERAL =
        new TypeLiteral<Adapter<Int, Integer>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public IntIntegerAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public Integer decode(Int input) {
        Preconditions.checkNotNull(input, "Input");
        return input.getInt();
    }
    
    @Override
    public Int encode(Integer input) {
        Preconditions.checkNotNull(input, "Input");
        final Int i = factory.createInt();
        i.setInt(input.intValue());
        return i;
    }
    
}
