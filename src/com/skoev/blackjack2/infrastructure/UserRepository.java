package com.skoev.blackjack2.infrastructure;

import com.skoev.blackjack2.model.account.User;

/**
 * Responsible for storing and retrieving user objects. The storage mechanism is up to the implementing class: it could be database, file system, or even in-memory.  
 */
public interface UserRepository {
	/**
	 * Retrieves a user object from storage. 
	 * @param username
	 * @return The user object for that username; null if not found.
	 * @throws ResourceException - If there was a problem with the underlying storage mechanism
	 */
	User getUser(String username);
	
	/**
	 * Saves a user object to storage. 
	 * @param user
	 * @throws ResourceException - If there was a problem with the underlying storage mechanism
	 */
	void saveUser(User user);
}
