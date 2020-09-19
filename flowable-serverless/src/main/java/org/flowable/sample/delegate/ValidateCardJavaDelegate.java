package org.flowable.sample.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * @author Valentin Zickner
 */
public class ValidateCardJavaDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Object cardId = execution.getVariable("cardId");
        execution.setVariable("cardValid", cardId != null);
        execution.setVariable("customerId", "C" + cardId);
    }
}
