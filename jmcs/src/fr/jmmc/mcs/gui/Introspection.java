/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: Introspection.java,v 1.6 2008-09-02 12:31:00 lafrasse Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2008/09/01 11:07:45  lafrasse
 * Improved logging.
 *
 * Revision 1.4  2008/06/25 08:12:22  bcolucci
 * Add functions.
 *
 * Revision 1.3  2008/06/20 08:41:45  bcolucci
 * Remove unused imports and add class comments.
 *
 * Revision 1.2  2008/06/12 11:54:11  bcolucci
 * Add functions in order to simply execute a method without get it's value.
 *
 * Revision 1.1  2008/06/12 09:30:06  bcolucci
 * *** empty log message ***
 *
 ******************************************************************************/
package fr.jmmc.mcs.gui;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.logging.*;


/**
 * This class provides helper functions related to object introspection.
 *
 * For example, you can easily execute a method of a class given the path to the
 * seeked class. and the method name. Or you can also discover at runtime if a
 * field exists, retireve a method execution result, etc ...
 */
public class Introspection
{
    /** Logger */
    private static final Logger _logger = Logger.getLogger("fr.jmmc");

    /**
     * Returns a class object according to a given class path.
     *
     * @param classPath path to the seeked class.
     *
     * @return found class, null otherwise.
     */
    public static Class getClass(String classPath)
    {
        Class searchedClass = null;

        try
        {
            searchedClass = Class.forName(classPath);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot find class '" + classPath + "'",
                ex);
        }

        return searchedClass;
    }

    /**
     * Test whether a class exists, from a given class path.
     *
     * @param classPath path to the seeked class.
     *
     * @return true if the class exists, false otherwise.
     */
    public static boolean hasClass(String classPath)
    {
        if (getClass(classPath) != null)
        {
            _logger.fine("Found class '" + classPath + "'.");

            return true;
        }

        return false;
    }

    /**
     * Returns the package containing the class identified by the given class
     * path.
     *
     * @param classPath path to the seeked class.
     *
     * @return found class package, null otherwise.
     */
    public static Package getClassPackage(String classPath)
    {
        Package packageSearched = getClass(classPath).getPackage();

        return packageSearched;
    }

    /**
     * Returns the package containing the given class.
     *
     * @param seekedClass class.
     *
     * @return found class package, null otherwise.
     */
    public static Package getClassPackage(Class seekedClass)
    {
        return seekedClass.getPackage();
    }

    /**
     * Returns the name of the package containing the class identified by the
     * given class path.
     *
     * @param classPath path to the seeked class.
     *
     * @return found class package name, null otherwise.
     */
    public static String getClassPackageName(String classPath)
    {
        return getClassPackage(classPath).getName();
    }

    /**
     * Returns the name of the package containing the given class.
     *
     * @param seekedClass class.
     *
     * @return found class package name, null otherwise.
     */
    public static String getClassPackageName(Class seekedClass)
    {
        return seekedClass.getPackage().getName();
    }

    /**
     * Returns a new instance of the class identified by the gien class path.
     *
     * @param classPath path to the seeked class.
     *
     * @return new instance of the class, null otherwise.
     */
    public static Object getInstance(String classPath)
    {
        Object instance = null;

        try
        {
            instance = getClass(classPath).newInstance();
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot get instance of class '" + classPath + "'", ex);
        }

        return instance;
    }

    /**
     * Returns the list of methods of the class identified by the given class
     * path.
     *
     * @param classPath path to the seeked class.
     *
     * @return Array of found methods, null otherwise.
     */
    public static Method[] getMethods(String classPath)
    {
        return getClass(classPath).getMethods();
    }

    /**
     * Returns the seeked method with EMPTY argument list in the class
     * identified by the given class path.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     *
     * @return seeked method in class, null otherwise.
     */
    public static Method getMethod(String classPath, String methodName)
    {
        return getMethod(classPath, methodName, new Class[] {  });
    }

    /**
     * Returns the seeked method with given argument list in the class
     * identified by the given class path.
     *
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters seeked method parameter Class array.
     *
     * @return seeked method with parameters in class, null otherwise.
     */
    public static Method getMethod(String classPath, String methodName,
        Class[] parameters)
    {
        Method methodSearched = null;

        try
        {
            methodSearched = getClass(classPath)
                                 .getMethod(methodName, parameters);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot find method '" + methodName + "'", ex);
        }

        return methodSearched;
    }

    /**
     * Test whether a method exists, from a given class path.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     *
     * @return true if the method exists, false otherwise.
     */
    public static boolean hasMethod(String classPath, String methodName)
    {
        return hasMethod(classPath, methodName, new Class[] {  });
    }

    /**
     * Test whether a method with guven parameters list exists, from a given
     * class path.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters seeked parameters array.
     *
     * @return true if the method exists, false otherwise.
     */
    public static boolean hasMethod(String classPath, String methodName,
        Class[] parameters)
    {
        if (getMethod(classPath, methodName, parameters) != null)
        {
            _logger.fine("Found method '" + methodName + "' in class '" +
                classPath + "'.");

            return true;
        }

        return false;
    }

    /**
     * Returns the execution result of the method in the class identified by
     * their own names.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     *
     * @return result of the method execution, null otherwise.
     */
    public static Object getMethodValue(String classPath, String methodName)
    {
        return getMethodValue(classPath, methodName, new Class[] {  });
    }

    /**
     * Returns the execution result of the method with given parameters in the
     * class identified by their own names.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters parameters array.
     *
     * @return result of the method execution, null otherwise.
     */
    public static Object getMethodValue(String classPath, String methodName,
        Class[] parameters)
    {
        return getMethodValue(classPath, methodName, parameters,
            new Object[] {  });
    }

    /**
     * Returns the execution result of the method with given parameters and
     * argument values in the class identified by their own names.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters parameters array.
     * @param arguments arguments array.
     *
     * @return result of the method execution, null otherwise.
     */
    public static Object getMethodValue(String classPath, String methodName,
        Class[] parameters, Object[] arguments)
    {
        Method method = getMethod(classPath, methodName, parameters);
        Object value  = null;

        try
        {
            value = method.invoke(getInstance(classPath), arguments);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot get result of method '" + methodName + "'", ex);
        }

        return value;
    }

    /**
     * Execute a method in the class identified by their own names, without
     * returning the result.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     */
    public static void executeMethod(String classPath, String methodName)
    {
        executeMethod(classPath, methodName, new Class[] {  });
    }

    /**
     * Execute a method with given parameters in the class identified by their
     * own names, without returning the result.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters parameters array.
     */
    public static void executeMethod(String classPath, String methodName,
        Class[] parameters)
    {
        executeMethod(classPath, methodName, parameters, new Object[] {  });
    }

    /**
     * Execute a method with given parameters and argument values in the class
     * identified by their own names, without returning the result.
     *
     * @param classPath path to the seeked class.
     * @param methodName seeked method name.
     * @param parameters parameters array.
     * @param arguments arguments array.
     */
    public static void executeMethod(String classPath, String methodName,
        Class[] parameters, Object[] arguments)
    {
        getMethodValue(classPath, methodName, parameters, arguments);
    }

    /**
     * Returns the list of fields of the class identified by the given class
     * path.
     *
     * @param classPath path to the seeked class.
     *
     * @return Array of found fields, null otherwise.
     */
    public static Field[] getFields(String classPath)
    {
        return getClass(classPath).getFields();
    }

    /**
     * Returns the seeked field in the class identified by the given class path.
     *
     * @param classPath path to the seeked class.
     * @param fieldName seeked field name.
     *
     * @return seeked field in class, null otherwise.
     */
    public static Field getField(String classPath, String fieldName)
    {
        Field field = null;

        try
        {
            field = getClass(classPath).getField(fieldName);
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING, "Cannot get field '" + fieldName + "'",
                ex);
        }

        return field;
    }

    /**
     * Test whether a field exists, from a given class path.
     *
     * @param classPath path to the seeked class.
     * @param fieldName seeked field name.
     *
     * @return true if the field exists, false otherwise.
     */
    public static boolean hasField(String classPath, String fieldName)
    {
        if (getField(classPath, fieldName) != null)
        {
            _logger.fine("Found field '" + fieldName + "' in class '" +
                classPath + "'.");

            return true;
        }

        return false;
    }

    /**
     * Returns the value of the field in the class identified by the given class
     * path.
     *
     * @param classPath seeked class name.
     * @param fieldName seeked field name.
     *
     * @return value of the seeked field, null otherwise.
     */
    public static Object getFieldValue(String classPath, String fieldName)
    {
        Field  field = getField(classPath, fieldName);
        Object value = null;

        try
        {
            value = field.get(getInstance(classPath));
        }
        catch (Exception ex)
        {
            _logger.log(Level.WARNING,
                "Cannot get value of field '" + fieldName + "'", ex);
        }

        return value;
    }
}
/*___oOo___*/
