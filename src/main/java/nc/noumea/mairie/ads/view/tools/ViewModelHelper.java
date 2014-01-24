package nc.noumea.mairie.ads.view.tools;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.zk.ui.event.EventQueues;

/**
 * This class aims at providing a non static implementation of the ZK BindUtils static class.
 * This helper class provides the same methods and signature as BindUtils while allowing the consumers
 * to be fully unit tested because providing it as a Java Singleton Bean.
 * @author Nicolas
 *
 */
@Service
public class ViewModelHelper {

	/**
	 * Post a global command to corresponding event queue
	 * @param queueName the queue name, null for default queue name
	 * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
	 * @param cmdName the global command name
	 * @param args arguments, could get the data in command method by {@link BindingParam}
	 */
	public void postGlobalCommand(String queueName, String queueScope, String cmdName, Map<String, Object> args) {
		BindUtils.postGlobalCommand(queueName, queueScope, cmdName, args);
	}

	/**
	 * Post a notify change to corresponding event queue to notify a bean's property changing
	 * @param queueName the queue name, null for default queue name
	 * @param queueScope the queue scope, null for default queue scope (i.e. {@link EventQueues#DESKTOP})
	 * @param bean the bean instance
	 * @param property the property name of bean
	 */
	public void postNotifyChange(String queueName, String queueScope, Object bean, String property) {
		BindUtils.postNotifyChange(queueName, queueScope, bean, property);
	}
}
