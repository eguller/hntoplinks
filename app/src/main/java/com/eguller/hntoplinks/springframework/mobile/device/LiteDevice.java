package com.eguller.hntoplinks.springframework.mobile.device;

import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DevicePlatform;
import org.springframework.mobile.device.DeviceType;

class LiteDevice implements Device {

	public static final LiteDevice NORMAL_INSTANCE = new LiteDevice(DeviceType.NORMAL, DevicePlatform.UNKNOWN);

	public static final LiteDevice MOBILE_INSTANCE = new LiteDevice(DeviceType.MOBILE, DevicePlatform.UNKNOWN);

	public static final LiteDevice TABLET_INSTANCE = new LiteDevice(DeviceType.TABLET, DevicePlatform.UNKNOWN);

	public boolean isNormal() {
		return this.deviceType == DeviceType.NORMAL;
	}

	public boolean isMobile() {
		return this.deviceType == DeviceType.MOBILE;
	}

	public boolean isTablet() {
		return this.deviceType == DeviceType.TABLET;
	}

	public DevicePlatform getDevicePlatform() {
		return this.devicePlatform;
	}

	public DeviceType getDeviceType() {
		return this.deviceType;
	}

	public static Device from(DeviceType deviceType, DevicePlatform devicePlatform) {
		return new LiteDevice(deviceType, devicePlatform);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[LiteDevice ");
		builder.append("type").append("=").append(this.deviceType);
		builder.append("]");
		return builder.toString();
	}

	private final DeviceType deviceType;

	private final DevicePlatform devicePlatform;

	/**
	 * Creates a LiteDevice with DevicePlatform.
	 */
	private LiteDevice(DeviceType deviceType, DevicePlatform devicePlatform) {
		this.deviceType = deviceType;
		this.devicePlatform = devicePlatform;
	}
}
