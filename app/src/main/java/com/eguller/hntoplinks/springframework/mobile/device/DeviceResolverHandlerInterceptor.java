package com.eguller.hntoplinks.springframework.mobile.device;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DeviceResolverHandlerInterceptor implements HandlerInterceptor {

	private final DeviceResolver deviceResolver;

	/**
	 * Create a device resolving {@link HandlerInterceptor} that defaults to a {@link LiteDeviceResolver} implementation.
	 */
	public DeviceResolverHandlerInterceptor() {
		this(new LiteDeviceResolver());
	}

	/**
	 * Create a device resolving {@link HandlerInterceptor}.
	 * @param deviceResolver the device resolver to delegate to in {@link #preHandle(HttpServletRequest, HttpServletResponse, Object)}.
	 */
	public DeviceResolverHandlerInterceptor(DeviceResolver deviceResolver) {
		this.deviceResolver = deviceResolver;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Device device = deviceResolver.resolveDevice(request);
		request.setAttribute(DeviceUtils.CURRENT_DEVICE_ATTRIBUTE, device);
		return true;
	}

}
