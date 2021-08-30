# IDE Dev Kit Samples

These samples were included in an SDK to support developers who were augmenting the WebLogic Workshop IDE. (For my overview of the IDE, see [About WebLogic Workshop](../../weblogic-workshop-book/about-weblogic-workshop.md)).

Each sample illustrates how to use a different part of the SDK API. 

I developed the SDK in 2004 while working as a technical writer for BEA Systems. BEA was later acquired by Oracle. For documentation content from the SDK, see the [Extension Development Kit PDF](../ExtensionDevelopmentKit.pdf).

Folder | Description
------ | -----------
[CustomProject](CustomProject) | Illustrates how to add support for a project that includes files whose extension is PHP (a project type that WebLogic Workshop didn't support at the time.)
[DragDropSimple](DragDropSimple) | Illustrates how to support the IDE's drag/drop functionality between IDE windows. The sample illustrates this with a tree view of static data; you can drag nodes from the tree to a document open in the IDE's Source View to copy information from the tree to the source.
[FrameViewSimple](FrameViewSimple) | Illustrates how to create a dockable window.
[MenuItems](MenuItems) | Illustrates how to add a Favorites menu whose submenus allow the user to add and remove URLs as favorites; clicking a URL in the menu opens a browser to that URL's location.
[PopupAction](PopupAction) | Illustrates how to add a simple popup action -- an Upload popup menu through which you can use FTP to upload a file or project to a site specified in project preferences.
[PropertyListener](PropertyListener) | Illustrates how to keep informed of changes in the IDE's state -- including the files it contains, what has focus, and so on. registers to listen for IDE events -- by listening for events.
[SearchResults](SearchResults) | Illustrates how to add a dockable frame through which users can search WebLogic Workshop documentation.
[ToolbarButton](ToolbarButton) | Illustrates how to add a simple toolbar button action that launches a browser to a particular URL.
