//package com.meki.play.util.spring;
//
//import net.paoding.rose.scanning.context.RoseAppContext;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanFactoryUtils;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//
//
//public class VipBeanFactory implements ApplicationContextAware {
//
//	private static volatile ApplicationContext ctx;
//
//	//private static Logger logger = LoggerFactory.getLogger(RunOnceTask.class);
//
//	private static void init() {
//		if (ctx == null) {
//			//logger.error("ApplicationContext is null, need be injected!");
//			synchronized (VipBeanFactory.class) {
//				if (ctx == null) {
//					ctx = new RoseAppContext();
//					//logger.error("VipBeanFactory init ok.");
//				}
//			}
//		}
//	}
//
//	@SuppressWarnings("unchecked")
//	public static <T> T getBean(final String name) {
//		init();
//		return (T) ctx.getBean(name);
//	}
//
//	public static <T> T getBean(final Class<T> clazz) {
//		init();
//		return clazz.cast(BeanFactoryUtils.beanOfTypeIncludingAncestors(ctx, clazz));
//	}
//
//	@Override
//	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
//		ctx = applicationContext;
//		//logger.error("VipBeanFactory: applicationContext was set in.");
//	}
//
//	/*public static void main(String[] args) {
//		init();
//	}*/
//
//}
