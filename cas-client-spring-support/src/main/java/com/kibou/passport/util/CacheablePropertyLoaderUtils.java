package com.kibou.passport.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

/**
 * @author aimysaber@gmail.com
 *
 */
public class CacheablePropertyLoaderUtils {
	
	private static ConcurrentMap<String, Properties> resourceNamePropertiesMap = new ConcurrentHashMap<>();

	//对Spring的PropertyLoaderUtils进行了修改
	public static Properties loadAllProperties(String resourceName) throws IOException {
		return loadAllProperties(resourceName, null);
	}
	
	public static Properties loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException {
		Assert.notNull(resourceName, "Resource name must not be null");
		
		Properties cacheProperties = resourceNamePropertiesMap.get(resourceName);
		if(cacheProperties != null)
			return cacheProperties;
		
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		//FIXME 优先选择 项目中的配置 没有再找当前jar包的默认配置
		Enumeration<URL> urls = (classLoaderToUse != null ? classLoaderToUse.getResources(resourceName)
				: ClassLoader.getSystemResources(resourceName));
		Properties props = new Properties();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			URLConnection con = url.openConnection();
			ResourceUtils.useCachesIfNecessary(con);
			InputStream is = con.getInputStream();
			try {
				if (resourceName.endsWith(".xml")) {
					props.loadFromXML(is);
				} else {
					props.load(is);
				}
			} finally {
				is.close();
			}
		}
		resourceNamePropertiesMap.putIfAbsent(resourceName, props);
		return props;
	}
	
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}
}
