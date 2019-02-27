# UsersAndGroups Sample App

This sample illustrates how to call API BaaS Android SDK methods for working with user and group entities in an API BaaS application. The sample illustrates:

- How to add users and groups to the application.
- How to list users and groups.
- How to add users to a group.

Import this project in Eclipse / Android Developer Tools to get started.

1. Open ADT, do File > Import.
1. Under “Android”, select “Existing Android Code Into Workspace”, and click Next.
1. Pick the folder containing this README file as the root directory.
1. Eclipse should find a project called UsersAndGroups automatically. Just click Finish!

In order for this sample to work as is, you will need to:

- Change the ORGNAME value in UsersAndGroupsHomeActivity.java to your app services organization name.
- Ensure that the app services application you're using provides full permission for user and group entities. Your sandbox is best. This sample does not feature authentication.
