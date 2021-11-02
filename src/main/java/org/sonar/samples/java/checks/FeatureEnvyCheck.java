package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.java.model.declaration.MethodTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import java.util.Collections;
import java.util.List;

@Rule(key = "MyFirstCustomRule")
public class FeatureEnvyCheck  extends IssuableSubscriptionVisitor {
    @Override
    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.CLASS);
    }
    @Override
    public void visitNode(Tree tree) {
        // Cast the node to the correct type :
        // in this case we registered only to one kind so we will only receive ClassTree see Tree.Kind enum to know about which type you can
        // cast depending on Kind.
        // RULES:
        // external calls
        // int/ext
        // fo
        // stat/calls
        ClassTree classTree = (ClassTree) tree;
        for (Tree method : classTree.members()) {
            if (method instanceof MethodTreeImpl) {
                MethodTreeImpl methodTree = (MethodTreeImpl) method;
                // CHECK STATIC VS NON STATIC
                ModifiersTree modifiers = methodTree.modifiers();
                for (ModifierTree modifier : modifiers) {
                    if (modifier.equals(Modifier.STATIC)) {
                        // TODO
                    } else {
                        // TODO
                    }
                }
                List<StatementTree> body = methodTree.block().body();
            }
            
        }
//        // Retrieve
//        Symbol.MethodSymbol methodSymbol = methodTree.symbol();
//        Type returnType = methodSymbol.returnType().type();
//        // Check method has only one argument.
//        if (methodSymbol.parameterTypes().size() == 1) {
//            Type argType = methodSymbol.parameterTypes().get(0);
//            // Verify argument type is same as return type.
//            if (argType.is(returnType.fullyQualifiedName())) {
//                // raise an issue on this node of the SyntaxTree
//                reportIssue(tree, "message");
//            }
//        }
    }

}
