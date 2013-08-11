package com.skoev.blackjack2.common;

import java.io.Serializable;

/** 
 * Marker interface for entities. An entity is a model object that has identity. Identity really means that a change to the object's value 
 * will be propagated to all places that the object is used. 
 * 
 * The identity can be local (object is identifiable only within another entity) or global (object is identifiable within the entire application). 
 * @author stefan.t.koev
 *
 */
public interface Entity extends Serializable {

}
