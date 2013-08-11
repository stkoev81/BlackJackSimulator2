package com.skoev.blackjack2.common;

/**
 * Marker interface for aggregate roots. An aggregate root is an essential
 * entity with global identity and is responsible for accessing and mutating other entities and value objects within it. The 
 * entities and value objects within it should only be mutated by methods of the aggregate root. If this practice is 
 * followed, it is easier to enforce invariants within the root.
 *   
 * @author stefan.t.koev
 *
 */
public interface AggregateRoot extends Entity {

}
