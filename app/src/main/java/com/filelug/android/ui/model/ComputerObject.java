package com.filelug.android.ui.model;

public class ComputerObject {

	private String userComputerId;
	private String userId;
	private int computerId;
	private String computerGroup;
	private String computerName;
	private String computerAdminId;

	public ComputerObject(String userComputerId, String userId, int computerId,
						  String computerGroup, String computerName, String computerAdminId) {
		this.userComputerId = userComputerId;
		this.userId = userId;
		this.computerId = computerId;
		this.computerGroup = computerGroup;
		this.computerName = computerName;
		this.computerAdminId = computerAdminId;
	}

	public String getUserComputerId() {
		return userComputerId;
	}

	public void setUserComputerId(String userComputerId) {
		this.userComputerId = userComputerId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getComputerId() {
		return computerId;
	}

	public void setComputerId(int computerId) {
		this.computerId = computerId;
	}

	public String getComputerGroup() {
		return computerGroup;
	}

	public void setComputerGroup(String computerGroup) {
		this.computerGroup = computerGroup;
	}

	public String getComputerName() {
		return computerName;
	}

	public void setComputerName(String computerName) {
		this.computerName = computerName;
	}

	public String getComputerAdminId() {
		return computerAdminId;
	}

	public void setComputerAdminId(String computerAdminId) {
		this.computerAdminId = computerAdminId;
	}

	@Override
	public String toString() {
		return this.computerName;
	}

}
