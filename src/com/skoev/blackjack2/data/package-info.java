/**
 * This packages contains classes for data access. Each {@link com.skoev.blackjack2.common.AggregateRoot} should have its own data access class called a repository. 
 *  
 * Entities within aggregate roots and value objects should be accessed through their aggregate root. As much as possible, they should not be accessed
 * directly because that violates encapsulation.  
 *   
 * @author Stefan Koev
 */
package com.skoev.blackjack2.data;