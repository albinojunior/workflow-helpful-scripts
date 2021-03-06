//Create i user with i role, 1 blog per user, assign 1 task per user and complete it

import com.liferay.portal.kernel.dao.orm.*
import com.liferay.portal.kernel.util.*
import com.liferay.portal.kernel.workflow.*
import com.liferay.portal.kernel.model.*
import java.util.*

companyId = com.liferay.portal.kernel.util.PortalUtil.getCompanyId(actionRequest)
userId = com.liferay.portal.kernel.util.PortalUtil.getUserId(actionRequest)
group = com.liferay.portal.kernel.service.GroupLocalServiceUtil.getGroup(companyId, "Guest");
groupId = group.getGroupId();
serviceContext = new com.liferay.portal.kernel.service.ServiceContext();
serviceContext.setCompanyId(companyId)
serviceContext.setScopeGroupId(group.getGroupId())
serviceContext.setUserId(userId)
Stack<User> users = new Stack<>()

for (i =0; i < 9; i++) {
   boolean autoPassword = true;
   String password = "123";
   String screenName = i + "test";
   String emailAddress = i + "test@liferay.com";
   long facebookId = 0;
   String openId = "";
   String firstName = i + "test";
   String middleName = "";
   String lastName = "test" + i;
   long prefixId = 0;
   long suffixId = 0;
   boolean male = true;
   int birthdayMonth = 1;
   int birthdayDay = 1;
   int birthdayYear = 1970;
   String jobTitle = "";
   long[] organizationIds = null;
   long[] roleIds = null;
   long[] userGroupIds = null;
   boolean sendMail = false;
   long[] groudIds = [groupId];
  //create a user
   groupUser = com.liferay.portal.kernel.service.UserLocalServiceUtil.addUser(
       0L, companyId, autoPassword, password, password,
       false, screenName, emailAddress, facebookId,
       openId, java.util.Locale.US, firstName, middleName, lastName, prefixId, suffixId,
       male, birthdayMonth, birthdayDay, birthdayYear, jobTitle, groudIds,
       organizationIds, roleIds, userGroupIds, sendMail, serviceContext);
  users.push(groupUser);
   role = com.liferay.portal.kernel.service.RoleLocalServiceUtil.getRole(companyId, "Administrator");
  //assign admin role to new user
  long [] userIds = [groupUser.getUserId()];
  com.liferay.portal.kernel.service.UserLocalServiceUtil.addRoleUser(role.getRoleId(), groupUser);
  com.liferay.portal.kernel.service.UserGroupRoleLocalServiceUtil.addUserGroupRoles(userIds, groupId, role.getRoleId());
  //create a new Role and assign it to created user
  java.util.Map<java.util.Locale, String> titleMap = new java.util.HashMap<java.util.Locale, String>();
  titleMap.put(java.util.Locale.US, i + "Role");
  com.liferay.portal.kernel.model.Role newRole = com.liferay.portal.kernel.service.RoleLocalServiceUtil.addRole(userId, null, 0, i + "Role", titleMap, titleMap, 1, null, null);
  com.liferay.portal.kernel.service.UserLocalServiceUtil.addRoleUser(newRole.getRoleId(), groupUser);
  // add a Blog
  com.liferay.blogs.service.BlogsEntryLocalServiceUtil.addEntry(userId, "Blog " + i, "Blog "+ i, serviceContext)
}

List<WorkflowTask> workflowTasks = new ArrayList<>()
workflowTasks.addAll(
    WorkflowTaskManagerUtil.getWorkflowTasksByUserRoles(
        companyId, userId, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null))
workflowTasks.addAll(
    WorkflowTaskManagerUtil.getWorkflowTasksByUser(
        companyId, userId, false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null))

for (WorkflowTask workflowTask : workflowTasks) {
    if (users.isEmpty()) {
      break;
    }
    User assignee = users.pop();
    WorkflowTaskManagerUtil.assignWorkflowTaskToUser(
        companyId, userId, workflowTask.getWorkflowTaskId(), assignee.getUserId(), StringPool.BLANK, null, null);
    WorkflowTaskManagerUtil.completeWorkflowTask(
        companyId, assignee.getUserId(), workflowTask.getWorkflowTaskId(), Constants.APPROVE, StringPool.BLANK, null);
}