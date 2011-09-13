/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.jmal.model;

import fr.jmmc.jmal.model.AbstractModelFunction.ModelVariant;
import fr.jmmc.jmal.model.function.CircleModelFunction;
import fr.jmmc.jmal.model.function.DiskModelFunction;
import fr.jmmc.jmal.model.function.GaussianModelFunction;
import fr.jmmc.jmal.model.function.LDDiskModelFunction;
import fr.jmmc.jmal.model.function.PunctModelFunction;
import fr.jmmc.jmal.model.function.RingModelFunction;
import fr.jmmc.jmal.model.targetmodel.Model;
import fr.jmmc.jmal.model.targetmodel.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import org.apache.commons.math.complex.Complex;

/**
 * This class constitutes the main interface to target models (supported models, new model, computeModels)
 * 
 * @author Laurent BOURGES.
 */
public final class ModelManager {

  /** Class logger */
  private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ModelManager.class.getName());
  /** singleton pattern */
  private static final ModelManager instance = new ModelManager();
  // members :
  /** List of model type */
  private final Vector<String> modelTypes = new Vector<String>();
  /** Map : model type, ModelFunction instance */
  private final Map<String, ModelFunction> modelFunctions = new HashMap<String, ModelFunction>();

  /**
   * Return the ModelManager singleton
   * @return ModelManager singleton
   */
  public static ModelManager getInstance() {
    return instance;
  }

  /**
   * Constructor
   */
  private ModelManager() {
    super();
    this.registerFunctions();
  }

  /**
   * Register model functions
   */
  private void registerFunctions() {
    // 0 - background
    // TODO : background model

    // 1 - Punct Model :
    this.addFunction(new PunctModelFunction());
    // 2 - Disk Models :
    this.addFunction(new DiskModelFunction());
    // 2.1 Elongated Disk Model :
    this.addFunction(new DiskModelFunction(ModelVariant.Elongated));
    // 2.2 Flattened Disk Model :
    this.addFunction(new DiskModelFunction(ModelVariant.Flattened));
    // 3 - Circle Model (unresolved ring) :
    this.addFunction(new CircleModelFunction());
    // 3.1 - Ring Models :
    this.addFunction(new RingModelFunction());
    // 3.2 Elongated Ring Model :
    this.addFunction(new RingModelFunction(ModelVariant.Elongated));
    // 3.3 Flattened Ring Model :
    this.addFunction(new RingModelFunction(ModelVariant.Flattened));
    // 4 - Gaussian Models :
    this.addFunction(new GaussianModelFunction());
    // 4.1 Elongated Gaussian Model :
    this.addFunction(new GaussianModelFunction(ModelVariant.Elongated));
    // 4.2 Flattened Gaussian Model :
    this.addFunction(new GaussianModelFunction(ModelVariant.Flattened));
    // 5 - Limb darkened disk Models :
    this.addFunction(new LDDiskModelFunction());

    if (logger.isLoggable(Level.FINE)) {
      logger.fine("functions : " + modelFunctions);
    }
  }

  /**
   * Add the given function
   * @param mf function to add
   */
  private void addFunction(final ModelFunction mf) {
    final String type = mf.getType();
    this.modelFunctions.put(type, mf);
    this.modelTypes.add(type);
  }

  /**
   * Return the list of model types
   * @return list of model types
   */
  public Vector<String> getSupportedModels() {
    return this.modelTypes;
  }

  /**
   * Return the model description of the given type
   * @param type type of the model
   * @return model description
   * @throws IllegalStateException if the given type is unknown
   */
  public String getModelDescription(final String type) {
    return getModelFunction(type).getDescription();
  }

  /**
   * Return a new model of the given type
   * @param type type of the model
   * @return new model
   * @throws IllegalStateException if the given type is unknown
   */
  public Model createModel(final String type) {
    return getModelFunction(type).newModel();
  }

  /**
   * Return the model function for the given type
   * @param type type of the model
   * @return model function
   * @throws IllegalStateException if the given type is unknown
   */
  private ModelFunction getModelFunction(final String type) throws IllegalStateException {
    final ModelFunction mf = this.modelFunctions.get(type);
    if (mf == null) {
      throw new IllegalStateException("no model function registered for the type = " + type);
    }
    return mf;
  }

  /**
   * Validate the given models i.e. all parameter values are valid
   * @param models list of models to compute
   * @throws IllegalArgumentException if a parameter value is invalid !
   */
  public void validateModels(final List<Model> models) throws IllegalArgumentException {
    if (models != null && !models.isEmpty()) {
      ModelFunction mf;
      for (Model model : models) {
        mf = getModelFunction(model.getType());

        // check model parameters :
        mf.validate(model);
      }
    }
  }

  /**
   * Compute the complex visiblity of given models for the given Ufreq and Vfreq arrays
   * 
   * @param ufreq U frequencies in rad-1
   * @param vfreq V frequencies in rad-1
   * @param models list of models to compute
   * @return normalized complex visibility or null if thread interrupted
   * @throws IllegalArgumentException if a parameter value is invalid !
   */
  public Complex[] computeModels(final double[] ufreq, final double[] vfreq, final List<Model> models) throws IllegalArgumentException {
    Complex[] vis = null;

    if (ufreq != null && vfreq != null && models != null && !models.isEmpty()) {

      /** Get the current thread to check if the computation is interrupted */
      final Thread currentThread = Thread.currentThread();

      vis = new Complex[ufreq.length];

      // initialize the visiblity array with the Complex Zero (immutable) :
      Arrays.fill(vis, Complex.ZERO);

      // fast interrupt :
      if (currentThread.isInterrupted()) {
        return null;
      }

      // For now : no composite model supported (hierarchy) !
      ModelFunction mf;
      for (Model model : models) {
        mf = getModelFunction(model.getType());

        // add the model contribution to the current visibility array :
        mf.compute(ufreq, vfreq, model, vis);

        // fast interrupt :
        if (currentThread.isInterrupted()) {
          return null;
        }
      }
    }

    return vis;
  }

  /**
   * Normalize the fluxes and update the model parameters
   * @param models list of models to update
   */
  public static final void normalizeFluxes(final List<Model> models) {
    double totalFlux = 0d;

    Parameter parameter;
    for (Model model : models) {
      parameter = model.getParameter(ModelDefinition.PARAM_FLUX_WEIGHT);

      if (parameter == null) {
        throw new IllegalArgumentException("parameter [" + ModelDefinition.PARAM_FLUX_WEIGHT + "] not found in the model [" + model.getName() + "] !");
      }

      totalFlux += parameter.getValue();
    }

    if (totalFlux != 0d && totalFlux != 1d) {
      if (logger.isLoggable(Level.FINE)) {
        logger.fine("totalFlux = " + totalFlux);
      }

      for (Model model : models) {
        parameter = model.getParameter(ModelDefinition.PARAM_FLUX_WEIGHT);

        // p is not null :
        parameter.setValue(parameter.getValue() / totalFlux);
      }
    }
  }

  /**
   * Set the parameter value
   * @param model model to use
   * @param type type of the parameter
   * @param value value to set
   * @throws IllegalArgumentException if the parameter type is invalid for the given model
   */
  public static void setParameterValue(final Model model, final String type, final double value) {
    model.getParameter(type).setValue(value);
  }

  /**
   * Reset editable fields for the given parameter
   * @param parameter parameter to reset
   */
  public static void resetParameter(final Parameter parameter) {
    parameter.setValue(0D);
    parameter.setMinValue(null);
    parameter.setMaxValue(null);
    parameter.setScale(null);
    parameter.setHasFixedValue(false);
  }

  /**
   * Create a new model for the given type and define model and parameter names
   * @param type type of the model
   * @param targetModels existing target models
   * @return model new model
   */
  public Model newModel(final String type, final List<Model> targetModels) {

    final Model newModel = createModel(type);

    // generate an unique identifier for the new model :
    final int modelIdx = generateModelUniqueName(newModel, targetModels);

    // Update parameter names to be unique :
    for (Parameter parameter : newModel.getParameters()) {
      parameter.setName(parameter.getType() + modelIdx);
    }

    // First Model Rules :
    final boolean isFirst = targetModels.isEmpty();

    if (isFirst) {
      final Parameter paramRefX = newModel.getParameter(ModelDefinition.PARAM_X);
      final Parameter paramRefY = newModel.getParameter(ModelDefinition.PARAM_Y);

      // zero by default
      paramRefX.setHasFixedValue(true);
      paramRefY.setHasFixedValue(true);
    }

    return newModel;
  }

  /**
   * Create a new model for the given type and define model and parameter names replacing the given current model
   * @param type type of the model
   * @param currentModel model to replace
   * @param targetModels existing target models
   * @return model new model
   */
  public Model replaceModel(final String type, final Model currentModel, final List<Model> targetModels) {

    final Model newModel = createModel(type);

    // retrieve the unique identifier of the previous model if possible :
    int modelIdx = parseModelUniqueIndex(currentModel);
    if (modelIdx == 0) {
      modelIdx = generateModelUniqueName(newModel, targetModels, currentModel);
    } else {
      newModel.setName(newModel.getType() + modelIdx);
    }

    // Update parameter names to be unique :
    for (Parameter parameter : newModel.getParameters()) {
      parameter.setName(parameter.getType() + modelIdx);

      // try to recover previous parameters :
      for (Parameter oldParameter : currentModel.getParameters()) {

        if (matchType(parameter.getType(), oldParameter.getType())) {
          parameter.setValue(oldParameter.getValue());
          parameter.setMinValue(oldParameter.getMinValue());
          parameter.setMaxValue(oldParameter.getMaxValue());
          parameter.setScale(oldParameter.getScale());
          parameter.setHasFixedValue(oldParameter.isHasFixedValue());
        }
      }
    }

    // retrieve shared parameters :
    newModel.getParameterLinks().addAll(currentModel.getParameterLinks());

    return newModel;
  }

  /**
   * Recenter the models using the first model to be at (0,0) in the given list of models
   * @param targetModels list of models
   */
  public static void relocateModels(final List<Model> targetModels) {
    final int size = targetModels.size();
    if (size == 0) {
      return;
    }

    // new reference :
    final Model refModel = targetModels.get(0);

    // First Model Rules :
    final Parameter paramRefX = refModel.getParameter(ModelDefinition.PARAM_X);
    final Parameter paramRefY = refModel.getParameter(ModelDefinition.PARAM_Y);

    if (size > 1) {
      final double refX = paramRefX.getValue();
      final double refY = paramRefY.getValue();

      logger.severe("relocate to [" + refX + ", " + refY + "]");

      Model model;
      Parameter paramX, paramY;
      for (int i = 0; i < size; i++) {
        model = targetModels.get(i);

        paramX = model.getParameter(ModelDefinition.PARAM_X);
        paramY = model.getParameter(ModelDefinition.PARAM_Y);

        // remove first model position :
        paramX.setValue(paramX.getValue() - refX);
        paramY.setValue(paramY.getValue() - refY);
      }
    }

    // reset to zero :
    resetParameter(paramRefX);
    paramRefX.setHasFixedValue(true);

    resetParameter(paramRefY);
    paramRefY.setHasFixedValue(true);
  }

  /**
   * try to tell if the data of the old parameter can be copied to new parameter
   * according to both names. If they ends with the same string after the '_'
   * character, then this method returns true.
   * @param oldParamType parameter type of the old parameter
   * @param newParamType parameter type of the new parameter
   * @return true if both string ends with same keyword, else returns false
   */
  private static boolean matchType(String oldParamType, String newParamType) {
    int idx;
    idx = oldParamType.lastIndexOf('_');
    if (idx != -1) {
      oldParamType = oldParamType.substring(idx + 1);
    }
    idx = newParamType.lastIndexOf('_');
    if (idx != -1) {
      newParamType = newParamType.substring(idx + 1);
    }
    return newParamType.equals(oldParamType) || newParamType.contains(oldParamType);
  }

  /**
   * Generate the unique identifier [model type + digit] like 'disk'1 ...
   * @param newModel given model
   * @param models list of existing models to check the new identifier
   * @return unique model index
   */
  public static int generateModelUniqueName(final Model newModel, final List<Model> models) {
    return generateModelUniqueName(newModel, models, null);
  }

  /**
   * Generate the unique identifier [model type + digit] like 'disk'1 to the given model ...
   * @param newModel given model
   * @param models list of existing models to check the new identifier
   * @param skipModel model to skip in the model traversal
   * @return unique model index
   */
  public static int generateModelUniqueName(final Model newModel, final List<Model> models, final Model skipModel) {
    int prevIdx = 0;

    for (Model model : models) {
      if (model != skipModel) {
        prevIdx = findModelMaxUniqueIndex(model, prevIdx, skipModel);
      }
    }

    final int idx = prevIdx + 1;

    if (logger.isLoggable(Level.FINEST)) {
      logger.finest("new model index = " + idx);
    }

    newModel.setName(newModel.getType() + idx);

    return idx;
  }

  /**
   * Return the maximum value of the model unique index found recursively using the given model and child models
   * @param model model to traverse
   * @param prevIdx current maximum value of the model unique index
   * @param skipModel model to skip in the model traversal
   * @return maximum value of the model unique index
   */
  private static int findModelMaxUniqueIndex(final Model model, final int prevIdx, final Model skipModel) {
    // recompute unique index from model name :
    final int modelIdx = parseModelUniqueIndex(model);

    int idx = Math.max(prevIdx, modelIdx);

    for (Model childModel : model.getModels()) {
      if (childModel != skipModel) {
        idx = findModelMaxUniqueIndex(childModel, idx, skipModel);
      }
    }
    return idx;
  }

  /**
   * Return the model unique index from its name parsing [model type + digit] like 'disk'1 ...
   * @param model model to use
   * @return model unique index
   */
  public static int parseModelUniqueIndex(final Model model) {
    final String idx = model.getName().substring(model.getType().length());

    int index = 0;
    if (idx.length() > 0) {
      try {
        index = Integer.parseInt(idx);
      } catch (NumberFormatException nfe) {
        logger.log(Level.SEVERE, "model id parsing failure : ", nfe);
      }
    }

    return index;
  }
}
