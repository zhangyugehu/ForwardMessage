package com.thssh.smsdispatcher.dispatcher;

import android.os.Build;

import java.util.Locale;

public class DispatcherFactory {
    public static class UnSupportedDeviceException extends Throwable {
        public UnSupportedDeviceException(String device) {
            super(String.format(Locale.getDefault(), "Sorry, Device \"%s\" is Un-Supported Yet!!!", device));
        }
    }
    public static Dispatcher createDispatcher() throws UnSupportedDeviceException {
        String manufacturer = Build.MANUFACTURER;
        if ("xiaomi".equalsIgnoreCase(manufacturer)) {
        } else if ("HUAWEI".equalsIgnoreCase(manufacturer)) {
            return new HonorDispatcher();
        } else if ("OPPO".equalsIgnoreCase(manufacturer)) {
        } else if ("vivo".equalsIgnoreCase(manufacturer)) {
        } else if ("Google".equalsIgnoreCase(manufacturer)) {
            return new GoogleDispatcher();
        } else if ("Samsung".equalsIgnoreCase(manufacturer)) {
            return new SamsungDispatcher();
        }
        throw new UnSupportedDeviceException(manufacturer);
    }
}
