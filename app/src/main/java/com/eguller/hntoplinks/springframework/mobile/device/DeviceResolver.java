package com.eguller.hntoplinks.springframework.mobile.device;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.mobile.device.Device;

public interface DeviceResolver {

	/**
	 * Resolve the device that originated the web request.
	 */
	Device resolveDevice(HttpServletRequest request);

}
