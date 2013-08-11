package com.skoev.blackjack2.common;

import java.io.Serializable;

/**
 * Marker interface for value objects. A value object is a model object without identity. That is, two object with the same attribute values are 
 * interchangeable. Also, changes to one value object should not propagate to all places where the object is used.  
 * 
 * Ideally, a value object should be immutable; this allows value objects to be safely shared. If a value object is not immutable, 
 * then it should not be shared between multiple client objects and each client object should have its own copy.
 *  
 * @author stefan.t.koev
 *
 */
public interface ValueObject extends Serializable{

}
