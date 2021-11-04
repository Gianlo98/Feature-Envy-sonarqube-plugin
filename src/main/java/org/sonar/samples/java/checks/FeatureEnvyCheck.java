package org.sonar.samples.java.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import org.sonar.plugins.java.api.tree.Tree.Kind;

import java.util.*;

@Rule(key = "FeatureEnvyRule")
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
        instanceVariables.clear();
        fanOutClasses.clear();

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

        System.out.println("-----------------------------------");
        System.out.println("Class: " + classTree.simpleName());
        System.out.println("highExternalCalls: " + highExternalCalls + " (" + staticMethodsCounter + "/" + totalMethodsCounter + ")" );
        System.out.println("internalExternalCallRatio: " + internalExternalCallRatio + " (" + internalCallCounter + "/" + externalCallCounter + ")");
        System.out.println("lowFO: " + lowFO + " (" + fanOutClasses.size() + ")");
        System.out.println("staticMethodsOverused: "  + staticMethodsOverused + " (" + staticMethodsCounter + "/" + totalMethodsCounter + ")");

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
            if (Objects.requireNonNull(tree.parent()).is(Kind.CLASS)) {
                instanceVariables.add(tree.simpleName().name());
            }
            checkVariableTree(tree);
        }

        @Override
        public void visitMethod(MethodTree method) {

            method.parameters().forEach(this::checkVariableTree);


            // CHECK STATIC VS NON STATIC
            ModifiersTree modifiers = method.modifiers();

            for (ModifierTree modifier : modifiers) {
                FeatureEnvyCheck.totalMethodsCounter++;
                if (modifier instanceof ModifierKeywordTree) {
                    if (((ModifierKeywordTree) modifier).modifier().equals(Modifier.STATIC)) {
                        FeatureEnvyCheck.staticMethodsCounter++;
                    }
                }
            }


            Objects.requireNonNull(method.block()).accept(featureEnvyVisitor);

        }

        private void checkVariableTree(VariableTree tree) {
            if (!tree.type().is(Kind.PRIMITIVE_TYPE)) {

                if (tree.type() instanceof IdentifierTree) {
                    IdentifierTree a = (IdentifierTree) tree.type();
                    fanOutClasses.add(a.name());
                } else {
                    if (tree.type() instanceof ParameterizedTypeTree) {
                        ParameterizedTypeTree temp = ((ParameterizedTypeTree) tree.type());
                        if (temp.type() instanceof IdentifierTree) {
                            IdentifierTree a = (IdentifierTree) temp.type();
                            fanOutClasses.add(a.name());
                        }
                    }
                }
            }
        }


    }
}