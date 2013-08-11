package com.skoev.blackjack2.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.io.*;

import com.skoev.blackjack2.model.account.User;

/**
 * Implements a very simple repository in the file system using object serialization. This repository is persistent, so data
 * is preserved when the application is restarted. However, this repository lacks most of the features that a 
 * database would have, such as concurrency and transactions.   
 * @author stefan.t.koev
 *
 */
public class UserRepositoryInFile implements UserRepository {

	private static UserRepositoryInFile instance = null;
	private static final String DIRNAME = System.getProperty("user.home")  + File.separator + "BlackjackSim";
	private static final String FILENAME = "data"; 
	private Map<String, User> users;
	
	
	private UserRepositoryInFile() {
		ObjectInputStream ois = null;
		try{
			try{
				File dir = new File(DIRNAME);
				if(!dir.exists()){
					dir.mkdirs();	
				}
				File file = new File(dir, FILENAME);
				if (!file.exists()){
					file.createNewFile();
				}
				
				if(file.length() > 0){
					InputStream is = new FileInputStream(file);
					ois = new ObjectInputStream(is);
					users = (Map<String, User>) ois.readObject();
					
				}
				else{
					users = new HashMap<String, User>();
				}
				
			}
			finally{
				if(ois != null){
					ois.close();
				}
			}
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
		
	}
	
	public static UserRepositoryInFile getInstance() {
		if(instance == null){
			instance = new UserRepositoryInFile(); 
		}
		return instance; 
	}	
	
	
	
	@Override
	public User getUser(String username) {
		return users.get(username);
	}

	@Override
	public void saveUser(User user) {
		users.put(user.getUsername(), user);
		ObjectOutputStream oos = null;
		try{
			try{
				File file = new File(DIRNAME, FILENAME);
				OutputStream os = new FileOutputStream(file, false);
				oos = new ObjectOutputStream(os);
				oos.writeObject(users);
				
			}
			finally{
				if(oos != null){
					oos.close();	
				}
			}
		}
		catch (Exception e){
			throw new RuntimeException(e);
		}
	}

}
