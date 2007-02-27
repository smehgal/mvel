package org.mvel.block;

import org.mvel.CompileException;
import org.mvel.ExecutableStatement;
import static org.mvel.MVEL.compileExpression;
import org.mvel.Token;
import org.mvel.integration.VariableResolverFactory;
import org.mvel.integration.impl.LocalVariableResolverFactory;
import org.mvel.util.ParseTools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christopher Brock
 */
public class ForEachToken extends Token {

    protected char[] block;

    protected String item;
    protected ExecutableStatement condition;
    protected ExecutableStatement compiledBlock;

    public ForEachToken(char[] condition, char[] block, int fields) {
        super(condition, fields);
        handleCond(condition);
        this.compiledBlock = (ExecutableStatement) compileExpression(this.block = block);
    }


    public Object getReducedValueAccelerated(Object ctx, Object thisValue, VariableResolverFactory factory) {
        Map<String, Object> locals = new HashMap<String, Object>();
        LocalVariableResolverFactory local = new LocalVariableResolverFactory(locals);
        local.setNextFactory(factory);

        Object ret;

        Object iterCond = condition.getValue(ctx, thisValue, factory);


        if (iterCond instanceof Iterable) {
            for (Object o : (Iterable) iterCond) {
                locals.put(item, o);
                if ((ret = compiledBlock.getValue(ctx, thisValue, local)) != null) {
                    return ret;
                }
            }
        }
        else if (iterCond instanceof Object[]) {
            for (Object o : (Object[]) iterCond) {
                locals.put(item, o);
                if ((ret = compiledBlock.getValue(ctx, thisValue, local)) != null) {
                    return ret;
                }
            }
        }


        return Void.class;
    }

    private void handleCond(char[] condition) {
        int cursor = 0;
        while (cursor < condition.length && condition[cursor] != ':') cursor++;

        if (condition[cursor] != ':')
            throw new CompileException("expected : in foreach");

        item = new String(condition, 0, cursor).trim();

        cursor++;

        this.condition = (ExecutableStatement) compileExpression(ParseTools.subset(condition, cursor));
    }
}
