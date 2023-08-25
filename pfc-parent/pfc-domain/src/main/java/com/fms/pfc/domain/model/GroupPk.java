package com.fms.pfc.domain.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Id;

public class GroupPk implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String orgId;
    private String groupId;

    public GroupPk() {
    }

    public GroupPk(String orgId, String groupId) {
        this.orgId = orgId;
        this.groupId = groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupPk groupPk = (GroupPk) o;
        return orgId.equals(groupPk.orgId) &&
        		groupId.equals(groupPk.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId, groupId);
    }
    
    public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}
