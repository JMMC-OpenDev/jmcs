/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fest.common;

import fr.jmmc.jmcs.App;

/**
 * This utility class is useful to define the main class to launch the Swing application
 * @author bourgesl
 */
public final class JmcsApplicationSetup {

  /**
   * Define the application class to launch
   */
  static Class<? extends App> applicationClass = null;
  /**
   * Define the application class to launch
   */
  static String[] arguments = null;

  /**
   * Define the main class to launch the Swing application without arguments
   * @param appClass App subclass
   */
  public static void define(final Class<? extends App> appClass) {
    applicationClass = appClass;
  }

  /**
   * Define the main class to launch the Swing application with arguments
   * @param appClass App subclass
   * @param args main method arguments
   */
  public static void define(final Class<? extends App> appClass, final String... args) {
    applicationClass = appClass;
    arguments = args;
  }

  /**
   * Forbidden constructor (utility class)
   */
  private JmcsApplicationSetup() {
    super();
  }
}
