package com.skoev.blackjack2.model.account;

/**
 * The responsibility is to retrieve and create users. Not to check for rules regarding creation of new users. That is done in service. 
 */
public interface UserRepository {
	User getUser(String username);
	void saveUser(User user);
}
