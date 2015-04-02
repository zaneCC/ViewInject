package com.zane.viewinject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.view.View;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(listenerType=View.OnClickListener.class,methodName="onClick",listenerSetter="setOnClickListener")
public @interface OnClick {

	int[] value();
}
