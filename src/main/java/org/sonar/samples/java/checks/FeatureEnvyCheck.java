package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.java.model.JavaTree;
import org.sonar.java.model.declaration.MethodTreeImpl;
import org.sonar.java.model.declaration.ModifierKeywordTreeImpl;
import org.sonar.java.model.expression.IdentifierTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Rule(key = "MyFirstCustomRule")
public class FeatureEnvyCheck  extends IssuableSubscriptionVisitor {

    private final BaseTreeVisitor featureEnvyVisitor = new FeatureEnvyVisitor();

    public static double staticMethodsCounter, totalMethodsCounter;
    public static double internalCallCounter, externalCallCounter;
    public static Set<String> instanceVariables = new HashSet<>();
    public static Set<String> fanOutClasses = new HashSet<>();

    private final double HIGH_CALL_THRESHOLD = 4;
    private final double LOW_INT_EXT_CALL_RATIO_THRESHOLD = 0.3;
    private final double LOW_FO_THRESHOLD = 4;
    private final double STATIC_OVERUSED_THRESHOLD = 0.3;

    public List<Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.CLASS);
    }

    @Override
    public void visitNode(Tree tree) {
        staticMethodsCounter = 0;
        totalMethodsCounter = 0;
        internalCallCounter = 0;
        externalCallCounter = 0;


        ClassTree classTree = (ClassTree) tree;
        classTree.accept(featureEnvyVisitor);

        boolean highExternalCalls = externalCallCounter >= HIGH_CALL_THRESHOLD;
        boolean internalExternalCallRatio = externalCallCounter != 0 &&
                (internalCallCounter / externalCallCounter) <= LOW_INT_EXT_CALL_RATIO_THRESHOLD;
        boolean lowFO = fanOutClasses.size() <= LOW_FO_THRESHOLD;
        boolean staticMethodsOverused = totalMethodsCounter != 0 &&
                staticMethodsCounter / totalMethodsCounter >= STATIC_OVERUSED_THRESHOLD;


        if (highExternalCalls && internalExternalCallRatio && lowFO && staticMethodsOverused) {
            reportIssue(tree, "Feature envy");
        }


        System.out.println("static/total: " + staticMethodsCounter + " / " + totalMethodsCounter);
        System.out.println("internal: " + internalCallCounter);
        System.out.println("external: " + externalCallCounter);
        System.out.println("instanceVars: " + instanceVariables);
        System.out.println("fanOutClasses: " +fanOutClasses);

    }

    private class FeatureEnvyVisitor extends BaseTreeVisitor {

        @Override
        public void visitMethodInvocation(MethodInvocationTree tree) {

            if (tree.methodSelect() instanceof MemberSelectExpressionTree) {
                String invocatorMethodVariableName = ((MemberSelectExpressionTree) tree.methodSelect()).expression().toString();

                if (instanceVariables.contains(invocatorMethodVariableName)) {
                    internalCallCounter++;
                } else {
                    externalCallCounter++;
                }
            }
        }

        @Override
        public void visitVariable(VariableTree tree) {
            // Check if the variable is a local instance
            if (tree.parent().is(Kind.CLASS)) {
                instanceVariables.add(tree.simpleName().name());
            }
            checkVariableTree(tree);
        }

        @Override
        public void visitMethod(MethodTree method) {

            method.parameters().forEach(this::checkVariableTree);

            if (method instanceof MethodTreeImpl) {
                MethodTreeImpl methodTree = (MethodTreeImpl) method;
                // CHECK STATIC VS NON STATIC
                ModifiersTree modifiers = methodTree.modifiers();

                for (ModifierTree modifier : modifiers) {
                    FeatureEnvyCheck.totalMethodsCounter++;
                    if (modifier instanceof ModifierKeywordTreeImpl) {
                        if (((ModifierKeywordTreeImpl) modifier).modifier().equals(Modifier.STATIC)) {
                            FeatureEnvyCheck.staticMethodsCounter++;
                        }
                    }
                }

                methodTree.block().accept(featureEnvyVisitor);
            }
        }

        private void checkVariableTree(VariableTree tree) {
            if (!tree.type().is(Kind.PRIMITIVE_TYPE)) {
                if (tree.type() instanceof IdentifierTreeImpl) {
                    IdentifierTreeImpl a = (IdentifierTreeImpl) tree.type();
                    fanOutClasses.add(a.name());
                } else {
                    if (tree.type() instanceof JavaTree.ParameterizedTypeTreeImpl) {
                        IdentifierTreeImpl a = (IdentifierTreeImpl) ((JavaTree.ParameterizedTypeTreeImpl) tree.type()).type();
                        fanOutClasses.add(a.name());
                    }
                }
            }
        }


    }
}