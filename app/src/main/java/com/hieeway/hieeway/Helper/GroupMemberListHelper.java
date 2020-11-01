package com.hieeway.hieeway.Helper;

import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.GroupMember;
import com.hieeway.hieeway.Model.Recipient;

import java.util.List;

public class GroupMemberListHelper {

    private List<Friend> groupMemberList = null;
    public static final GroupMemberListHelper instance = new GroupMemberListHelper();


    public GroupMemberListHelper() {
    }

    public static GroupMemberListHelper getInstance() {
        return instance;
    }


    public List<Friend> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<Friend> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }


}

