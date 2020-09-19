package org.flowable.sample.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author Valentin Zickner
 */
public class ShowCardInvalidJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        System.out.println("#####################################################");
        System.out.println("# Your card is INVALID - please see agent           #");
        System.out.println("#####################################################");
    }
}
