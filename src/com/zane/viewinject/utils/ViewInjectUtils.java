package com.zane.viewinject.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.app.Activity;
import android.view.View;

import com.zane.viewinject.annotation.ContentView;
import com.zane.viewinject.annotation.EventBase;
import com.zane.viewinject.annotation.ViewInject;

public class ViewInjectUtils {

	private static void injectContentView(Activity activity) {
		ContentView contentView = activity.getClass().getAnnotation(
				ContentView.class);
		if (contentView != null) {
			int id = contentView.value();
			activity.setContentView(id);
		}
	}

	private static void injectView(Activity activity) {
		Class<? extends Activity> clazz = activity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field field : fields) {
				ViewInject viewInject = field.getAnnotation(ViewInject.class);
				field.setAccessible(true);
				if (viewInject != null) {
					int viewId = viewInject.value();
					if (viewId != -1) {
						field.set(activity, activity.findViewById(viewId));
					} else {
						viewId = activity.getResources().getIdentifier(
								field.getName(), "id",
								activity.getPackageName());
						field.set(activity, activity.findViewById(viewId));
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private static void injectEvents(Activity activity) {
		Class<? extends Activity> clazz = activity.getClass();
		Method[] methods = clazz.getMethods();
		// 遍历所有的方法
		for (Method method : methods) {
			Annotation[] annotations = method.getAnnotations();
			// 拿到方法上的所有的注解
			for (Annotation annotation : annotations) {
				Class<? extends Annotation> annotationType = annotation
						.annotationType();
				// 拿到注解上的注解
				EventBase eventBaseAnnotation = annotationType
						.getAnnotation(EventBase.class);
				// 如果设置为EventBase
				if (eventBaseAnnotation != null) {
					// 取出设置监听器的名称，监听器的类型，调用的方法名
					String listenerSetter = eventBaseAnnotation
							.listenerSetter();
					Class<?> listenerType = eventBaseAnnotation.listenerType();
					String methodName = eventBaseAnnotation.methodName();

					try {
						// 拿到Onclick注解中的value方法
						Method aMethod = annotationType
								.getDeclaredMethod("value");
						// 取出所有的viewId
						int[] viewIds = (int[]) aMethod
								.invoke(annotation, null);
						// 通过InvocationHandler设置代理
						DynamicHandler handler = new DynamicHandler(activity);
						handler.addMethod(methodName, method);
						Object listener = Proxy.newProxyInstance(
								listenerType.getClassLoader(),
								new Class<?>[] { listenerType }, handler);
						// 遍历所有的View，设置事件
						for (int viewId : viewIds) {
							View view = activity.findViewById(viewId);
							Method setEventListenerMethod = view.getClass()
									.getMethod(listenerSetter, listenerType);
							setEventListenerMethod.invoke(view, listener);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	public static void inject(Activity activity) {
		injectContentView(activity);
		injectView(activity);
		injectEvents(activity);
	}

}
