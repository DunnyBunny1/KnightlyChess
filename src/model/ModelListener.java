package model;

/**
 * Represents a listener for a chess model. When interesting events happen on the model, the model
 * broadcasts events to its listeners so that they can be notified.
 */
public interface ModelListener {
  /**
   * Notifies this model listener that a model that the model it listens to has been updated.
   * The specific model listener implementation can decide what to do when it has been updated.
   *
   * @param event the type of event that the model has been updated with
   */
  void notifyAfterModelUpdate(ModelEvent event);
}
