# UsersAndGroups Sample App

This sample illustrates how to call API BaaS Android SDK methods for working with user and group entities in an API BaaS application. The sample illustrates:

- How to add users and groups to the application.
- How to list users and groups.
- How to add users to a group.

File | Description
---- | -----------
[AddUserToGroupActivity.java](src/com/apigee/sample/usersandgroups/AddUserToGroupActivity.java) | Adds a user to a group.
[GroupsListViewActivity.java](src/com/apigee/sample/usersandgroups/GroupsListViewActivity.java) | Activity code behind the group list.
[NewGroupActivity.java](src/com/apigee/sample/usersandgroups/NewGroupActivity.java) | Adds a new user group.
[NewUserActivity.java](src/com/apigee/sample/usersandgroups/NewUserActivity.java) | Adds a new user.
[UsersAndGroupsApplication.java](src/com/apigee/sample/usersandgroups/UsersAndGroupsApplication.java) | Creates instances of ApigeeClient and DataClient SDK objects for global use.
[UsersAndGroupsHomeActivity.java](src/com/apigee/sample/usersandgroups/UsersAndGroupsHomeActivity.java) | Main activity for the app. This contains logic behind the app's home page and  initializes the ApigeeClient object that's used in other activity classes.
[UsersListViewActivity.java](src/com/apigee/sample/usersandgroups/UsersListViewActivity.java) | Lists users.