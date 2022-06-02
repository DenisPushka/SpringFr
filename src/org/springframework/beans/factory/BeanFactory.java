package org.springframework.beans.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BeanFactory {
    private Map<String, Object> singletons = new HashMap();

    public Object getBean(String beanName) {
        return singletons.get(beanName);
    }

    public void instantiate(String basePackage) {
        try {
            var classLoader = ClassLoader.getSystemClassLoader();
            var path = basePackage.replace('.', '/');
            var resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                var resource = resources.nextElement();
                var file = new File(resource.toURI());
                for (File classFile : file.listFiles()) {
                    var fileName = classFile.getName();
                    if (fileName.endsWith(".class")) {
                        var className = fileName.substring(0, fileName.lastIndexOf("."));
                        var classObject = Class.forName(basePackage + "." + className);

                        if (classObject.isAnnotationPresent(Component.class)) {
                            System.out.println("Component: " + classObject);
                            var instance = classObject.newInstance();
                            var beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                            singletons.put(beanName, instance);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void populateProperties() {
        System.out.println("==populateProperties==");
        for (var object : singletons.values()) {
            for (var field : object.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {

                    for (var dependency : singletons.values()) {
                        if (dependency.getClass().equals(field.getType())) {
                            var setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                            System.out.println("Setter name = " + setterName);
                            Method setter;
                            try {
                                setter = object.getClass().getMethod(setterName, dependency.getClass());
                                setter.invoke(object, dependency);
                            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
