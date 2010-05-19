package de.cosmocode.palava.ipc.xml.rpc.adapters;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.xml.rpc.XmlRpc;
import de.cosmocode.palava.ipc.xml.rpc.generated.I4;
import de.cosmocode.palava.ipc.xml.rpc.generated.ObjectFactory;

/**
 * A {@link I4} to {@link Integer} adapter.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class I4IntegerAdapter implements Adapter<I4, Integer> {

    static final TypeLiteral<Adapter<I4, Integer>> LITERAL =
        new TypeLiteral<Adapter<I4, Integer>>() { };

    private final ObjectFactory factory;
    
    @Inject
    public I4IntegerAdapter(@XmlRpc ObjectFactory factory) {
        this.factory = Preconditions.checkNotNull(factory, "Factory");
    }
    
    @Override
    public Integer decode(I4 input) {
        Preconditions.checkNotNull(input, "Input");
        return input.getI4();
    }
    
    @Override
    public I4 encode(Integer input) {
        Preconditions.checkNotNull(input, "Input");
        final I4 i4 = factory.createI4();
        i4.setI4(input.intValue());
        return i4;
    }
    
}
