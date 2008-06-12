/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Introspection.java,v 1.2 2008-06-12 11:54:11 bcolucci Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2008/06/12 09:30:06  bcolucci
 * *** empty log message ***
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.lang.reflect.*;

import java.util.logging.*;


/** This class is used for introspection */
public class Introspection
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc");

    /**
     * Returns class according to classpath
     * passed in argument
     *
     * @param classpath classpath of the class to find
     *
     * @return class according to classpath given
     */
    public static Class getClass(String classpath)
    {
        Class classSearched = null;

        try
        {
            classSearched = Class.forName(classpath);
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot find class '" + classpath + "'");
        }

        return classSearched;
    }

    /**
     * Returns true if the class exists
     *
     * @param className class name
     *
     * @return true if the class exists
     */
    public static boolean isClassExists(String className)
    {
        return (getClass(className) != null);
    }

    /**
     * Return a new instance of the class
     * according to the class name passed in
     * argument
     *
     * @param className class name
     *
     * @return new instance of the class
     */
    public static Object getInstance(String className)
    {
        Object instance = null;

        try
        {
            instance = getClass(className).newInstance();
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot get instance of '" + className + "'");
        }

        return instance;
    }

    /**
     * Returns method, in the class according to the
     * class name passed in argument, which name according to
     * the method name passed in argument
     *
     * @param className class name
     * @param methodName method name
     *
     * @return method in className and named methodName
     */
    public static Method getMethod(String className, String methodName)
    {
        return getMethod(className, methodName, new Class[] {  });
    }

    /**
     * Returns methods of the class
     * according to the class name
     * passed in argument
     *
     * @param className class name
     *
     * @return methods of the class
     */
    public static Method[] getMethods(String className)
    {
        return getClass(className).getMethods();
    }

    /**
     * Returns method of the class
     * according to the class name
     * passed in argument. The method searched
     * is named methodName and have parameters
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     *
     * @return method according to className, methodName and parameters
     */
    public static Method getMethod(String className, String methodName,
        Class[] parameters)
    {
        Method methodSearched = null;

        try
        {
            methodSearched = getClass(className)
                                 .getMethod(methodName, parameters);
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot find method '" + methodName + "'");
        }

        return methodSearched;
    }

    /**
     * Returns true if the method exists
     *
     * @param className class name
     * @param methodName method name
     *
     * @return true if the method exists
     */
    public static boolean isMethodExists(String className, String methodName)
    {
        return isMethodExists(className, methodName, new Class[] {  });
    }

    /**
     * Returns true if the method exists
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     *
     * @return true if the method exists
     */
    public static boolean isMethodExists(String className, String methodName,
        Class[] parameters)
    {
        return (getMethod(className, methodName, parameters) != null);
    }

    /**
     * Returns value of the method according
     * to the className and the methodName
     *
     * @param className class name
     * @param methodName method name
     *
     * @return value of the method
     */
    public static Object getMethodValue(String className, String methodName)
    {
        return getMethodValue(className, methodName, new Class[] {  });
    }

    /**
     * Returns value of the method according
     * to the className, the methodName and
     * the parameters
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     *
     * @return value of the method
     */
    public static Object getMethodValue(String className, String methodName,
        Class[] parameters)
    {
        return getMethodValue(className, methodName, parameters,
            new Object[] {  });
    }

    /**
     * Returns value of the method according
     * to the className, the methodName,
     * the parameters and arguments
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     * @param arguments arguments
     *
     * @return value of the method
     */
    public static Object getMethodValue(String className, String methodName,
        Class[] parameters, Object[] arguments)
    {
        Method method = getMethod(className, methodName, parameters);
        Object value  = null;

        try
        {
            value = method.invoke(getInstance(className), arguments);
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot get value of '" + methodName + "'");
        }

        return value;
    }

    /**
     * Executes a method but don't returns it's value
     *
     * @param className class name
     * @param methodName method name
     */
    public static void executeMethod(String className, String methodName)
    {
        executeMethod(className, methodName, new Class[] {  });
    }

    /**
     * Executes a method but don't returns it's value
     *
     * @param className class name
     * @param methodName method name
     * @param arguments arguments
     */
    public static void executeMethod(String className, String methodName,
        Object[] arguments)
    {
        executeMethod(className, methodName, new Class[] {  }, arguments);
    }

    /**
     * Executes a method but don't returns it's value
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     */
    public static void executeMethod(String className, String methodName,
        Class[] parameters)
    {
        executeMethod(className, methodName, parameters, new Object[] {  });
    }

    /**
     * Executes a method but don't returns it's value
     *
     * @param className class name
     * @param methodName method name
     * @param parameters parameters
     * @param arguments arguments
     */
    public static void executeMethod(String className, String methodName,
        Class[] parameters, Object[] arguments)
    {
        getMethodValue(className, methodName, parameters, arguments);
    }

    /**
     * Returns fields of a class
     *
     * @param className class name
     *
     * @return fields of class
     */
    public static Field[] getFields(String className)
    {
        return getClass(className).getFields();
    }

    /**
     * Returns field of a class according
     * to the fieldName
     *
     * @param className class name
     * @param fieldName field name
     *
     * @return field of the class named fieldName
     */
    public static Field getField(String className, String fieldName)
    {
        Field field = null;

        try
        {
            field = getClass(className).getField(fieldName);
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot get value of '" + fieldName + "'");
        }

        return field;
    }

    /**
     * Returns true if the field exists in
     * the class according to the class name
     *
     * @param className class name
     * @param fieldName field name
     *
     * @return true if the field exists
     */
    public static boolean isFieldExists(String className, String fieldName)
    {
        return (getField(className, fieldName) != null);
    }

    /**
     * Returns the value of the field in the
     * class according to the class name
     *
     * @param className class name
     * @param fieldName field name
     *
     * @return value of the field
     */
    public static Object getFieldValue(String className, String fieldName)
    {
        Field  field = getField(className, fieldName);
        Object value = null;

        try
        {
            value = field.get(getInstance(className));
        }
        catch (Exception ex)
        {
            _logger.fine("Cannot get value of '" + fieldName + "'");
        }

        return value;
    }
}
/*___oOo___*/
