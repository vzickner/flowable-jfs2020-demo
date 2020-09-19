package org.flowable.sample.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author Valentin Zickner
 */
public class ReturnCardJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("#####################################################");
        System.out.println("# Please take your card back                        #");
        System.out.println("#####################################################");
        System.out.println("#                                                   #");
        System.out.println("# ------------------------------------------------- #");
        System.out.println("#       |                                 |         #");
        System.out.println("#       |                                 |         #");
        System.out.println("#       |                                 |         #");
        System.out.println("#       |                                 |         #");
        System.out.println("#        \\                               /          #");
        System.out.println("#         -------------------------------           #");
        System.out.println("#                                                   #");
        System.out.println("#####################################################");
    }
}
