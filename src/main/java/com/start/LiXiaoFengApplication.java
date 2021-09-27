package com.start;

import com.config.AppConfig;
import com.config.BeanDefinition;
import com.service.UserService;
import com.service.impl.UserServiceImpl;
import com.spring.Component;
import com.spring.ComponentScan;
import com.spring.Resource;
import com.spring.Scope;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiXiaoFengApplication {

    private Class config;

    private ConcurrentHashMap<String,Object> singleTonContainer = new ConcurrentHashMap<String,Object>();//单例池
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMaps = new ConcurrentHashMap<String,BeanDefinition>();

    /**
     * 初始化容器
     * @param config
     */
    private LiXiaoFengApplication(Class config) {
        this.config = config;
        doScanPackage(config);

        //实例化创建bean放入单例池
        for (Map.Entry<String, BeanDefinition> beanDefinition : beanDefinitionMaps.entrySet()) {
            String beanName = beanDefinition.getKey();
            BeanDefinition bean = beanDefinition.getValue();
            Object bean1 = createBean(bean);
            singleTonContainer.put(beanName, bean1);
        }
        for (Map.Entry<String, BeanDefinition> beanDefinition : beanDefinitionMaps.entrySet()) {
            String beanName = beanDefinition.getKey();
            BeanDefinition bean = beanDefinition.getValue();
            dependencyInjection(bean,singleTonContainer.get(beanName));
        }
    }

    /**
     * 扫描路径下面的所有类，查看是否需要spring容器管理
     * @param config
     */
    private void doScanPackage(Class config) {
        Annotation declaredAnnotation = config.getDeclaredAnnotation(ComponentScan.class);
        //初始化容器，查看需要添加到容器的bean对象的路径
        if (declaredAnnotation != null) {
            String path = ((ComponentScan) declaredAnnotation).value();
            ClassLoader classLoader = LiXiaoFengApplication.class.getClassLoader();
            URL resource = classLoader.getResource(path.replace(".", "/"));
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                File[] listFiles = file.listFiles();
                for (File listFile : listFiles) {
                    String fileName = listFile.getAbsolutePath();
                    if (fileName.endsWith(".class")) {
                        String className = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class"));
                        className = className.replace("\\", ".");
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            //判断当前类是否bean实例
                            boolean flag = clazz.isAnnotationPresent(Component.class);
                            if (flag) {
                                //获取beanName
                                Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                                String beanName = componentAnnotation.value();
                                if ("".equals(beanName) || beanName == null) {
                                    beanName = clazz.getSimpleName();
                                }
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setaClass(clazz);
                                //判断bean的作用域
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                    beanDefinition.setScope(scopeAnnotation.value());
                                } else {
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMaps.put(beanName,beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**

     * 创建bean对象
     * @param
     * @return
     */
    public Object  createBean(BeanDefinition beanDefinition) {
        Class aClass = beanDefinition.getaClass();
        try {
            Object instance = aClass.getDeclaredConstructor().newInstance();

            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dependencyInjection(BeanDefinition beanDefinition,Object instance){
        Class aClass = beanDefinition.getaClass();
        //依赖注入
        for(Field field :aClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resource.class)) {
                Object bean = getBean(field.getName());
                Resource resourceAnnotation = field.getDeclaredAnnotation(Resource.class);

                field.setAccessible(true);//开启对对象私有属性访问
                try {
                    field.set(instance,bean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据@Scope获取bean对象
     * @param beanName
     * @return
     */
    public Object getBean(String beanName) {
        //如果存在bean对象
        if (beanDefinitionMaps.containsKey(beanName)) {
            BeanDefinition bean = beanDefinitionMaps.get(beanName);
            //单例bean直接从单例池获取，多例直接创建bean
            if ("singleton".equals(bean.getScope())) {
                return singleTonContainer.get(beanName);
            } else {
                return createBean(bean);
            }
        }
        throw new IllegalArgumentException("请传入正确的bean对象名称");
    }

    public static void main(String[] args) {

        LiXiaoFengApplication application = new LiXiaoFengApplication(AppConfig.class);
        UserServiceImpl service = (UserServiceImpl) application.getBean("userServiceImpl");
        service.test();
        //service.studentServiceImpl("今天天气真好！");
    }

}
