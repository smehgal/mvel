package org.mvel.compiled;

import org.mvel.AccessorNode;
import org.mvel.integration.VariableResolverFactory;

public class VariableAccessor implements AccessorNode {
    private AccessorNode nextNode;

    private String property;
    private VariableResolverFactory resolverFactory;

    public VariableAccessor(String property, VariableResolverFactory vrf) {
        this.property = property;

        while (vrf != null) {
            if (vrf.isTarget(property)) {
                this.resolverFactory = vrf;
                break;
            }
            vrf = vrf.getNextFactory();
        }

    }

    public Object getValue(Object ctx, Object elCtx, VariableResolverFactory vrf) throws Exception {
        if (nextNode != null) {
            return nextNode.getValue(resolverFactory.getVariableResolver(property).getValue(), elCtx, vrf);
        }
        else {
            return resolverFactory.getVariableResolver(property).getValue();
        }
    }


    public Object getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public AccessorNode getNextNode() {
        return nextNode;
    }

    public AccessorNode setNextNode(AccessorNode nextNode) {
        return this.nextNode = nextNode;
    }


    public String toString() {
        return "Map Accessor -> [" + property + "]";
    }
}
