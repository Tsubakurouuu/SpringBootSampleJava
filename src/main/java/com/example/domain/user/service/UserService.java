package com.example.domain.user.service;

import java.util.List;

import com.example.domain.user.model.MUser;


public interface UserService {
	
	/*ユーザー登録*/
	public void signup(MUser user);
	
	/*ユーザー取得*/
	public List<MUser> getUsers();
	
	/*ユーザー１件取得*/
	public MUser getUserOne(String userId);
	
	/*ユーザー１件更新*/
	public void updateUserOne(String userId, String password, String userName);
	
	public void deleteUserOne(String userId);
}
